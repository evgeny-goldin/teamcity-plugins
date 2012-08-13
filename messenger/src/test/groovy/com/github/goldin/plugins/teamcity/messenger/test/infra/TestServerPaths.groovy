package com.github.goldin.plugins.teamcity.messenger.test.infra

import jetbrains.buildServer.serverSide.ServerPaths

/**
 * {@link ServerPaths} test implementation
 */
class TestServerPaths extends ServerPaths
{
    TestServerPaths ()
    {
        super( "${ System.getProperty( 'user.home' ) }/.BuildServer" )
    }

    @Override
    File getPluginDataDirectory () { new File ( Constants.MESSAGES_DIR ) }
}
