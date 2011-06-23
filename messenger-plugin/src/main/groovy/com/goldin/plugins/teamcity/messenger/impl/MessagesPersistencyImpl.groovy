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
@Invariant({ this.context && this.jsonFile })
class MessagesPersistencyImpl implements MessagesPersistency
{
    private final MessagesContext context
    private final File            jsonFile


    @Requires({ context && paths })
    MessagesPersistencyImpl ( MessagesContext context, ServerPaths paths )
    {
        this.context  = context
        this.jsonFile = new File( paths.pluginDataDirectory, "${ context.pluginName }/messages.json" )
    }


    @Override
    void persist ( Map data )
    {
        try
        {
            jsonFile.write( new JsonBuilder( data ).toString())
        }
        catch ( e )
        {
            context.log.error( "Failed to persist [$data] to [$jsonFile.canonicalPath]: $e", e )
        }
    }


    @Override
    Map restore ()
    {
        try
        {
            return ( jsonFile.isFile() ? ( Map ) new JsonSlurper().parseText( jsonFile.text ) : [:] )
        }
        catch ( e )
        {
            context.log.error( "Failed to restore JSON data from [$jsonFile.canonicalPath]: $e", e )
            return [:]
        }
    }
}
