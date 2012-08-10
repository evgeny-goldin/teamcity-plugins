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
 * Adds "Report" tab to Administration => Diagnostics.
 */
class ReportExtension extends SimpleCustomTab
{
    static  final String             DELIMITER = '---------------'
    private final ReportHelper       helper    = new ReportHelper()
    private final SBuildServer       server
    private final ApplicationContext context
    private final ServerPaths        paths


    ReportExtension ( PagePlaces         pagePlaces,
                      SBuildServer       server,
                      ApplicationContext context,
                      PluginDescriptor   descriptor,
                      ServerPaths        paths )
    {
        super( pagePlaces, PlaceId.ADMIN_SERVER_DIAGNOSTIC_TAB, descriptor.getParameterValue( 'name' ), 'displayReport.jsp', 'Report' )

        this.server  = server
        this.context = context
        this.paths   = paths

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
        //noinspection GroovyConditionalCanBeElvis
        model << [ report    : helper.getReport( server, paths, context ),
                   action    : ReportController.MAPPING,
                   delimiter : DELIMITER ]
    }
}
