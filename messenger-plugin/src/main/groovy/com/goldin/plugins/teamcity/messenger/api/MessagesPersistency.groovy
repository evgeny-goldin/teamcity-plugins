package com.goldin.plugins.teamcity.messenger.api


/**
 * Messages persistency
 */
interface MessagesPersistency
{
    void persist( List<Message> messages )

    List<Message> restore()
}
