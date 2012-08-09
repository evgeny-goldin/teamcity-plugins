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
    private final ReportHelper       helper = new ReportHelper()
    private final SBuildServer       server
    private final ApplicationContext context
    private final PluginDescriptor   descriptor
    private final ServerPaths        paths

    static final String EVAL_CODE   = 'evalCode'
    static final String EVAL_RESULT = 'evalResult'


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

        assert tabId
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
        final evalCode   = request.session.getAttribute( EVAL_CODE ) ?: '''
# Type your script and click "Evaluate", lines starting with '#' are ignored.

# Variables available in the script context:
# * "request" - instance of javax.servlet.http.HttpServletRequest
# * "context" - instance of org.springframework.context.ApplicationContext
# * "server"  - instance of jetbrains.buildServer.serverSide.SBuildServer

# To retrieve currently logged in user:
# Class.forName( 'jetbrains.buildServer.web.util.SessionUser' ).getUser( request )

# To retrieve SBuildServer instance:
# context.getBean( Class.forName( 'jetbrains.buildServer.serverSide.SBuildServer' ))
'''
        final evalResult = request.session.getAttribute( EVAL_RESULT )

        request.session.removeAttribute( EVAL_CODE )
        request.session.removeAttribute( EVAL_RESULT )

        //noinspection GroovyConditionalCanBeElvis
        model << [ tables     : helper.getReport( server, paths, context ),
                   action     : ReportController.MAPPING,
                   evalCode   : ( evalCode   ?: '' ),
                   evalResult : ( evalResult ?: '' )]
    }
}
