package com.goldin.plugins.teamcity.report
import jetbrains.buildServer.controllers.BaseController
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.web.openapi.WebControllerManager
import jetbrains.buildServer.web.util.SessionUser
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse



class ReportController extends BaseController
{
    static final String MAPPING = '/displayReport.html'


    ReportController ( SBuildServer         server,
                       WebControllerManager manager )
    {
        super( server )
        manager.registerController( MAPPING, this )
    }


    @Override
    protected ModelAndView doHandle ( HttpServletRequest  request,
                                      HttpServletResponse response )
    {
        if ( ! SessionUser.getUser( request )?.systemAdministratorRoleGranted )
        {
            response.sendRedirect( '' ) // Overview page
            return null
        }

        null
    }
}
