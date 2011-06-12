package com.goldin.plugins.teamcity.messenger.api

import jetbrains.buildServer.users.SUser

/**
 * Spring and TC context-related properties
 */
interface MessagesContext
{
    boolean isTest()

    String getPluginName()

    SUser getUser( String username )

    Set<String> getUserGroups( String username )
}
