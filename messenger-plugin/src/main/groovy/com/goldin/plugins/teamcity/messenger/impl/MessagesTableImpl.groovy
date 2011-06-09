package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.MessagesTable
import com.goldin.plugins.teamcity.messenger.api.Message


/**
 * {@link MessagesTable} implementation
 */
class MessagesTableImpl implements MessagesTable
{
    @Override
    long addMessage (Message message)
    {
        0
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
