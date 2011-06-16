package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.MessagesConfiguration
import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires

/**
 * {@link MessagesConfiguration} implementation
 */
class MessagesConfigurationImpl implements MessagesConfiguration
{
    final MessagesContext context

    @Requires({ context })
    MessagesConfigurationImpl ( MessagesContext context )
    {
        this.context = context
    }

    
    @Override
    @Ensures({ result > 0 })
    int getAjaxRequestInterval () { 300 }

    @Override
    @Ensures({ result > 0 })
    int getPersistencyInterval () { 600 }

    @Override
    @Ensures({ result > 0 })
    int getMessagesLimitPerUser () { 100 }

    @Override
    @Ensures({ result > 0 })
    int getMessageLengthLimit () { 100 }

    @Override
    @Ensures({ result })
    String getDateFormatPattern () { 'EEEEEEE, MMMMMM dd, yyyy \'at\' HH:mm' } // "Wed, Jun 15, 2011 at 17:03"
}

