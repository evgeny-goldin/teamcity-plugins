package com.goldin.plugins.teamcity.messenger

import javax.servlet.http.HttpServletRequest
import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PlaceId
import jetbrains.buildServer.web.openapi.SimplePageExtension
import jetbrains.buildServer.web.util.WebUtil
import org.jetbrains.annotations.NotNull
import jetbrains.buildServer.web.openapi.PositionConstraint


/**
 * Messenger extension
 */
class MessengerExtension extends SimplePageExtension
{
    MessengerExtension ( PagePlaces pagePlaces )
    {
        super( pagePlaces, PlaceId.ALL_PAGES_HEADER, 'messenger-plugin', 'message.jsp' )
        position = PositionConstraint.first()
        register()
    }

    @Override
    boolean isAvailable ( @NotNull final HttpServletRequest request )
    {
        WebUtil.getPathWithoutAuthenticationType( request ).startsWith( '/overview.html' )
    }
}
