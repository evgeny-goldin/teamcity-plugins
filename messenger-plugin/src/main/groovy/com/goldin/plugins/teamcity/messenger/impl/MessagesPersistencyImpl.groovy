package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.Message
import com.goldin.plugins.teamcity.messenger.api.MessagesPersistency
import jetbrains.buildServer.serverSide.ServerPaths
import org.gcontracts.annotations.Requires
import com.goldin.plugins.teamcity.messenger.api.MessagesContext

/**
 * {@link MessagesPersistency} implementation
 */
class MessagesPersistencyImpl implements MessagesPersistency
{

    private final File directory


    @Requires({ context && paths })
    MessagesPersistencyImpl ( MessagesContext context, ServerPaths paths )
    {
        directory = new File( paths.pluginDataDirectory, context.pluginName );
        assert ( directory.isDirectory() || directory.mkdirs());
    }


    @Override
    void persist ( List<Message> messages )
    {

    }

    @Override
    List<Message> restore ()
    {
        []
    }

}
