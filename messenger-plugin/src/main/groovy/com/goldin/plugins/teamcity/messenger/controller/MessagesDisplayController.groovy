package com.goldin.plugins.teamcity.messenger.controller

import com.goldin.plugins.teamcity.messenger.api.MessagesBean
import com.goldin.plugins.teamcity.messenger.api.MessagesConfiguration
import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import com.goldin.plugins.teamcity.messenger.api.MessagesUtil
import groovy.json.JsonBuilder
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.users.SUser
import jetbrains.buildServer.web.openapi.WebControllerManager
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires
import org.springframework.web.servlet.ModelAndView

/**
 * Controller that is invoked by the script displaying messages received
 */
class MessagesDisplayController extends MessagesBaseController
{
    static final String MAPPING = 'messagesDisplay.html'


    @Requires({ server && manager && messagesBean && context && config && util })
    MessagesDisplayController ( SBuildServer          server,
                                WebControllerManager  manager,
                                MessagesBean          messagesBean,
                                MessagesContext       context,
                                MessagesConfiguration config,
                                MessagesUtil          util )
    {
        super( server, messagesBean, context, config, util )
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
            def message = messagesBean.deleteMessageByUser( messageId as long, username )
            new TextModelAndView( message ? String.valueOf( message.id ) : '', context.locale )
        }
        else
        {
            /**
             * User retrieves all his messages: List<Message> => List<Map> => JSON
             */
            def messages = messagesBean.getMessagesForUser( username )*.displayData
            new TextModelAndView( new JsonBuilder( messages ).toString(), 'application/json', context.locale )
        }
    }
}
