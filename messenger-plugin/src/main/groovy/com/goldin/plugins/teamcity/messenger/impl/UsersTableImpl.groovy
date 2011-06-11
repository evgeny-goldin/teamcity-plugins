package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.Message
import com.goldin.plugins.teamcity.messenger.api.MessagesConfiguration
import com.goldin.plugins.teamcity.messenger.api.UsersTable
import org.gcontracts.annotations.Ensures
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
    @Requires({ messages })
    void init ( List<Message> messages )
    {

    }

    
    @Override
    @Requires({ username })
    List<Message> getMessagesForUser ( String username )
    {
        []
    }


    @Override
    @Requires({ groupName })
    List<Message> getMessagesForGroup ( String groupName )
    {
        []
    }


    @Override
    @Ensures({ result != null })
    List<Message> getMessagesForAll ()
    {
        []
    }


    @Override
    @Requires({ message && ( message.id > 0 ) })
    long addMessage ( Message message )
    {
        0
    }
}
