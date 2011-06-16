package com.goldin.plugins.teamcity.messenger.api


/**
 * Configuration data
 */
interface MessagesConfiguration
{
    int    getAjaxRequestInterval()
    int    getPersistencyInterval()
    int    getMessagesLimitPerUser()
    int    getMessageLengthLimit()
    String getDateFormatPattern()
}
