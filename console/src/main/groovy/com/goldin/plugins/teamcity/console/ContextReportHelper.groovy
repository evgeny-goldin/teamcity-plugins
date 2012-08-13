package com.goldin.plugins.teamcity.console

import jetbrains.buildServer.serverSide.BuildServerAdapter
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.serverSide.ServerPaths
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

import java.lang.reflect.Method


/**
 * Various helper methods.
 */
final class ContextReportHelper extends BuildServerAdapter
{
    final Set<String> apiClasses = ContextReportHelper.getResource( '/open-api-classes.txt' ).getText( 'UTF-8' ).
                                   readLines()*.trim().grep().toSet().asImmutable()


    @Autowired private SBuildServer       server
    @Autowired private ServerPaths        paths
    @Autowired private ApplicationContext context

    List<List<?>> contextReport // Initialized upon TeamCity serverStartup()


    ContextReportHelper ( SBuildServer server )
    {
        server.addListener( this )
    }


    @Override
    void serverStartup()
    {
        contextReport = createContextReport().asImmutable()
    }


    /**
     * Retrieves context report tables.
     *
     * @return context report tables, every element in list returned is a 4-elements list:
     *  - table title
     *  - left column header
     *  - right column header
     *  - data table
     */
    private List<List<?>> createContextReport ()
    {
        final tables = []
        tables << [ javadocHtmlLink( SBuildServer, false ), 'Method Name', 'Value Returned', serverTable( server )]
        tables << [ javadocHtmlLink( ServerPaths,  false ), 'Method Name', 'Value Returned', pathsTable ( paths  )]

        for ( ApplicationContext c = context; c; c = c.parent )
        {
            final table = contextTable( c )
            tables << [ "${ c == context ? 'Plugin' : 'Parent' } Spring Context - ${ table.size() } bean${ table.size() == 1 ? '' : 's' }<p/>" +
                        "<code style='font-size: 82%'>${ context.toString() }</code>",
                        'Bean Class', 'Bean Name',
                        table ]
        }

        tables
    }


    /**
     * Retrieves {@link jetbrains.buildServer.serverSide.SBuildServer} report table.
     * @return {@link jetbrains.buildServer.serverSide.SBuildServer} report table
     */
    private Map<String, ?> serverTable( SBuildServer server )
    {
        propertiesMap(
            server,
            'fullServerVersion serverRootPath'.tokenize())
    }


    /**
     * Retrieves {@link jetbrains.buildServer.serverSide.ServerPaths} report table.
     * @return {@link jetbrains.buildServer.serverSide.ServerPaths} report table
     */
    private Map<String, ?> pathsTable ( ServerPaths paths )
    {
        propertiesMap (
            paths,
            'dataDirectory artifactsDirectory backupDir cachesDir configDir libDir logsPath pluginDataDirectory pluginsDir systemDir'.tokenize()) {
            Object o -> (( o instanceof File ) ? o : new File( o.toString())).canonicalPath.replace( '\\', '/' )
        }
    }


    /**
     * Retrieves {@link org.springframework.context.ApplicationContext} report table.
     * @param context       context to read the data from
     * @param allApiClasses Open API list of classes available as public Javadoc
     * @return {@link org.springframework.context.ApplicationContext} report table
     */
    private Map<String, ?> contextTable( ApplicationContext context )
    {
        assert context

        context.getBeanNamesForType( Object ).sort().inject([:]){
            Map m, String beanName ->
            //noinspection GroovyGetterCallCanBePropertyAccess
            final beanClass  = context.getBean( beanName ).getClass()
            final beanTitle  = beanClass.name in apiClasses ? javadocHtmlLink( beanClass ) : shorten( beanClass.name )
            final apiClasses = parentClasses( beanClass ).findAll{ it.name in apiClasses }

            if ( apiClasses )
            {
                beanTitle += ":<br/>- ${ apiClasses.collect{ javadocHtmlLink( it )}.join( '<br/>- ') }"
            }

            m[ beanTitle ] = ( beanName.size() > 70 ? beanName[ 0 .. 70 ] + '..' : beanName )
            m
        }
    }


    /**
     * Shortens class name.
     * @param className class name to make shorter
     * @return class name made shorter
     */
    private shorten( String className )
    {
        assert className
        className.startsWith( 'jetbrains.buildServer.' ) ? "j.b.${ className[ 'jetbrains.buildServer.'.length() .. -1 ]}" :
                                                           className
    }


    /**
     * Constructs a link to an Open API class Javadoc.
     *
     * @param c class to construct the link for
     * @return  link to class Javadoc
     */
    final String javadocLink ( Class c )
    {
        assert ( c && ( c.name in apiClasses )), "Class [$c.name] is not part of an Open API"
        "http://javadoc.jetbrains.net/teamcity/openapi/current/${ c.name.replace( '.', '/' )}.html"
    }


    /**
     * Constructs an html link to an Open API class Javadoc.
     *
     * @param c           class to construct the link for
     * @param useFullName whether full class name or simple name should be used as link title
     * @return            HTML link to class Javadoc
     */
    private String javadocHtmlLink ( Class c, boolean useFullName = true )
    {
        assert ( c && ( c.name in apiClasses )), "Class [$c.name] is not part of an Open API"
        "<a href='${ javadocLink( c )}'>${ useFullName ? shorten( c.name ) : c.simpleName }</a>"
    }


    /**
     * Creates a {@link Map} of properties of the object specified.
     *
     * @param o             object to read its properties
     * @param propertyNames names of properties to read
     * @param transformer   closure transforming property value
     * @return {@link Map} of properties of the object specified
     */
    private Map<String,?> propertiesMap( Object o, List<String> propertyNames, Closure transformer = null )
    {
        assert o && propertyNames

        final Map<String,?>      map     = [:]
        //noinspection GroovyGetterCallCanBePropertyAccess
        final Collection<Method> methods = o.class.methods

        for ( propertyName in propertyNames )
        {
            final String methodName = "get${ propertyName.capitalize()}".toString()
            final Method method     = methods.find { it.name == methodName }
            assert method, "Object [$o] of class [${ o.class.name }] doesn't have method [$methodName]"

            final  value = method.invoke( o )
            assert value != null, "Method [$methodName] of object's [$o] returned null"

            map[ "$methodName()" ] = ( transformer ? transformer( value ) : value )
        }

        map
    }


    /**
     * Retrieves all parent classes and interfaces of the class specified.
     *
     * @param c class to retrieve its parent classes
     * @return all parent classes and interfaces of the class specified
     */
    final Set<Class> parentClasses( Class c )
    {
        assert c

        final classes = [] as Set

        c.interfaces.each {
            classes << it
            classes.addAll( parentClasses( it ))
        }

        for ( Class superC = c.superclass; superC; superC = superC.superclass )
        {
            classes << superC
            classes.addAll( parentClasses( superC ))
        }

        classes
    }
}
