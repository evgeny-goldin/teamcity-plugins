package com.goldin.plugins.teamcity.messenger.controller

import com.goldin.plugins.teamcity.messenger.api.Message
import com.goldin.plugins.teamcity.messenger.api.Message.Urgency
import com.goldin.plugins.teamcity.messenger.api.MessagesBean
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.users.SUser
import jetbrains.buildServer.web.openapi.WebControllerManager
import jetbrains.buildServer.web.util.SessionUser
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires
import org.springframework.web.servlet.ModelAndView

/**
 * Controller activated when message is sent
 */
class MessagesSendController extends MessagesBaseController
{
    private final MessagesBean messagesBean


    @Requires({ server && manager && messagesBean })
    MessagesSendController ( SBuildServer server, WebControllerManager manager, MessagesBean messagesBean )
    {
        super( server )
        this.messagesBean = messagesBean
        manager.registerController( '/messagesSend.html', this )
    }


    @Override
    @Requires({ request && response })
    @Ensures({ result != null })
    protected ModelAndView doHandle ( HttpServletRequest request, HttpServletResponse response )
    {
        SUser  user = SessionUser.getUser( request )
        assert user, 'User is not logged in'

        def          sender        = user.username
        def          urgency       = param( request, 'urgency' ).toUpperCase() as Urgency
        def          messageText   = param( request, 'message' )
        long         longevity     = longevity( request )
        boolean      sendToAll     = request.getParameter( 'all' ) as boolean
        List<String> sendToGroups  = params( request, 'groups', false ) // Values are not sent
        List<String> sendToUsers   = params( request, 'users',  false ) // when groups/users are disabled
        Message      message       = new Message( sender, urgency, messageText, longevity, sendToAll, sendToGroups, sendToUsers )
        long         messageId     = messagesBean.sendMessage( message )

        new ModelAndView( new TextView( messageId as String ))
    }


    @Requires({ request })
    @Ensures({ result > 0 })
    private long longevity ( HttpServletRequest request )
    {
        long   number = param( request, 'longevity-number' ) as long
        String unit   = param( request, 'longevity-unit'   )

        number * (( 'hours'  == unit ) ? 1       :
                  ( 'days'   == unit ) ? 24      :
                  ( 'weeks'  == unit ) ? 24 * 7  :
                  ( 'months' == unit ) ? 24 * 30 :
                                         24 * 365 )
    }
}