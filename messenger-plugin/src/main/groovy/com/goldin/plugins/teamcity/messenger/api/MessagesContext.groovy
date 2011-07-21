package com.goldin.plugins.teamcity.messenger.api

import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.users.SUser
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires


/**
 * Spring and TeamCity context-related properties
 */
interface MessagesContext
{
    /**
     * Determines if current context belongs to testing environment
     * @return true if current context belongs to testing environment, false otherwise
     */
    boolean isTest()


    /**
     * Retrieves logger to use for logging messages.
     * @return logger to use for logging messages
     */
    @Ensures({ result })
    Logger getLog()


    /**
     * Retrieves current user's locale.
     * @return user's locale
     */
    @Ensures({ result })
    Locale getLocale()


    /**
     * Retrieves current plugin name according to its metadata
     * @return current plugin name according to its metadata
     */
    @Ensures({ result })
    String getPluginName()


    /**
     * Retrieves a user object given its username.
     * @param username username of a user
     * @return user object or null if user is guest user
     */
    @Requires({ username })
    SUser getUser( String username )


    /**
     * Retrieves groups user specified belongs to.
     * @param username username of a user
     * @return groups user specified belongs to
     */
    @Requires({ username })
    @Ensures({ result != null })
    Set<String> getUserGroups( String username )
}
