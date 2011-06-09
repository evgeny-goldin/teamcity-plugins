package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.MessagesConfiguration


/**
 * {@link MessagesConfiguration} implementation
 */
class MessagesConfigurationImpl implements MessagesConfiguration
{
    @Override
    int getAjaxRequestInterval () { 300 }

    @Override
    int getPersistencyInterval () { 600 }

    @Override
    int getMessagesLimitPerUser () { 100 }
}
