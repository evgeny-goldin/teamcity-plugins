package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import com.goldin.plugins.teamcity.messenger.api.MessagesPersistency
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import jetbrains.buildServer.serverSide.ServerPaths
import org.gcontracts.annotations.Requires
import org.gcontracts.annotations.Invariant


/**
 * {@link MessagesPersistency} implementation
 */
@Invariant({ this.dataFile != null })
class MessagesPersistencyImpl implements MessagesPersistency
{

    private final File dataFile


    @Requires({ context && paths })
    MessagesPersistencyImpl ( ServerPaths paths, MessagesContext context )
    {
        dataFile = new File( paths.pluginDataDirectory, "${ context.pluginName }/messages.json" )
    }


    @Override
    void persist ( Map data )
    {
        dataFile.write( new JsonBuilder( data ).toString())
    }


    @Override
    Map restore ()
    {
        ( dataFile.isFile() ? ( Map ) new JsonSlurper().parseText( dataFile.text ) : [:] )
    }
}
