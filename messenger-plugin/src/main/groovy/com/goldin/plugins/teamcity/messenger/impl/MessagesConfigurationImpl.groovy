package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.MessagesConfiguration
import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import jetbrains.buildServer.serverSide.MainConfigProcessor
import org.gcontracts.annotations.Requires
import org.jdom.Element


/**
 * {@link MessagesConfiguration} implementation
 */
class MessagesConfigurationImpl implements MessagesConfiguration, MainConfigProcessor
{
    final MessagesContext context

    @Requires({ context })
    MessagesConfigurationImpl ( MessagesContext context )
    {
        this.context = context
    }

    @Override
    boolean minify () { true }

    @Override
    int getAjaxRequestInterval () { 20 }

    @Override
    int getPersistencyInterval () { 600 }

    @Override
    int getMessagesLimitPerUser () { 100 }

    @Override
    int getMessageLengthLimit () { 100 }

    @Override
    String getDateFormatPattern () { 'EEEEEEE, MMMMMM dd, yyyy' } // "Wed, Jun 15, 2011"

    @Override
    String getTimeFormatPattern () { 'HH:mm' }                    // "17:03"

    
    @Override
    void readFrom ( Element rootElement )
    {
        System.out.println( 'readFromreadFromreadFromreadFromreadFromreadFromreadFromreadFromreadFromreadFromreadFromreadFrom' );
    }


    @Override
    void writeTo ( Element parentElement )
    {
        System.out.println( 'writeTowriteTowriteTowriteTowriteTowriteTowriteTowriteTowriteTowriteTowriteTowriteTowriteTowriteTowriteTo' );
    }
}

