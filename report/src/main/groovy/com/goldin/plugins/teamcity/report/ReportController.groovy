package com.goldin.plugins.teamcity.report
import jetbrains.buildServer.controllers.BaseController
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.web.openapi.WebControllerManager
import jetbrains.buildServer.web.util.SessionUser
import org.springframework.context.ApplicationContext
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class ReportController extends BaseController
{
    static final String MAPPING = '/displayReport.html'

    private final ApplicationContext context
    private final ReportExtension    extension


    ReportController ( SBuildServer         server,
                       WebControllerManager manager,
                       ApplicationContext   context,
                       ReportExtension      extension )
    {
        super( server )

        manager.registerController( MAPPING, this )
        this.context    = context
        this.extension  = extension
    }


    @Override
    @SuppressWarnings([ 'CatchThrowable' ])
    protected ModelAndView doHandle ( HttpServletRequest  request,
                                      HttpServletResponse response )
    {
        if ( ! SessionUser.getUser( request )?.systemAdministratorRoleGranted )
        {
            response.sendRedirect( '' ) // Overview page
            return null
        }

        String evalCode = request.getParameter( 'evalCode' )
        request.session.setAttribute( ReportExtension.EVAL_CODE, evalCode )

        if ( evalCode )
        {
            final code = evalCode.readLines()*.trim().findAll{ ! it.startsWith( '#' ) }.join( '\n' ).trim()
            if ( code )
            {
                Object evalValue

                try
                {
                    evalValue = new GroovyShell( new Binding( [ request: request, context: context, server: myServer ] )).
                                evaluate( code )
                }
                catch ( Throwable t )
                {
                    evalValue = t.toString()
                }

                //noinspection GroovyConditionalCanBeElvis
                request.session.setAttribute( ReportExtension.EVAL_RESULT, ( evalValue?.toString() ?: '&lt;null&gt;' ))
            }
        }

        // "Report" tab in Administration => Diagnostics
        response.sendRedirect( "admin/admin.html?item=diagnostics&tab=${ extension.tabId }" )
        null
    }
}
