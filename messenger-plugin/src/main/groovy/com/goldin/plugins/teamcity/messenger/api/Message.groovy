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

    final long            id
    final MessagesContext context
    final MessagesUtil    util

    final long            timestamp
    final String          sender
    final Urgency         urgency
    final String          message
    final long            longevity
    final boolean         sendToAll
    final Set<String>     sendToGroups
    final Set<String>     sendToUsers
    final Set<String>     usersDeleted


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
        this.message      = util.htmlEscape( message.message )
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
