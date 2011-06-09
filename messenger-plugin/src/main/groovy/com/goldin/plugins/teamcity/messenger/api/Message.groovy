package com.goldin.plugins.teamcity.messenger.api

/**
 * Message data container
 */
class Message
{
    public enum Urgency { INFO, WARNING, CRITICAL }

    final long         id
    final long         timestamp
    final String       sender
    final Urgency      urgency
    final String       message
    final long         longevity
    final List<String> sendToUsers
    final List<String> sendToGroups
    final boolean      sendToAll
    final List<String> usersDeleted


    public Message ( String       sender,
                     Urgency      urgency,
                     String       message,
                     long         longevity,
                     List<String> sendToUsers,
                     List<String> sendToGroups,
                     boolean      sendToAll )
    {
        this.sender       = sender
        this.urgency      = urgency
        this.message      = message
        this.longevity    = longevity
        this.sendToUsers  = new ArrayList<String>( sendToUsers ).asImmutable()
        this.sendToGroups = new ArrayList<String>( sendToGroups ).asImmutable()
        this.sendToAll    = sendToAll
        this.usersDeleted = []
    }


    public Message ( long id, Message message )
    {
        this.id           = id
        this.timestamp    = System.currentTimeMillis()
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
