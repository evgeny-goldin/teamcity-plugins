package com.github.goldin.plugins.teamcity.messenger.test.infra

import com.github.goldin.plugins.teamcity.messenger.api.MessagesContext
import jetbrains.buildServer.users.SUser
import com.intellij.openapi.diagnostic.Logger

/**
 * Test {@link MessagesContext} implementation
 */
class MessagesContextImpl implements MessagesContext
{
    @Override
    boolean isTest () { true }

    @Override
    Logger getLog () { Logger.getInstance( 'com.github.goldin.plugins' ) }

    @Override
    String getPluginName () { Constants.PLUGIN_NAME }

    @Override
    SUser getUser ( String username ) { null }

    @Override
    Locale getLocale () { Locale.US }

    @Override
    Set<String> getUserGroups ( String username ) { [ Constants.TEST_GROUP ] }
}
