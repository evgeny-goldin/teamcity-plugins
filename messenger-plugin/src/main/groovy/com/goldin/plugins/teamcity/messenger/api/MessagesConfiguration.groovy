package com.goldin.plugins.teamcity.messenger.api

import org.gcontracts.annotations.Ensures

/**
 * Configuration data
 */
interface MessagesConfiguration
{

    boolean isMinify ()

    
    @Ensures({ result > 0 })
    int    getAjaxRequestInterval()


    @Ensures({ result > 0 })
    int    getPersistencyInterval()


    @Ensures({ result > 0 })
    int    getMessagesLimitPerUser()


    @Ensures({ result > 0 })
    int    getMessageLengthLimit()


    @Ensures({ result })
    String getDateFormatPattern()


    @Ensures({ result })
    String getTimeFormatPattern()
}
