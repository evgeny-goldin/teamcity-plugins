package com.github.goldin.plugins.teamcity.messenger.api

import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires


/**
 * Users table storing per-user messages
 */
interface UsersTable
{
   /**
    * Restores users table using messages provided.
    * @param messages messages to restore users table from
    */
    @Requires({ messages != null })
    void restore ( List<Message> messages )


    /**
     * Retrieves messages addressed to user specified.
     * @param username username of a user
     * @return messages addressed to user specified
     */
    @Requires({ username })
    @Ensures({ result != null })
    List<Message> getMessagesForUser ( String username  )


    /**
     * Retrieves messages addressed to group specified.
     * @param groupName name of a group
     * @return messages addressed to group specified
     */
    @Requires({ groupName })
    @Ensures({ result != null })
    List<Message> getMessagesForGroup( String groupName )


    /**
     * Retrieves messages addressed for all users.
     * @return messages addressed for all users
     */
    @Ensures({ result != null })
    List<Message> getMessagesForAll  ()


    /**
     * Adds new message.
     * @param message message to add
     * @return id of message added
     */
    @Requires({ message && ( message.id > 0 ) })
    @Ensures({ result == message.id })
    long addMessage( Message message )
}
