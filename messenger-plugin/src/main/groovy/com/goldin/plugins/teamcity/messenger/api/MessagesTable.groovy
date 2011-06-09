package com.goldin.plugins.teamcity.messenger.api


/**
 * Messages storage
 */
interface MessagesTable
{
    long addMessage( Message message )

    Message deleteMessage( long messageId )
    
    Message deleteMessage( long messageId, String username )

    List<Message> getAllMessages()

    boolean containsMessage( Message m )

    void persist()
    
    void restore()
}
