package com.goldin.plugins.teamcity.report

import jetbrains.buildServer.controllers.BaseController
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.web.openapi.WebControllerManager
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class ReportController extends BaseController
{
    ReportController ( SBuildServer server, WebControllerManager manager )
    {
        super( server )
        manager.registerController( "/$MAPPING", this )
    }


    @Override
    protected ModelAndView doHandle ( HttpServletRequest httpServletRequest , HttpServletResponse httpServletResponse )
    {
        return null
    }
}
