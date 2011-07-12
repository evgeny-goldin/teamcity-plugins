package com.goldin.plugins.teamcity.messenger.impl

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires
import com.goldin.plugins.teamcity.messenger.api.*
import org.gcontracts.annotations.Invariant


/**
 * {@link MessagesTable} implementation
 */
@Invariant({ this.context && this.config && this.util &&
             ( this.messages != null ) && ( this.messageIdGenerator != null )})
class MessagesTableImpl implements MessagesTable
{
    private final MessagesContext       context
    private final MessagesConfiguration config
    private final MessagesUtil          util
    private final Map<Long, Message>    messages
    private final AtomicLong            messageIdGenerator


    @Requires({ context && config && util })
    MessagesTableImpl ( MessagesContext context, MessagesConfiguration config, MessagesUtil util )
    {
        this.context            = context
        this.config             = config
        this.util               = util
        this.messages           = new ConcurrentHashMap( 128, 0.75f, 10 )
        this.messageIdGenerator = new AtomicLong( 1000 )
    }


    /**
     * Generates new message id.
     * @return new message id
     */
    @Ensures({ ( result > 0 ) && ( ! messages.containsKey( result )) })
    private long getNextMessageId () { messageIdGenerator.incrementAndGet() }


    @Override
    @Requires({ message.usersDeleted.isEmpty() })
    Message addMessage ( Message message )
    {
        long    messageId  = nextMessageId
        Message newMessage = new Message( messageId, context, config, util, message )
        Message previous   = messages.put( messageId, newMessage )
        assert  previous  == null, "Message with new id [$messageId] already existed: [$previous]"

        newMessage
    }


    @Override
    @Requires({ messages.containsKey( messageId ) })
    Message getMessage ( long messageId )
    {
        messages[ messageId ]
    }


    @Override
    List<Message> deleteMessage ( long ... messageIds )
    {
        messageIds.collect { messages.remove( it ) }.findAll { it }
    }


    @Override
    @Requires({ messages.containsKey( messageId ) && username })
    @Ensures({ result.usersDeleted.contains( username ) })
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
    List<Message> getAllMessages ()
    {
        new ArrayList<Message>( messages.values())
    }


    @Override
    boolean containsMessage ( long messageId )
    {
        messages.containsKey( messageId )
    }


    @Override
    int getNumberOfMessages ()
    {
        messages.size()
    }


    Map getPersistencyData()
    {
        [ messageId : messageIdGenerator.get(),
          // 'messages' is List of Maps (one Map per Message), sorted chronologically
          messages  : allMessages.sort{ Message m1, Message m2 -> m1.timestamp <=> m2.timestamp }*.
                                  messagePersistencyData ]
    }


    @Override
    @Requires({ data.isEmpty() || ( data[ 'messageIds' ] && ( data[ 'messages' ] != null )) })
    void setPersistencyData ( Map data )
    {
        if ( data )
        {
            messageIdGenerator.set( data[ 'messageIds' ] as long )

            for ( Map messagePersistencyData in data[ 'messages' ] )
            {
                long      messageId   = messagePersistencyData[ 'id' ] as long
                messages[ messageId ] = new Message( messagePersistencyData, context, config, util )
            }
        }
    }
}
