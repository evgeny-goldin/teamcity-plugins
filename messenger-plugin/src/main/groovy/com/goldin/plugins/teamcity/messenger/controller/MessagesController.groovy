package com.goldin.plugins.teamcity.messenger.controller

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import jetbrains.buildServer.controllers.BaseController
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.web.openapi.WebControllerManager
import org.springframework.web.servlet.ModelAndView
import com.goldin.plugins.teamcity.messenger.Constants


/**
 * Controller returning messages to be displayed
 */
class MessagesController extends BaseController
{
    MessagesController ( SBuildServer         buildServer,
                         WebControllerManager manager )
    {
        super( buildServer )
        manager.registerController( '/messagesDisplay.html', this )
    }

    
    @Override
    protected ModelAndView doHandle ( HttpServletRequest  request,
                                      HttpServletResponse response )
    {
        Map model = [ a:'b', c:'d' ]
        new ModelAndView( "/plugins/${ Constants.PLUGIN_NAME }/messages.jsp", model )
    }
}
