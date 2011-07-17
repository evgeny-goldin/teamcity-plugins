package com.goldin.plugins.teamcity.messenger.extension

import com.goldin.plugins.teamcity.messenger.api.MessagesConfiguration
import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import com.goldin.plugins.teamcity.messenger.controller.MessagesSendController
import javax.servlet.http.HttpServletRequest
import jetbrains.buildServer.groups.UserGroupManager
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.web.openapi.CustomTab
import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PlaceId
import jetbrains.buildServer.web.openapi.PositionConstraint
import org.gcontracts.annotations.Requires


/**
 * Messenger extension sending messages
 */
class MessagesSendExtension extends MessagesBaseExtension implements CustomTab
{
    private final SBuildServer     server
    private final UserGroupManager groupsManager


    @Requires({ server && groupsManager && pagePlaces && context })
    MessagesSendExtension ( SBuildServer server, UserGroupManager groupsManager,
                            PagePlaces pagePlaces, MessagesContext context, MessagesConfiguration config )
    {
        super( pagePlaces, PlaceId.MY_TOOLS_TABS, 'messagesSend.jsp', PositionConstraint.last(), context, config )

        this.server        = server
        this.groupsManager = groupsManager
    }


    @Override
//    @Ensures({ result })
    List<String> getFilesToAdd () { [ 'messages-send.js' ] }


    String getTabId    () { 'sendMessage'  }
    String getTabTitle () { 'Send Message' }
    boolean isVisible  () { true }


    @Override
//    @Requires({( model != null ) && server && groupsManager })
//    @Ensures({ model })
    void fillModel ( Map<String, Object> model, HttpServletRequest request )
    {
        def groups = groupsManager.userGroups
        def users  = server.userModel.allUsers.users

        assert groups, 'No groups found on the server'
        assert users,  'No users found on the server'

        model << [ groups    : groups*.name            as List,
                   userNames : users*.username         as List,
                   fullNames : users*.descriptiveName  as List,
                   action    : MessagesSendController.MAPPING ]
    }
}
