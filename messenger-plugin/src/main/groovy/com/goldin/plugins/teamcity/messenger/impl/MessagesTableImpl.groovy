package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.Message
import com.goldin.plugins.teamcity.messenger.api.MessagesConfiguration
import com.goldin.plugins.teamcity.messenger.api.MessagesTable
import org.gcontracts.annotations.Requires

/**
 * {@link MessagesTable} implementation
 */
class MessagesTableImpl implements MessagesTable
{
    final MessagesConfiguration configuration


    @Requires({ configuration })
    MessagesTableImpl ( MessagesConfiguration configuration )
    {
        this.configuration = configuration
    }


    @Override
    Message addMessage (Message message)
    {
        null
    }

    @Override
    Message deleteMessage (long messageId)
    {
        null
    }

    @Override
    Message deleteMessage (long messageId, String username)
    {
        null
    }

    @Override
    List<Message> getAllMessages ()
    {
        []
    }

    @Override
    boolean containsMessage (Message m)
    {
        false
    }

    @Override
    void persist ()
    {

    }

    @Override
    void restore ()
    {

    }

}
