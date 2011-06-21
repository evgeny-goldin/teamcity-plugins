package com.goldin.plugins.teamcity.messenger.controller

import com.goldin.plugins.teamcity.messenger.api.Message
import com.goldin.plugins.teamcity.messenger.api.Message.Urgency
import com.goldin.plugins.teamcity.messenger.api.MessagesBean
import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import com.goldin.plugins.teamcity.messenger.api.MessagesUtil
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.users.SUser
import jetbrains.buildServer.web.openapi.WebControllerManager
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires
import org.springframework.web.servlet.ModelAndView

/**
 * Controller activated when message is sent
 */
class MessagesSendController extends MessagesBaseController
{
    static final String MAPPING = 'messagesSend.html'


    @Requires({ server && messagesBean && context && util && manager })
    MessagesSendController ( SBuildServer         server,
                             MessagesBean         messagesBean,
                             MessagesContext      context,
                             MessagesUtil         util,
                             WebControllerManager manager )
    {
        super( server, messagesBean, context, util )
        manager.registerController( "/$MAPPING", this )
    }


    @Requires({ requestParams && user && username })
    @Ensures({ result != null })
    ModelAndView handleRequest ( Map<String, ?> requestParams, SUser user, String username )
    {
        Urgency      urgency       = param( requestParams, 'urgency' ).toUpperCase() as Urgency
        String       messageText   = param( requestParams, 'message' )
        long         longevity     = longevity( requestParams )
        boolean      sendToAll     = param( requestParams,  'all',    false ) as boolean
        List<String> sendToGroups  = params( requestParams, 'groups', false ) // Values are not sent
        List<String> sendToUsers   = params( requestParams, 'users',  false ) // when groups/users are disabled
        Message      message       = new Message( username, urgency, messageText, longevity, sendToAll, sendToGroups, sendToUsers )
        long         messageId     = messagesBean.sendMessage( message )

        new TextModelAndView( messageId as String, context.locale )
    }


    @Requires({ requestParams })
    @Ensures({ result > 0 })
    private long longevity ( Map<String, ?> requestParams)
    {
        long   number = param( requestParams, 'longevity-number' ) as long
        String unit   = param( requestParams, 'longevity-unit'   )

        number * (( 'hours'  == unit ) ? 1       :
                  ( 'days'   == unit ) ? 24      :
                  ( 'weeks'  == unit ) ? 24 * 7  :
                  ( 'months' == unit ) ? 24 * 30 :
                                         24 * 365 )
    }
}