package com.goldin.plugins.teamcity.messenger.controller

import com.goldin.plugins.teamcity.messenger.api.MessagesBean
import com.goldin.plugins.teamcity.messenger.api.MessagesConfiguration
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
//@Invariant({ this.messagesBean && this.context && this.config && this.util })
abstract class MessagesBaseController extends BaseController
{
    protected final MessagesBean          messagesBean
    protected final MessagesContext       context
    protected final MessagesConfiguration config
    protected final MessagesUtil          util


    @Requires({ server && messagesBean && context && config && util })
    @Ensures({ this.messagesBean == messagesBean })
    protected MessagesBaseController ( SBuildServer          server,
                                       MessagesBean          messagesBean,
                                       MessagesContext       context,
                                       MessagesConfiguration config,
                                       MessagesUtil          util )
    {
        super( server )

        this.messagesBean = messagesBean
        this.context      = context
        this.config       = config
        this.util         = util
    }


    abstract ModelAndView handleRequest( Map<String, ?> requestParams, SUser currentUser, String username )


//    @Requires({ request && name })
//    @Ensures({ result })
    String param( Map<String, ?> requestParams,
                  String         name,
                  boolean        failIfMissing = true,
                  boolean        trim          = true )
    {
        if ( requestParams.containsKey( name ))
        {
            Object o = requestParams[ name ]
            assert o instanceof String, "Requests contains parameter [$name], but it's not a String - [$o][${ o.class.name }]"
            trim ? ( o as String ).trim() : o
        }
        else
        {
            assert ( ! failIfMissing ), "Requests contains no parameter [$name]. It contains paramaters ${ requestParams.keySet() }"
            ''
        }
    }


//    @Requires({ request && name })
//    @Ensures({ result })
    List<String> params( Map<String, ?> requestParams,
                         String         name,
                         boolean        failIfMissing = true,
                         boolean        trim          = true )
    {
        if ( requestParams.containsKey( name ))
        {
            Object       o    = requestParams[ name ]
            List<String> list = ( o instanceof String ) ? [ o ]      :
                                ( o instanceof List   ) ? ( List ) o : null

            assert ( list != null ), \
                   "Requests contains parameter [$name], but it's neither a String nor a List - [$o][${ o.class.name }]"

            ( trim ? list*.trim() : list )
        }
        else
        {
            assert ( ! failIfMissing ), "Requests contains no parameter [$name]. It contains paramaters ${ requestParams.keySet() }"
            []
        }
    }


    @Override
    @Requires({ request && response })
    protected ModelAndView doHandle ( HttpServletRequest request, HttpServletResponse response )
    {
        SUser  user = SessionUser.getUser( request )
        assert user, 'User is not logged in'

        Map<String, ?> requestParams = [:]
        for ( String param in request.parameterNames )
        {
            String[] values        = request.getParameterValues( param )
            requestParams[ param ] = ( values?.size() == 1 ) ? values[ 0 ] : values?.toList()
        }

        handleRequest( requestParams, user, user.username )
    }
}
