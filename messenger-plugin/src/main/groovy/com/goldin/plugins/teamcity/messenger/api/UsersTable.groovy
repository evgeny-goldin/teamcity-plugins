package com.goldin.plugins.teamcity.messenger.api


/**
 * Users table
 */
interface UsersTable
{
    void init ( List<Message> messages )

    List<Message> getMessagesForUser ( String username  )

    List<Message> getMessagesForGroup( String groupName )

    List<Message> getMessagesForAll  ()

    long addMessage( Message message )
}
