package com.goldin.plugins.teamcity.messenger.controller

import groovy.json.JsonBuilder
import java.text.DateFormat
import java.text.SimpleDateFormat
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.users.SUser
import jetbrains.buildServer.web.openapi.WebControllerManager
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires
import org.springframework.web.servlet.ModelAndView
import com.goldin.plugins.teamcity.messenger.api.*

/**
 * Controller that is invoked by the script displaying messages received
 */
class MessagesDisplayController extends MessagesBaseController
{
    private final Locale     locale
    private final DateFormat dateFormatter
    private final DateFormat timeFormatter


    @Requires({ server && messagesBean && context && config && util && manager })
    MessagesDisplayController ( SBuildServer          server,
                                MessagesBean          messagesBean,
                                MessagesContext       context,
                                MessagesConfiguration config,
                                MessagesUtil          util,
                                WebControllerManager  manager )
    {
        super( server, messagesBean, context, util )
        this.locale        = context.locale
        this.dateFormatter = new SimpleDateFormat( config.dateFormatPattern, context.locale )
        this.timeFormatter = new SimpleDateFormat( config.timeFormatPattern, context.locale )
        manager.registerController( '/messagesDisplay.html', this )
    }

    
    @Requires({ requestParams && user && username })
    @Ensures({ result != null })
    ModelAndView handleRequest ( Map<String, ?> requestParams, SUser user, String username )
    {
        def groups   = context.getUserGroups( username )
        def messages = messagesBean.getMessagesForUser( username ).collect {
            Message m ->

            def recipient = m.sendToAll                              ? 'all' :
                            m.sendToUsers.contains( username )       ? "user $username" :
                            util.intersect( m.sendToGroups, groups ) ? "groups ${ m.sendToGroups.intersect( groups )}" :
                                                                       ''
            assert recipient, "Can't determine recipient for [$m]"

            [ id        : m.id,
              sender    : context.getUser( m.sender ).descriptiveName,
              text      : m.message,
              recipient : recipient,
              date      : dateFormatter.format( new Date( m.timestamp )),
              time      : timeFormatter.format( new Date( m.timestamp ))]
        }

        new ModelAndView( new TextView( new JsonBuilder( messages ).toString(), 'application/json', locale ))
    }
}
