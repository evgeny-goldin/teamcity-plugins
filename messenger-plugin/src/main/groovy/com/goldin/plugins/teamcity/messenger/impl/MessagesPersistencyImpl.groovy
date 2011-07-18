package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import com.goldin.plugins.teamcity.messenger.api.MessagesPersistency
import jetbrains.buildServer.serverSide.ServerPaths
import net.sf.json.JSONObject
import org.gcontracts.annotations.Invariant
import org.gcontracts.annotations.Requires

/**
 * {@link MessagesPersistency} implementation
 */
@Invariant({
    this.dataDirectory.directory &&
    this.context                 &&
    this.jsonFile.file           &&
    this.jsonFile.canWrite()
})
class MessagesPersistencyImpl implements MessagesPersistency
{
    private final File            dataDirectory
    private final MessagesContext context
    private final File            jsonFile


    @Requires({ context && paths })
    MessagesPersistencyImpl ( MessagesContext context, ServerPaths paths )
    {
        this.dataDirectory = new File( paths.pluginDataDirectory, context.pluginName )
        this.context       = context
        this.jsonFile      = new File( dataDirectory, 'messages.json' )

        if ( ! dataDirectory.isDirectory()) { assert dataDirectory.mkdirs()  }
        if ( ! jsonFile.isFile())           { assert jsonFile.createNewFile()}
    }


    @Override
    void save ( Map data )
    {
        try
        {
            long t         = System.currentTimeMillis()
            int  nMessages = (( List<Map> ) data[ 'messages' ] ?: [] ).size()

            jsonFile.write( JSONObject.fromObject( data ).toString())

            context.log.with { debugEnabled && debug(
                 "Data of [$nMessages] message${ nMessages == 1 ? '' : 's' } " +
                 "persisted in [${ System.currentTimeMillis() - t }] ms" ) }
        }
        catch ( e )
        {
            context.log.error( "Failed to persist [$data] to [$jsonFile.canonicalPath]: $e", e )
        }
    }


    @Override
    Map restore ()
    {
        String jsonData = jsonFile.text

        try
        {
            long t         = System.currentTimeMillis()
            Map data       = ( jsonData ? JSONObject.fromObject( jsonData ) : [:] )
            int  nMessages = (( List<Map> ) data[ 'messages' ] ?: [] ).size()

            context.log.with { debugEnabled && debug(
                 "Data of [$nMessages] message${ nMessages == 1 ? '' : 's' } " +
                 "restored in [${ System.currentTimeMillis() - t }] ms" ) }

            return data
        }
        catch ( e )
        {
            def copyFile = new File( dataDirectory, "messages-failed-to-load-${ System.currentTimeMillis() }.json" )
            assert jsonFile.with{ renameTo( copyFile ) && createNewFile() }

            context.log.error( "Failed to read JSON data at [$jsonFile.canonicalPath], " +
                               "copied to [$copyFile.canonicalPath]: $e",
                               e )
            return [:]
        }
    }
}
