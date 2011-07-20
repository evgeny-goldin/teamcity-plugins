package com.goldin.plugins.teamcity.messenger.api

import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires


/**
 * Main messages storage
 */
interface MessagesTable
{

   /**
    * Adds new message to the system.
    * @param message message to add
    * @return new message based on the message provided with id auto-generated
    */
    @Requires({ message && ( message.id < 0 ) })
    @Ensures({ ( result.id > 0 ) && ( result.timestamp == message.timestamp ) })
    Message addMessage( Message message )


    /**
     * Retrieves message using its id.
     * @param messageId id of the message to retrieve
     * @return message with id specified
     */
    @Requires({ messageId > 0 })
    @Ensures({ result.id == messageId })
    Message getMessage( long messageId )


    /**
     * Deletes messages provided their message ids.
     * @param messageIds ids of messages to delete
     * @return messages deleted
     */
    @Requires({ ( messageIds != null ) && messageIds.every { it > 0 } })
    @Ensures({ messageIds.every { ! containsMessage( it ) } && result.every { ! containsMessage( it.id ) } })
    List<Message> deleteMessage( long ... messageIds )


    /**
     * Deletes message specified by user provided.
     * @param messageId id of the message to delete
     * @param username username of a user deleting the message
     * @return message deleted if it was deleted,
     *         null if message was already deleted
     */
    @Requires({ ( messageId > 0 ) && username })
    @Ensures({ ( result == null ) || ( result.id == messageId ) })
    Message deleteMessageByUser ( long messageId, String username )


    /**
     * Retrieves <b>copy</b> of all messages stored.
     * @return copy of all messages stored
     */
    @Ensures({ result != null })
    List<Message> getAllMessages()


    /**
     * Determines if messages table contains message with id specified.
     * @param messageId id of message to check
     * @return true if messages table contains message with id specified, false otherwise
     */
    @Requires({ messageId > 0 })
    boolean containsMessage( long messageId )


    /**
     * Retrieves number of currently stored messages.
     * @return number of currently stored messages
     */
    @Ensures({ result > -1 })
    int getNumberOfMessages()


    /**
     * Retrieves messages persistency data.
     * @return messages persistency data
     */
    @Ensures({ result })
    Map getPersistencyData()


    /**
     * Restores messages table from its persistency data.
     * @param data persistency data to restore messages table from
     */
    @Requires({ data })
    void restoreFromPersistencyData ( Map data )
}
