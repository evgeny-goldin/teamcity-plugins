package com.goldin.plugins.teamcity.messenger.test.infra

import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import jetbrains.buildServer.users.SUser


/**
 * Test {@link MessagesContext} implementation
 */
class MessagesContextImpl implements MessagesContext
{
    @Override
    boolean isTest () { true }

    @Override
    String getPluginName () { 'messenger-plugin' }

    @Override
    SUser getUser ( String username ) { null }

    @Override
    Set<String> getUserGroups ( String username ) { [ Constants.TEST_GROUP ] }
}
