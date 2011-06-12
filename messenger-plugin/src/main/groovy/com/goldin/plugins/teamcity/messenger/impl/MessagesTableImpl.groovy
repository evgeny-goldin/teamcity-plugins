package com.goldin.plugins.teamcity.messenger.impl

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires
import com.goldin.plugins.teamcity.messenger.api.*


/**
 * {@link MessagesTable} implementation
 */
class MessagesTableImpl implements MessagesTable
{
    final MessagesConfiguration configuration
    final MessagesContext       context
    final MessagesUtil          util

    final private Map<Long, Message> messages           = new ConcurrentHashMap( 128, 0.75f, 10 )
    final private AtomicLong         messageIdGenerator = new AtomicLong( 1000  )


    @Requires({ configuration && context && util })
    MessagesTableImpl ( MessagesConfiguration configuration, MessagesContext context, MessagesUtil util )
    {
        this.configuration = configuration
        this.context       = context
        this.util          = util
    }


    /**
     * Generates new message id.
     * @return new message id
     */
    @Ensures({ ( result > 0 ) && ( ! messages.containsKey( result )) })
    private long nextMessageId() { messageIdGenerator.incrementAndGet() }


    @Override
    @Requires({ message && ( message.id < 0 ) && message.usersDeleted.isEmpty() })
    @Ensures({  result  && ( result.id  > 0 ) && ( result.timestamp == message.timestamp ) && result.message.is( message.message ) })
    Message addMessage ( Message message )
    {
        long    messageId  = nextMessageId()
        Message newMessage = new Message( messageId, context, util, message )
        Message previous   = messages.put( messageId, newMessage )
        assert  previous  == null, "Message with new id [$messageId] already existed: [$previous]"

        newMessage
    }


    @Override
    @Requires({ ( messageId > 0 ) && messages.containsKey( messageId ) })
    @Ensures({ result && ( result.id == messageId ) })
    Message getMessage ( long messageId )
    {
        messages[ messageId ]
    }


    @Override
    @Requires({ ( messageId > 0 ) && messages.containsKey( messageId ) })
    @Ensures({ result && ( result.id == messageId ) && ( ! messages.containsKey( messageId )) })
    Message deleteMessage ( long messageId )
    {
        messages.remove( messageId )
    }


    @Override
    @Requires({ ( messageId > 0 ) && messages.containsKey( messageId ) && username })
    @Ensures({ result && ( result.id == messageId ) && result.usersDeleted.contains( username ) })
    Message deleteMessageByUser ( long messageId, String username )
    {
        Message m = messages[ messageId ]
        assert  m.forUser( username ), "[$m] is not for user [$username], can not be deleted by him"
        
        m.usersDeleted << username
        if (( ! m.sendToAll ) && ( ! m.sendToGroups ) && ( m.usersDeleted.containsAll( m.sendToUsers )))
        {
            deleteMessage( m.id )
        }
        m
    }

    
    @Override
    void deleteAllMessages ()
    {
        messages.clear()
    }


    @Override
    @Ensures({ result != null })
    List<Message> getAllMessages ()
    {
        new ArrayList<Message>( messages.values())
    }

    
    @Override
    @Requires({ messageId > 0 })
    boolean containsMessage ( long messageId )
    {
        messages.containsKey( messageId )
    }

    
    @Override
    int getNumberOfMessages ()
    {
        messages.size()
    }


    @Override
    void persist ()
    {
        // Save messages table
        // Save nextMessageId
    }

    
    @Override
    void restore ()
    {
        // Restore messages table
        // Restore nextMessageId
        // UsersTable.init()
    }
}
