package com.goldin.plugins.teamcity.messenger.extension

import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PlaceId
import jetbrains.buildServer.web.openapi.SimplePageExtension
import jetbrains.buildServer.web.openapi.CustomTab

import com.goldin.plugins.teamcity.messenger.MessagesContext

/**
 * Messenger extension
 */
class MessagesSendExtension extends SimplePageExtension implements CustomTab
{
    MessagesSendExtension ( PagePlaces pagePlaces, MessagesContext context )
    {
        super( pagePlaces, PlaceId.MY_TOOLS_TABS, context.pluginName, 'messagesSend.jsp' )
        register()
    }


    String getTabId    () { 'sendMessage'  }
    String getTabTitle () { 'Send Message' }
    boolean isVisible  () { true }
}
