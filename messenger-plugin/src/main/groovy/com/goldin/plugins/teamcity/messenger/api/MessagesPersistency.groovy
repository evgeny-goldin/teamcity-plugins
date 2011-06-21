package com.goldin.plugins.teamcity.messenger.api

import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires

/**
 * Messages persistency
 */
interface MessagesPersistency
{

    @Requires({ messages != null })
    void persist( List<Message> messages )


    @Ensures({ result != null })
    List<Message> restore()
}
