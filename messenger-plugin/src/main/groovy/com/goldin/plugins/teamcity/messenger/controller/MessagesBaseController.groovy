package com.goldin.plugins.teamcity.messenger.controller

import com.goldin.plugins.teamcity.messenger.api.MessagesBean
import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import com.goldin.plugins.teamcity.messenger.api.MessagesUtil
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import jetbrains.buildServer.controllers.BaseController
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.users.SUser
import jetbrains.buildServer.web.util.SessionUser
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires
import org.springframework.web.servlet.ModelAndView

/**
 * Base class for all controllers
 */
abstract class MessagesBaseController extends BaseController
{
    final MessagesBean    messagesBean
    final MessagesContext context
    final MessagesUtil    util

    
    @Requires({ server && messagesBean && context && util })
    @Ensures({ this.messagesBean == messagesBean })
    protected MessagesBaseController ( SBuildServer    server,
                                       MessagesBean    messagesBean,
                                       MessagesContext context,
                                       MessagesUtil    util )
    {
        super( server )
        
        this.messagesBean = messagesBean
        this.context      = context
        this.util         = util
    }


    abstract ModelAndView handleRequest( HttpServletRequest request, SUser currentUser )
    

//    @Requires({ request && name })
//    @Ensures({ result })
    String param( HttpServletRequest request, String name, boolean failIfMissing = true )
    {
        String parameter = request.getParameter( name ) ?: ''
        assert ( ! failIfMissing ) || parameter, \
               "Requests contains no parameter [$name]. It contains paramaters ${ request.parameterMap.keySet() }"
        parameter.trim()
    }


//    @Requires({ request && name })
//    @Ensures({ result })
    List<String> params( HttpServletRequest request, String name, boolean failIfMissing = true )
    {
        Set<String> parameters = ( request.getParameterValues( name ) ?: [] ) as List
        assert ( ! failIfMissing ) || parameters, \
               "Requests contains no parameters [$name]. It contains paramaters ${ request.parameterMap.keySet() }"
        parameters*.trim()
    }

    
    @Override
    protected ModelAndView doHandle ( HttpServletRequest request, HttpServletResponse response )
    {
        SUser  user = SessionUser.getUser( request )
        assert user, 'User is not logged in'

        handleRequest( request, user )
    }
}
