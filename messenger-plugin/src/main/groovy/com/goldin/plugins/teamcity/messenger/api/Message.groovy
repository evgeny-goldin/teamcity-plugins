package com.goldin.plugins.teamcity.messenger.api

import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires

/**
 * Message data container
 */
final class Message
{
    public enum Urgency { INFO, WARNING, CRITICAL }

    static final MessagesUtil UTIL = new MessagesUtil()

    final long         id
    final long         timestamp
    final String       sender
    final Urgency      urgency
    final String       message
    final long         longevity
    final boolean      sendToAll
    final List<String> sendToGroups
    final List<String> sendToUsers
    final List<String> usersDeleted


    @Requires({ sender && urgency && message && ( sendToGroups != null ) && ( sendToUsers != null ) })
    @Ensures({ ! result.message.with{ contains( '<' ) || contains( '>' ) }})
    Message ( String       sender,
              Urgency      urgency,
              String       message,
              long         longevity    = -1,
              boolean      sendToAll    = true,
              List<String> sendToGroups = [],
              List<String> sendToUsers  = [] )
    {
        assert sender
        assert urgency
        assert message

        this.id           = -1
        this.timestamp    = System.currentTimeMillis()
        this.sender       = sender
        this.urgency      = urgency
        this.message      = UTIL.htmlEscape( message )
        this.longevity    = longevity
        this.sendToAll    = sendToAll
        this.sendToGroups = new ArrayList<String>( sendToGroups ).asImmutable()
        this.sendToUsers  = new ArrayList<String>( sendToUsers  ).asImmutable()
        this.usersDeleted = []
    }


    Message ( long id, Message message )
    {
        this.id           = id
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


    @Override
    String toString ()
    {
        ''
    }
}
