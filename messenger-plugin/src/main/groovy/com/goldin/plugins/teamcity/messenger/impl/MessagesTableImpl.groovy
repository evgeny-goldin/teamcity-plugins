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
    private long getNextMessageId () { messageIdGenerator.incrementAndGet() }


    @Override
    @Requires({ message.usersDeleted.isEmpty() })
    Message addMessage ( Message message )
    {
        long    messageId  = nextMessageId
        Message newMessage = new Message( messageId, context, util, message )
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
    @Requires({  messages.containsKey( messageId ) })
    @Ensures({ ! messages.containsKey( messageId ) })
    Message deleteMessage ( long messageId )
    {
        messages.remove( messageId )
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
        [ nextMessageId : nextMessageId,
          messages      : allMessages*.persistencyData ] // List of Maps, one Map per Message
    }

    
    @Override
    @Requires({ data.isEmpty() || ( data[ 'nextMessageId' ] && data[ 'messages' ] ) })
    void readPersistencyData( Map data )
    {
        if ( data )
        {
            messageIdGenerator.set( data[ 'nextMessageId' ] as long )

            for ( Map m in data[ 'messages' ] )
            {
                /**
                 * As written by {@link Message#getPersistencyData}
                 */
                
                def messageId    = m[ 'id'           ] as long
                def timestamp    = m[ 'timestamp'    ]
                def sender       = m[ 'sender'       ]
                def urgency      = m[ 'urgency'      ]
                def text         = m[ 'text'         ]
                def longevity    = m[ 'longevity'    ]
                def sendToAll    = m[ 'sendToAll'    ]
                def sendToGroups = m[ 'sendToGroups' ]
                def sendToUsers  = m[ 'sendToUsers'  ]
                def usersDeleted = m[ 'usersDeleted' ]

                int j = 5
            }
        }
    }
}
