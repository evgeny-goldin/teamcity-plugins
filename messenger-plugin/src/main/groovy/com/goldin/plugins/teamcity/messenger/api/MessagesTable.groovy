package com.goldin.plugins.teamcity.messenger.api


/**
 * Messages storage
 */
interface MessagesTable
{
    Message addMessage( Message message )

    Message getMessage( long messageId )

    Message deleteMessage( long messageId )
    
    Message deleteMessageByUser ( long messageId, String username )

    List<Message> getAllMessages()

    boolean containsMessage( long messageId )

    int getNumberOfMessages()

    void deleteAllMessages()

    void persist()
    
    void restore()
}
