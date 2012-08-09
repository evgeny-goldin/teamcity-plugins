package com.goldin.plugins.teamcity.report

import jetbrains.buildServer.controllers.BaseController
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.serverSide.ServerPaths
import jetbrains.buildServer.web.openapi.PluginDescriptor
import jetbrains.buildServer.web.openapi.WebControllerManager
import org.springframework.context.ApplicationContext
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.lang.reflect.Method


class ReportController extends BaseController
{
    static final String              MAPPING    = 'displayReport.html'
    private final Set<String>        apiClasses = this.class.getResource( '/open-api-classes.txt' ).getText( 'UTF-8' ).readLines()*.trim().toSet()
    private final SBuildServer       server
    private final ApplicationContext context
    private final PluginDescriptor   descriptor
    private final ServerPaths        paths


    ReportController ( SBuildServer         server,
                       WebControllerManager manager,
                       ApplicationContext   context,
                       PluginDescriptor     descriptor,
                       ServerPaths          paths )
    {
        super( server )
        this.server     = server
        this.context    = context
        this.descriptor = descriptor
        this.paths      = paths
        manager.registerController( "/$MAPPING", this )
    }


    @Override
    protected ModelAndView doHandle ( HttpServletRequest  httpServletRequest,
                                      HttpServletResponse httpServletResponse )
    {
        final model = []

        model << [ link( SBuildServer, false ), 'Method Name', 'Value Returned', serverTable()]
        model << [ link( ServerPaths,  false ), 'Method Name', 'Value Returned', pathsTable ()]

        for ( ApplicationContext context = this.context; context; context = context.parent )
        {
            final title = ( context == this.context ) ? 'Spring Context' : 'Parent Spring Context'
            model << [ "<a href='http://static.springsource.org/spring/docs/3.0.x/javadoc-api/org/springframework/context/ApplicationContext.html'>$title</a>",
                       'Bean Class', 'Bean Name',
                       contextTable( context )]
        }

        new ModelAndView( descriptor.getPluginResourcesPath( 'displayReport.jsp' ),
                          [ tables: model ] )
    }


    /**
     * Normalizes path of the object specified.
     * @param o object to normalize its path, can be {@link File}
     * @return normalized path of the object specified
     */
    private normalizePath( Object o )
    {
        (( o instanceof File ) ? o : new File( o.toString())).canonicalPath.replace( '\\', '/' )
    }


    /**
     * Retrieves {@link SBuildServer} report table.
     * @return {@link SBuildServer} report table
     */
    private Map<String, ?> serverTable()
    {
        propertiesMap(
            server,
            'fullServerVersion serverRootPath'.tokenize())
    }



    /**
     * Retrieves {@link ServerPaths} report table.
     * @return {@link ServerPaths} report table
     */
    private Map<String, ?> pathsTable ()
    {
        propertiesMap (
            paths,
            'dataDirectory artifactsDirectory backupDir cachesDir configDir libDir logsPath pluginDataDirectory pluginsDir systemDir'.tokenize(),
            this.&normalizePath )
    }


    /**
     * Retrieves {@link ApplicationContext} report table.
     * @param context       context to read the data from
     * @param allApiClasses Open API list of classes available as public Javadoc
     * @return {@link ApplicationContext} report table
     */
    private Map<String, ?> contextTable( ApplicationContext context )
    {
        assert context

        context.getBeanNamesForType( Object ).sort().inject([:]){
            Map m, String beanName ->
            //noinspection GroovyGetterCallCanBePropertyAccess
            final beanClass  = context.getBean( beanName ).getClass()
            final beanTitle  = beanClass.name in this.apiClasses ? link( beanClass ) : beanClass.name
            final apiClasses = parentClasses( beanClass ).findAll{ it.name in this.apiClasses }.sort {
                c1, c2 -> c1.name <=> c2.name
            }

            if ( apiClasses )
            {
                beanTitle += ":<br/>- ${ apiClasses.collect{ link( it )}.join( '<br/>- ') }"
            }

            m[ beanTitle ] = beanName
            m
        }
    }


    /**
     * Constructs a link to an Open API class Javadoc.
     *
     * @param c           class to construct the link for
     * @param useFullName whether full class name or simple name should be used as link title
     * @return HTML link to class Javadoc
     */
    private String link( Class c, boolean useFullName = true )
    {
        assert ( c && ( c.name in apiClasses )), "Class [$c.name] is not part of an Open API"
        "<a href='http://javadoc.jetbrains.net/teamcity/openapi/current/${ c.name.replace( '.', '/' )}.html'>${ useFullName ? c.name : c.simpleName }</a>"
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
    private Set<Class> parentClasses( Class c )
    {
        assert c

        final classes = [] as Set

        c.interfaces.each { classes.addAll( parentClasses( it ) + it )}

        for ( Class superC = c.superclass; superC; superC = superC.superclass )
        {
            classes.addAll( parentClasses( superC ) + superC )
        }

        classes
    }
}
