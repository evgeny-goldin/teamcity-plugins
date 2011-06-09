package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.UsersTable
import com.goldin.plugins.teamcity.messenger.api.Message


/**
 * {@link UsersTable} implementation
 */
class UsersTableImpl implements UsersTable
{
    @Override
    void init (List<Message> messages)
    {

    }

    @Override
    List<Message> getMessagesForUser (String username)
    {
        []
    }

    @Override
    List<Message> getMessagesForGroup (String groupName)
    {
        []
    }

    @Override
    List<Message> getMessagesForAll (String groupName)
    {
        []
    }

    @Override
    long addMessage (Message message)
    {
        0
    }

}
