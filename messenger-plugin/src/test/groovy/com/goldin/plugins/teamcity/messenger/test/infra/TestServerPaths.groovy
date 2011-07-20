package com.goldin.plugins.teamcity.messenger.test.infra

import jetbrains.buildServer.serverSide.ServerPaths

/**
 * {@link ServerPaths} test implementation
 */
class TestServerPaths extends ServerPaths
{
    @Override
    File getPluginDataDirectory () { new File ( Constants.MESSAGES_DIR ) }
}
