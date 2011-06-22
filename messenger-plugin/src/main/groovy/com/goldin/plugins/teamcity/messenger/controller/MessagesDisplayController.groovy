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
    static final String MAPPING = 'messagesDisplay.html'

    private final DateFormat dateFormatter
    private final DateFormat timeFormatter


    @Requires({ server && manager && messagesBean && context && config && util })
    MessagesDisplayController ( SBuildServer          server,
                                WebControllerManager  manager,
                                MessagesBean          messagesBean,
                                MessagesContext       context,
                                MessagesConfiguration config,
                                MessagesUtil          util )
    {
        super( server, messagesBean, context, util )
        this.dateFormatter = new SimpleDateFormat( config.dateFormatPattern, context.locale )
        this.timeFormatter = new SimpleDateFormat( config.timeFormatPattern, context.locale )
        manager.registerController( "/$MAPPING", this )
    }


    @Requires({ requestParams && user && username })
    @Ensures({ result != null })
    ModelAndView handleRequest ( Map<String, ?> requestParams, SUser user, String username )
    {
        String messageId = requestParams[ 'id' ]

        if ( messageId )
        {   /**
             * User deletes message displayed
             */
            messagesBean.deleteMessageByUser( messageId as long, username )
            new TextModelAndView( messageId, context.locale )
        }
        else
        {
            /**
             * User retrieves all his messages
             */
            def messages = messagesBean.getMessagesForUser( username ).collect {
                Message m ->

                def data = m.displayData
                def date = new Date( data[ 'timestamp' ] as long )

                data << [ date : dateFormatter.format( date ),
                          time : timeFormatter.format( date )]
            }

            new TextModelAndView( new JsonBuilder( messages ).toString(), 'application/json', context.locale )
        }
    }
}
