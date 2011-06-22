package com.goldin.plugins.teamcity.messenger.api

import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires

/**
 * Messages persistency
 */
interface MessagesPersistency
{

    @Requires({ data })
    void persist( Map data )


    @Ensures({ result })
    Map restore()
}
