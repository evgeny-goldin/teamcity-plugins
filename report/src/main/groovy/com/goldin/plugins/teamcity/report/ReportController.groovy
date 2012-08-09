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


class ReportController extends BaseController
{
    static final String         MAPPING     = 'displayReport.html'
    private static final String API_JAVADOC = 'http://javadoc.jetbrains.net/teamcity/openapi/current'

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
        final model       = []
        final serverTable = [:]
        final pathsTable  = [:]
        final link        = {
            Class c, boolean useFullName = true ->
            "<a href='$API_JAVADOC/${ c.name.replace( '.', '/' )}.html'>${ useFullName ? c.name : c.simpleName }</a>"
        }

        serverTable[ 'getFullServerVersion()' ] = server.fullServerVersion
        serverTable[ 'getServerRootPath()'    ] = server.serverRootPath

        model << [ link( SBuildServer, false ), 'Key', 'Value', serverTable ]

        pathsTable[ 'getArtifactsDirectory()'  ] = paths.artifactsDirectory.canonicalPath
        pathsTable[ 'getBackupDir()'           ] = paths.backupDir
        pathsTable[ 'getCachesDir()'           ] = paths.cachesDir
        pathsTable[ 'getConfigDir()'           ] = paths.configDir
        pathsTable[ 'getDataDirectory()'       ] = paths.dataDirectory.canonicalPath
        pathsTable[ 'getLibDir()'              ] = paths.libDir
        pathsTable[ 'getLogsPath()'            ] = paths.logsPath.canonicalPath
        pathsTable[ 'getPluginDataDirectory()' ] = paths.pluginDataDirectory
        pathsTable[ 'getPluginsDir()'          ] = paths.pluginsDir
        pathsTable[ 'getSystemDir()'           ] = paths.systemDir

        model << [ link( ServerPaths, false ), 'Key', 'Value', pathsTable ]

        final allApiClasses = this.class.getResource( '/open-api-classes.txt' ).
                              getText( 'UTF-8' ).readLines()*.trim().toSet()

        for ( ApplicationContext c = context; c; c = c.parent )
        {
            final contextMap = c.getBeanNamesForType( Object ).sort().inject([:]){
                Map m, String beanName ->
                //noinspection GroovyGetterCallCanBePropertyAccess
                final beanClass  = c.getBean( beanName ).getClass()
                final beanTitle  = beanClass.name in allApiClasses ? link( beanClass ) : beanClass.name
                final apiClasses = parentClasses( beanClass ).findAll{ it.name in allApiClasses }.sort {
                    c1, c2 -> c1.name <=> c2.name
                }

                if ( apiClasses )
                {
                    beanTitle += "<br/>Extends / Implements:<br/>- ${ apiClasses.collect{ link( it )}.join( '<br/>- ') }"
                }

                m[ beanTitle ] = beanName
                m
            }

            final title = ( c == context ) ? 'Spring Context' : 'Parent Spring Context'
            model << [ "<a href='http://static.springsource.org/spring/docs/3.0.x/javadoc-api/org/springframework/context/ApplicationContext.html'>$title</a>",
                       'Bean Class', 'Bean Name',
                       contextMap ]
        }

        new ModelAndView( descriptor.getPluginResourcesPath( 'displayReport.jsp' ),
                          [ tables: model ] )
    }


    Set<Class> parentClasses( Class c )
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
