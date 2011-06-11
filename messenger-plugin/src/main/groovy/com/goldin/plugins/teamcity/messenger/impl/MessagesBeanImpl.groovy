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
        Message m = messagesTable.addMessage( message )
        usersTable.addMessage( m )
        m.id
    }

    
    @Override
    @Requires({ username })
    @Ensures({ result.each{ it.forUser( username ) } })
    List<Message> getMessages ( String username )
    {
        Set<Message> messages = []

        messages << usersTable.getMessagesForUser( username )
        context.getUserGroups( username ).each { messages << usersTable.getMessagesForGroup( it ) }
        messages << usersTable.messagesForAll

        util.sort( messages.findAll { Message m -> ! m.usersDeleted.contains( username ) }.
                            findAll { Message m -> messagesTable.containsMessage( m )    },
                   username )
    }

    
    @Override
    @Requires({ messageId > 0 })
    @Ensures({ result && ( result.id == messageId ) && ( ! messagesTable.containsMessage( result )) })
    Message deleteMessage ( long messageId )
    {
        messagesTable.deleteMessage( messageId )
    }

    
    @Override
    @Requires({ ( messageId > 0  ) && username })
    @Ensures({ result && ( result.id == messageId ) && result.usersDeleted.contains( username ) })
    Message deleteMessage ( long messageId, String username )
    {
        messagesTable.deleteMessage( messageId, username )
    }
}
