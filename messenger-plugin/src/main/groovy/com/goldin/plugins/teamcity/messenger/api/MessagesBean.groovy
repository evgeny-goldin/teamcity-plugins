package com.goldin.plugins.teamcity.messenger.api


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
    long sendMessage( Message message )


    /**
     * Retrieves messages addressed to user specified.
     * @param username recipient username
     * @return messages addressed to user specified
     */
    List<Message> getMessagesForUser( String username )


    /**
     * Deletes message specified.
     * @param messageId message id to delete
     * @return message deleted
     */
    Message deleteMessage( long messageId )


    /**
     * Deletes message specified for specific user.
     * @param messageId message id to delete
     * @param username username of the person who deleted his message
     * @return message deleted
     */
    Message deleteMessageByUser( long messageId, String username )
}