package com.goldin.plugins.teamcity.messenger.api

import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires

/**
 * Bean sending messages and retrieving messages sent
 */
interface MessagesBean
{

   /**
    * Sends message specified.
    * @param message message to send
    * @return message id
    */
    @Requires({ message && ( message.id < 0 )})
    @Ensures({ result > 0 })
    long sendMessage( Message message )


    /**
     * Retrieves all messages sent.
     *
     * @return all messages sent
     */
    @Ensures({ result != null })
    List<Message> getAllMessages()


    /**
     * Retrieves messages addressed to user specified.
     * @param username recipient username
     * @return messages addressed to user specified
     */
    @Requires({ username })
    @Ensures({ result.isEmpty() || result.each{ Message m -> m.forUser( username ) } })
    List<Message> getMessagesForUser( String username )


    /**
     * Deletes message specified.
     * @param messageIds message id to delete
     * @return message deleted
     */
    @Requires({ ( messageIds != null ) && ( messageIds.every { it > 0 } ) })
    @Ensures({ ( result != null ) && ( result.size() <= messageIds.size()) })
    List<Message> deleteMessage( long ... messageIds )


    /**
     * Deletes message specified for specific user.
     * @param messageId message id to delete
     * @param username username of the person who deleted his message
     * @return message deleted
     */
    @Requires({ ( messageId > 0 ) && username })
    @Ensures({ result && ( result.id == messageId ) })
    Message deleteMessageByUser( long messageId, String username )


    /**
     * Persists all current messages on the disk
     */
    void persistMessages()
}