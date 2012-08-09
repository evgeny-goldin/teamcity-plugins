package com.goldin.plugins.teamcity.report

import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.serverSide.ServerPaths
import org.springframework.context.ApplicationContext

import java.lang.reflect.Method
/**
 * Various helper methods.
 */
class ReportHelper
{
    private final Set<String> apiClasses = this.class.getResource( '/open-api-classes.txt' ).getText( 'UTF-8' ).readLines()*.trim().toSet()

    /**
     * Retrieves {@link jetbrains.buildServer.serverSide.SBuildServer} report table.
     * @return {@link jetbrains.buildServer.serverSide.SBuildServer} report table
     */
    Map<String, ?> serverTable( SBuildServer server )
    {
        propertiesMap(
            server,
            'fullServerVersion serverRootPath'.tokenize())
    }


    /**
     * Retrieves {@link jetbrains.buildServer.serverSide.ServerPaths} report table.
     * @return {@link jetbrains.buildServer.serverSide.ServerPaths} report table
     */
    Map<String, ?> pathsTable ( ServerPaths paths )
    {
        propertiesMap (
            paths,
            'dataDirectory artifactsDirectory backupDir cachesDir configDir libDir logsPath pluginDataDirectory pluginsDir systemDir'.tokenize(),
            { Object o -> (( o instanceof File ) ? o : new File( o.toString())).canonicalPath.replace( '\\', '/' ) })
    }


    /**
     * Retrieves {@link org.springframework.context.ApplicationContext} report table.
     * @param context       context to read the data from
     * @param allApiClasses Open API list of classes available as public Javadoc
     * @return {@link org.springframework.context.ApplicationContext} report table
     */
    Map<String, ?> contextTable( ApplicationContext context )
    {
        assert context

        context.getBeanNamesForType( Object ).sort().inject([:]){
            Map m, String beanName ->
            //noinspection GroovyGetterCallCanBePropertyAccess
            final beanClass  = context.getBean( beanName ).getClass()
            final beanTitle  = beanClass.name in this.apiClasses ? javadocLink( beanClass ) : beanClass.name
            final apiClasses = parentClasses( beanClass ).findAll{ it.name in this.apiClasses }

            if ( apiClasses )
            {
                beanTitle += ":<br/>- ${ apiClasses.collect{ javadocLink( it )}.join( '<br/>- ') }"
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
    String javadocLink ( Class c, boolean useFullName = true )
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
