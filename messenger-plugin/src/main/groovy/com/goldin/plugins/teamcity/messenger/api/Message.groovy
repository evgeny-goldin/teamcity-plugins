package com.goldin.plugins.teamcity.messenger.api

import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires
import org.gcontracts.annotations.Invariant
import groovy.transform.ToString


/**
 * Message data container
 */
@Invariant({
    ( this.timestamp > 0 )        &&
    ( this.sender                 &&
      this.urgency                &&
      this.text )                 &&
    ( this.sendToGroups != null ) &&
    ( this.sendToUsers  != null ) &&
    ( this.usersDeleted != null )
})
@ToString
final class Message
{
   /**
    * Urgency levels should be specified from most to least critical, it affects messages sorting.
    */
    public enum Urgency { CRITICAL, WARNING, INFO }

    private final MessagesContext       context // Context instance
    private final MessagesConfiguration config  // Configuration instance
    private final MessagesUtil          util    // Util instance

    final long            id            // Message id, assigned when added to messages table: MessagesTable.addMessage()
    final long            timestamp     // Message creation timestamp
    final String          sender        // Message sender, a username
    final Urgency         urgency       // Message urgency
    final String          text          // Message text
    final long            longevity     // Message "longevity" in hours - for how long should it be kept
    final boolean         sendToAll     // Whether message should be sent to all users
    final Set<String>     sendToGroups  // Groups message should be sent to
    final Set<String>     sendToUsers   // Users message should be sent to
    final Set<String>     usersDeleted  // Users who deleted this message, usernames


    @Requires({ sender && urgency && text && ( sendToGroups != null ) && ( sendToUsers != null )})
    @Ensures({ this.id == -1 })
    Message ( String       sender,
              Urgency      urgency,
              String       text,
              long         longevity    = -1,
              boolean      sendToAll    = true,
              List<String> sendToGroups = [],
              List<String> sendToUsers  = [] )
    {
        this.id           = -1
        this.context      = null
        this.config       = null
        this.util         = null

        this.timestamp    = System.currentTimeMillis()
        this.sender       = sender
        this.urgency      = urgency
        this.text         = text
        this.longevity    = longevity
        this.sendToAll    = sendToAll
        this.sendToGroups = new HashSet<String>( sendToGroups ).asImmutable()
        this.sendToUsers  = new HashSet<String>( sendToUsers  ).asImmutable()
        this.usersDeleted = []

        assert ( this.sendToAll || this.sendToGroups || this.sendToUsers ), "[$this] has no recipients"
    }


    @Requires({( id > 0 ) && context && config && util && message })
    @Ensures({ this.id == id })
    Message ( long id, MessagesContext context, MessagesConfiguration config, MessagesUtil util, Message message )
    {
        this.id           = id
        this.context      = context
        this.config       = config
        this.util         = util

        this.timestamp    = message.timestamp
        this.sender       = message.sender
        this.urgency      = message.urgency
        this.text         = message.text
        this.longevity    = message.longevity
        this.sendToAll    = message.sendToAll
        this.sendToGroups = message.sendToGroups
        this.sendToUsers  = message.sendToUsers
        this.usersDeleted = message.usersDeleted
    }


    @Requires({ persistencyData && context && config && util && persistencyData[ 'id' ] && persistencyData[ 'timestamp' ] })
    Message ( Map persistencyData, MessagesContext context, MessagesConfiguration config, MessagesUtil util )
    {
        this.id           = persistencyData[ 'id' ] as long
        this.context      = context
        this.config       = config
        this.util         = util

        this.timestamp    = persistencyData[ 'timestamp' ] as long
        this.sender       = persistencyData[ 'sender'    ]
        this.urgency      = (( String ) persistencyData[ 'urgency' ] ).toUpperCase() as Urgency
        this.text         = persistencyData[ 'text'      ]
        this.longevity    = persistencyData[ 'longevity' ] as long
        this.sendToAll    = Boolean.valueOf( persistencyData[ 'sendToAll' ])
        this.sendToGroups = new HashSet<String>(( Set ) persistencyData[ 'sendToGroups' ] ).asImmutable()
        this.sendToUsers  = new HashSet<String>(( Set ) persistencyData[ 'sendToUsers'  ] ).asImmutable()
        this.usersDeleted = new HashSet<String>(( Set ) persistencyData[ 'usersDeleted' ] )
    }


    @Override
    int hashCode () { id.hashCode() }


    @Override
    boolean equals ( Object object ) { ( object instanceof Message ) && (( Message ) object ).id == id }


    /**
     * Retrieves {@link Message} data to be sent over network to display it to user.
     * @return {@link Message} data to be sent over network to display it to user
     * @see com.goldin.plugins.teamcity.messenger.controller.MessagesDisplayController#handleRequest
     */
    @Requires({ ( this.id > 0 ) && this.context })
    @Ensures({ result && result[ 'id' ] && result[ 'text' ] })
    Map<String, String> getDisplayData ( boolean truncateText )
    {
        def date = new Date( timestamp )

        [ id         : id as String,
          urgency    : urgency.toString().toLowerCase( context.locale ),
          senderName : context.getUser( sender )?.descriptiveName ?: 'Test Sender',
          date       : config.dateFormatter.format( date ),
          time       : config.timeFormatter.format( date ),
          text       : ( truncateText && text.size() > config.messageLengthLimit ) ?
                           text.substring( 0, config.messageLengthLimit ) + ' ..' :
                           text
        ]
    }


    /**
     * Retrieves {@link Message} data to be sent to persistency storage.
     * @return {@link Message} data to be sent to persistency storage
     * @see com.goldin.plugins.teamcity.messenger.impl.MessagesPersistencyImpl#save
     */
    @Requires({ ( this.id > 0 ) && this.context })
    @Ensures({ result && result.id && result.text && result.sender })
    Map<String, String> getMessagePersistencyData ()
    {
        getDisplayData( false ) << [ sender       : sender,
                                     timestamp    : timestamp as String,
                                     longevity    : longevity,
                                     sendToAll    : sendToAll,
                                     sendToGroups : sendToGroups,
                                     sendToUsers  : sendToUsers,
                                     usersDeleted : usersDeleted ]
    }


    /**
     * Determines if message should be delivered to the group specified.
     *
     * @param groupName message recipient group name
     * @return true if message should be delivered to the group specified,
     *         false otherwise
     */
    @Requires({ groupName })
    boolean forGroup ( String groupName )
    {
        ( sendToAll || sendToGroups.contains( groupName ))
    }


    /**
     * Determines if message should be delivered to the user specified.
     *
     * @param username message recipient username
     * @return true if message should be delivered to the user specified,
     *         false otherwise
     */
    @Requires({ username })
    boolean forUser ( String username )
    {
        ( sendToAll ||
          sendToUsers.contains( username ) ||
          util.intersect ( sendToGroups, context.getUserGroups( username )))
    }
}
