package com.goldin.plugins.teamcity.messenger.api

import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.users.SUser
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires

/**
 * Spring and TC context-related properties
 */
interface MessagesContext
{
    boolean isTest()

    @Ensures({ result })
    Logger getLog()


    @Ensures({ result })
    String getPluginName()


    @Requires({ username })
    @Ensures({ isTest() || result })
    SUser getUser( String username )


    @Ensures({ result })
    Locale getLocale()


    @Requires({ username })
    @Ensures({ result })
    Set<String> getUserGroups( String username )
}
