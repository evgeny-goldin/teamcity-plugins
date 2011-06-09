package com.goldin.plugins.teamcity.messenger.controller

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import jetbrains.buildServer.controllers.BaseController
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.web.openapi.WebControllerManager
import org.springframework.web.servlet.ModelAndView

import com.goldin.plugins.teamcity.messenger.Context

/**
 * Controller returning messages to be displayed
 */
class MessagesController extends BaseController
{
    final Context context

    MessagesController ( SBuildServer         buildServer,
                         WebControllerManager manager,
                         Context              context )
    {
        super( buildServer )
        this.context = context
        manager.registerController( '/messagesDisplay.html', this )
    }


    @Override
    protected ModelAndView doHandle ( HttpServletRequest  request,
                                      HttpServletResponse response )
    {
        new ModelAndView( "/plugins/${ context.pluginName }/messages.jsp",
                          [ a:'b', c:'d' ] )
    }
}
