package com.goldin.plugins.teamcity.messenger.api

import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires


/**
 * Message data container
 */
final class Message
{
   /**
    * Urgency levels should be specified from most to least critical, it affects messages sorting.
    */
    public enum Urgency { CRITICAL, WARNING, INFO }

    private final MessagesContext context // Context instance
    private final MessagesUtil    util    // Util instance

    final long            id            // Message id, assigned when added to messages table: MessagesTable.addMessage()
    final long            timestamp     // Message creation timestamp
    final String          sender        // Message sender, a username
    final Urgency         urgency       // Message urgency
    final String          message       // Message text
    final long            longevity     // Message "longevity", for how many hours should it be kept alive
    final boolean         sendToAll     // Whether message should be sent to all users
    final Set<String>     sendToGroups  // Groups message should be sent to
    final Set<String>     sendToUsers   // Users message should be sent to
    final Set<String>     usersDeleted  // Users who deleted this message, usernames


    @Requires({ sender && urgency && message && ( sendToGroups != null ) && ( sendToUsers != null ) })
    @Ensures({ ( this.id == -1 ) && ( this.sendToGroups != null ) && ( this.sendToUsers != null ) && ( this.usersDeleted != null ) })
    Message ( String       sender,
              Urgency      urgency,
              String       message,
              long         longevity    = -1,
              boolean      sendToAll    = true,
              List<String> sendToGroups = [],
              List<String> sendToUsers  = [] )
    {
        this.id           = -1
        this.context      = null
        this.util         = null

        this.timestamp    = System.currentTimeMillis()
        this.sender       = sender
        this.urgency      = urgency
        this.message      = message
        this.longevity    = longevity
        this.sendToAll    = sendToAll
        this.sendToGroups = new HashSet<String>( sendToGroups ).asImmutable()
        this.sendToUsers  = new HashSet<String>( sendToUsers  ).asImmutable()
        this.usersDeleted = []

        assert ( this.sendToAll || this.sendToGroups || this.sendToUsers ), "[$this] has no recipients"
    }


    @Requires({( id > 0 ) && context && util && message })
    @Ensures({ ( this.id == id ) && this.context.is( context ) && this.util.is( util ) })
    Message ( long id, MessagesContext context, MessagesUtil util, Message message )
    {
        this.id           = id
        this.context      = context
        this.util         = util

        this.timestamp    = message.timestamp
        this.sender       = message.sender
        this.urgency      = message.urgency
        this.message      = message.message
        this.longevity    = message.longevity
        this.sendToUsers  = message.sendToUsers
        this.sendToGroups = message.sendToGroups
        this.sendToAll    = message.sendToAll
        this.usersDeleted = message.usersDeleted
    }


    @Override
    int hashCode () { id.hashCode() }


    @Override
    boolean equals ( Object object ) { ( object instanceof Message ) && (( Message ) object ).id == id }


    /**
     * Retrieves {@link Message} data to be sent over JSON to display it to user.
     * @return {@link Message} data to be sent over JSON to display it to user
     * @see com.goldin.plugins.teamcity.messenger.controller.MessagesDisplayController#handleRequest
     */
    @Requires({ ( this.id > 0 ) && this.context })
    @Ensures({ result && result.id && result.text })
    Map<String, String> displayData()
    {
        [ id        : id as String,
          urgency   : urgency.toString().toLowerCase( context.locale ),
          sender    : context.getUser( sender ).descriptiveName,
          text      : message,
          timestamp : timestamp as String ]
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

    @Override
    String toString ()
    {
        "Message [$id]"
    }
}
