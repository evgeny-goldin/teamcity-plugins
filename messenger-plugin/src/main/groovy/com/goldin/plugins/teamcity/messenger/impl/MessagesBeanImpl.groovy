package com.goldin.plugins.teamcity.messenger.impl

import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires
import com.goldin.plugins.teamcity.messenger.api.*

/**
 * {@link MessagesBean} implementation
 */
class MessagesBeanImpl implements MessagesBean
{
    final MessagesTable   messagesTable
    final UsersTable      usersTable
    final MessagesContext context
    final MessagesUtil    util


    @Requires({ messagesTable && usersTable && context && util })
    MessagesBeanImpl ( MessagesTable messagesTable, UsersTable usersTable, MessagesContext context, MessagesUtil util )
    {
        this.messagesTable = messagesTable
        this.usersTable    = usersTable
        this.context       = context
        this.util          = util
    }
    

    @Override
    @Requires({ message && ( message.id < 0 )})
    @Ensures({ result > 0 })
    long sendMessage ( Message message )
    {
        usersTable.addMessage( messagesTable.addMessage( message ))
    }

    
    @Override
    @Requires({ username })
    @Ensures({ result.each{ it.forUser( username ) } })
    List<Message> getMessagesForUser ( String username )
    {
        List<Message> messages = []

        messages.addAll( usersTable.getMessagesForUser( username ))
        context.getUserGroups( username ).each { messages.addAll( usersTable.getMessagesForGroup( it )) }
        messages.addAll( usersTable.messagesForAll )

        util.sortForUser( messages.unique  { Message m1, Message m2 -> m1.id <=> m2.id }.
                                   findAll { Message m -> ! m.usersDeleted.contains( username )}.
                                   findAll { Message m -> messagesTable.containsMessage( m.id )},
                          username )
    }

    
    @Override
    @Requires({ messageId > 0 })
    @Ensures({ result && ( result.id == messageId ) && ( ! messagesTable.containsMessage( result.id )) })
    Message deleteMessage ( long messageId )
    {
        messagesTable.deleteMessage( messageId )
    }

    
    @Override
    @Requires({ ( messageId > 0  ) && username })
    @Ensures({ result && ( result.id == messageId ) && result.usersDeleted.contains( username ) })
    Message deleteMessageByUser ( long messageId, String username )
    {
        messagesTable.deleteMessageByUser( messageId, username )
    }
}
