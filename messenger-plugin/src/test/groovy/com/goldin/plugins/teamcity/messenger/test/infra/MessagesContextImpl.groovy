package com.goldin.plugins.teamcity.messenger.test.infra

import com.goldin.plugins.teamcity.messenger.api.MessagesContext
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
    Logger getLog () { Logger.getInstance( 'com.goldin.plugins' ) }

    @Override
    String getPluginName () { 'messenger-plugin' }

    @Override
    SUser getUser ( String username ) { null }

    @Override
    Locale getLocale () { Locale.US }

    @Override
    Set<String> getUserGroups ( String username ) { [ Constants.TEST_GROUP ] }
}
