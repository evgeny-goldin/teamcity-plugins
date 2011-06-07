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
    Constants constants

    MessagesController ( SBuildServer         buildServer,
                         WebControllerManager manager,
                         Constants            constants )
    {
        super( buildServer )
        setConstants( constants )
        manager.registerController( '/messagesDisplay.html', this )
    }

    
    @Override
    protected ModelAndView doHandle ( HttpServletRequest  request,
                                      HttpServletResponse response )
    {
        new ModelAndView( "/plugins/${ constants.pluginName }/messages.jsp",
                          [ a:'b', c:'d' ] )
    }
}
