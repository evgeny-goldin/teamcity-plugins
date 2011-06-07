package com.goldin.plugins.teamcity.messenger.extension

import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PlaceId
import jetbrains.buildServer.web.openapi.SimplePageExtension
import jetbrains.buildServer.web.openapi.CustomTab
import com.goldin.plugins.teamcity.messenger.Constants

/**
 * Messenger extension
 */
class MessageSendExtension extends SimplePageExtension implements CustomTab
{
    MessageSendExtension ( PagePlaces pagePlaces, Constants constants )
    {
        super( pagePlaces, PlaceId.MY_TOOLS_TABS, constants.pluginName, 'messagesSend.jsp' )
        register()
    }


    String getTabId    () { 'sendMessage'  }
    String getTabTitle () { 'Send Message' }
    boolean isVisible  () { true }
}
