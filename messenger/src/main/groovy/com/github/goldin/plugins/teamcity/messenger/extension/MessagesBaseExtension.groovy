package com.github.goldin.plugins.teamcity.messenger.extension

import com.github.goldin.plugins.teamcity.messenger.api.MessagesConfiguration
import com.github.goldin.plugins.teamcity.messenger.api.MessagesContext
import jetbrains.buildServer.serverSide.MainConfigProcessor
import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PlaceId
import jetbrains.buildServer.web.openapi.PositionConstraint
import jetbrains.buildServer.web.openapi.SimplePageExtension
import org.gcontracts.annotations.Requires
import org.jdom.Element

/**
 * Base class for all extensions
 */
abstract class MessagesBaseExtension extends SimplePageExtension implements MainConfigProcessor
{
    final MessagesContext       context
    final MessagesConfiguration config


//    @Ensures({ result != null })
    abstract List<String> getFilesToAdd()


    @Requires({ pagePlaces && placeId && includeUrl && position && context && config })
    protected MessagesBaseExtension ( PagePlaces pagePlaces, PlaceId placeId, String includeUrl, PositionConstraint position,
                                      MessagesContext context, MessagesConfiguration config )
    {
        super( pagePlaces, placeId, context.pluginName, includeUrl )

        this.position = position
        this.context  = context
        this.config   = config

        register()
    }


    @Override
    void readFrom ( Element rootElement )
    {
        config.readFrom( rootElement )

        jsPaths.clear()
        cssPaths.clear()

        for ( String fileName in ( [ 'jquery-ui-1.8.13.js', 'jquery-plugins.js', 'messages-constants.js',
                                     'jquery-ui-1.8.13.css', 'messenger-plugin.css' ] +
                                   filesToAdd ))
        {
            if ( fileName.endsWith( '.js' ))
            {
                addJsFile(  "js/${ config.minify  ? fileName.replace( '.js',  '.min.js'  ) : fileName }" )
            }
            else if ( fileName.endsWith( '.css' ))
            {
                addCssFile( "css/${ config.minify ? fileName.replace( '.css', '.min.css' ) : fileName }" )
            }
            else
            {
                assert false, "Resource file not recognized: [$fileName]"
            }
        }
    }


    @Override
    void writeTo ( Element parentElement ) { /* Keep CodeNarc happy */ true }
}
