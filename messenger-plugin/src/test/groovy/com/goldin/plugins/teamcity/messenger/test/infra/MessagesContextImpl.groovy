package com.goldin.plugins.teamcity.messenger.test.infra

import com.goldin.plugins.teamcity.messenger.api.MessagesContext

/**
 * Test {@link MessagesContext} implementation
 */
class MessagesContextImpl implements MessagesContext
{
    @Override
    String getPluginName () { 'messenger-plugin' }

    @Override
    Set<String> getUserGroups ( String username ) { [ 'testGroup' ] }
}
