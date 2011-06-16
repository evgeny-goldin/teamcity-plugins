package com.goldin.plugins.teamcity.messenger.extension

import com.goldin.plugins.teamcity.messenger.api.MessagesConfiguration
import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import javax.servlet.http.HttpServletRequest
import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PlaceId
import jetbrains.buildServer.web.openapi.PositionConstraint
import jetbrains.buildServer.web.openapi.SimplePageExtension
import jetbrains.buildServer.web.util.WebUtil
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires

/**
 * Messenger extension
 */
class MessagesDisplayExtension extends SimplePageExtension
{
    private final int ajaxRequestInterval

    @Requires({ pagePlaces && context && config })
    @Ensures({ this.ajaxRequestInterval > 0 })
    MessagesDisplayExtension ( PagePlaces pagePlaces, MessagesContext context, MessagesConfiguration config )
    {
        super( pagePlaces, PlaceId.ALL_PAGES_HEADER, context.pluginName, 'messagesDisplay.jsp' )
        
        this.position            = PositionConstraint.first()
        this.ajaxRequestInterval = config.ajaxRequestInterval
        register()

    }

    
    @Override
    @Requires({ request })
    boolean isAvailable ( final HttpServletRequest request )
    {
        def path = WebUtil.getPathWithoutAuthenticationType( request )
        (( path == '/' ) || path.startsWith( '/overview.html' ))
    }


    @Override
    @Requires({ model != null })
    @Ensures({ model[ 'intervalMs' ] > 0 })
    void fillModel ( Map<String, Object> model, HttpServletRequest request )
    {
        model[ 'intervalMs' ] = ( ajaxRequestInterval * 1000 )
    }
}
