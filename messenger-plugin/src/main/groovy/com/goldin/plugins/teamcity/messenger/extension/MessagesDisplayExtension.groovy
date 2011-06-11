package com.goldin.plugins.teamcity.messenger.extension

import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import javax.servlet.http.HttpServletRequest
import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PlaceId
import jetbrains.buildServer.web.openapi.PositionConstraint
import jetbrains.buildServer.web.openapi.SimplePageExtension
import jetbrains.buildServer.web.util.WebUtil
import org.gcontracts.annotations.Requires

/**
 * Messenger extension
 */
class MessagesDisplayExtension extends SimplePageExtension
{
    @Requires({ pagePlaces && context })
    MessagesDisplayExtension ( PagePlaces pagePlaces, MessagesContext context )
    {
        super( pagePlaces, PlaceId.ALL_PAGES_HEADER, context.pluginName, 'messagesDisplay.jsp' )
        position = PositionConstraint.first()
        register()
    }

    
    @Override
    @Requires({ request })
    boolean isAvailable ( final HttpServletRequest request )
    {
        def path = WebUtil.getPathWithoutAuthenticationType( request )
        (( path == '/' ) || path.startsWith( '/overview.html' ))
    }
}
