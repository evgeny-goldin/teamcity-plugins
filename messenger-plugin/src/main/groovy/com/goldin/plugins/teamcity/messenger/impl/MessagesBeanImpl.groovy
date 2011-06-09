package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.Message
import com.goldin.plugins.teamcity.messenger.api.MessagesBean
import com.goldin.plugins.teamcity.messenger.api.MessagesTable
import com.goldin.plugins.teamcity.messenger.api.UsersTable
import org.gcontracts.annotations.Requires

/**
 * {@link MessagesBean} implementation
 */
class MessagesBeanImpl implements MessagesBean
{

    final MessagesTable messagesTable
    final UsersTable    usersTable


    @Requires({ messagesTable && usersTable })
    MessagesBeanImpl ( MessagesTable messagesTable, UsersTable usersTable )
    {
        this.messagesTable = messagesTable
        this.usersTable    = usersTable
    }
    

    @Override
    long sendMessage (Message message)
    {
        0
    }

    @Override
    List<Message> getMessagesForUser (String username)
    {
        []
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

}
