package com.goldin.plugins.teamcity.messenger.api


/**
 * Spring and TC context-related properties
 */
interface MessagesContext
{
    String getPluginName()

    Set<String> getUserGroups( String username )
}
