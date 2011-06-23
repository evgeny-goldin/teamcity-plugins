package com.goldin.plugins.teamcity.messenger.extension

import com.goldin.plugins.teamcity.messenger.api.MessagesConfiguration
import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import com.goldin.plugins.teamcity.messenger.controller.MessagesDisplayController
import javax.servlet.http.HttpServletRequest
import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PlaceId
import jetbrains.buildServer.web.openapi.PositionConstraint
import jetbrains.buildServer.web.util.WebUtil
import org.gcontracts.annotations.Requires

/**
 * Messenger extension displaying to user messages received
 */
class MessagesDisplayExtension extends MessagesBaseExtension
{
    @Requires({ pagePlaces && context && config })
    MessagesDisplayExtension ( PagePlaces pagePlaces, MessagesContext context, MessagesConfiguration config )
    {
        super( pagePlaces, PlaceId.ALL_PAGES_HEADER, 'messagesDisplay.jsp', PositionConstraint.first(), context, config )
    }


    @Override
//    @Ensures({ result })
    List<String> getFilesToAdd () { [ 'messages-display.js' ] }


    @Override
//    @Requires({ request })
    boolean isAvailable ( final HttpServletRequest request )
    {
        def path = WebUtil.getPathWithoutAuthenticationType( request )
        (( path == '/' ) || path.startsWith( '/overview.html' ))
    }


    @Override
//    @Requires({ model != null })
//    @Ensures({ model })
    void fillModel ( Map<String, Object> model, HttpServletRequest request )
    {
        model << [ ajaxRequestInterval : config.ajaxRequestInterval,
                   action              : MessagesDisplayController.MAPPING ]
    }
}
