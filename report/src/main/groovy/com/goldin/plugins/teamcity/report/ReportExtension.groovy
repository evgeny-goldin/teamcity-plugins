package com.goldin.plugins.teamcity.report
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.serverSide.ServerPaths
import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PlaceId
import jetbrains.buildServer.web.openapi.PluginDescriptor
import jetbrains.buildServer.web.openapi.SimpleCustomTab
import jetbrains.buildServer.web.util.SessionUser
import org.springframework.context.ApplicationContext

import javax.servlet.http.HttpServletRequest


/**
 * Adds "Reports" tab to Administration => Diagnostics.
 */
class ReportExtension extends SimpleCustomTab
{
    private final ReportHelper       helper = new ReportHelper()
    private final SBuildServer       server
    private final ApplicationContext context
    private final PluginDescriptor   descriptor
    private final ServerPaths        paths


    ReportExtension ( PagePlaces         pagePlaces,
                      SBuildServer       server,
                      ApplicationContext context,
                      PluginDescriptor   descriptor,
                      ServerPaths        paths )
    {
        super( pagePlaces, PlaceId.ADMIN_SERVER_DIAGNOSTIC_TAB, descriptor.getParameterValue( 'name' ), 'displayReport.jsp', 'Report' )

        this.server     = server
        this.context    = context
        this.descriptor = descriptor
        this.paths      = paths

        register()
    }


    @Override
    boolean isAvailable ( HttpServletRequest request )
    {
        SessionUser.getUser( request )?.systemAdministratorRoleGranted
    }


    @Override
    void fillModel ( Map<String , Object> model, HttpServletRequest request )
    {
        model.tables = []
        model.action = ReportController.MAPPING

        model.tables << [ helper.javadocLink( SBuildServer, false ), 'Method Name', 'Value Returned', helper.serverTable( server )]
        model.tables << [ helper.javadocLink( ServerPaths,  false ), 'Method Name', 'Value Returned', helper.pathsTable ( paths  )]

        for ( ApplicationContext context = this.context; context; context = context.parent )
        {
            final title = (( context == this.context ) ? 'Plugin' : 'Parent' ) + ' Spring Context'
            model.tables << [ "<a href='http://static.springsource.org/spring/docs/3.0.x/javadoc-api/org/springframework/context/ApplicationContext.html'>$title</a>",
                              'Bean Class', 'Bean Name',
                              helper.contextTable( context )]
        }
    }
}
