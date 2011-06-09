package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.Message
import com.goldin.plugins.teamcity.messenger.api.MessagesConfiguration
import com.goldin.plugins.teamcity.messenger.api.UsersTable
import org.gcontracts.annotations.Requires

/**
 * {@link UsersTable} implementation
 */
class UsersTableImpl implements UsersTable
{

    final MessagesConfiguration configuration

    @Requires({ configuration })
    UsersTableImpl ( MessagesConfiguration configuration )
    {
        this.configuration = configuration
    }

    
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
