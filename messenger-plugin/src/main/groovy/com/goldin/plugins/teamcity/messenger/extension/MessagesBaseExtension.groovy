package com.goldin.plugins.teamcity.messenger.extension

import com.goldin.plugins.teamcity.messenger.api.MessagesConfiguration
import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PlaceId
import jetbrains.buildServer.web.openapi.PositionConstraint
import jetbrains.buildServer.web.openapi.SimplePageExtension
import org.gcontracts.annotations.Requires

/**
 * Base class for all extensions
 */
abstract class MessagesBaseExtension extends SimplePageExtension
{
    final MessagesContext       context
    final MessagesConfiguration config


//    @Ensures({ result != null })
    abstract List<String> getFilesToAdd()


    @Requires({ pagePlaces && placeId && includeUrl && position && context && config })
    MessagesBaseExtension ( PagePlaces pagePlaces, PlaceId placeId, String includeUrl, PositionConstraint position,
                            MessagesContext context, MessagesConfiguration config )
    {
        super( pagePlaces, placeId, context.pluginName, includeUrl )

        setPosition( position )
        
        this.context = context
        this.config  = config
        
        register()

        for ( String fileName in ( [ 'jquery-ui-1.8.13.js',  'jquery-plugins.js',
                                     'jquery-ui-1.8.13.css', 'messenger-plugin.css' ] +
                                   filesToAdd ))
        {
            if ( fileName.endsWith( '.js' ))
            {
                addJsFile( "js/${ config.minify()   ? fileName.replace( '.js',  '.min.js'  ) : fileName }" )
            }
            else if ( fileName.endsWith( '.css' ))
            {
                addCssFile( "css/${ config.minify() ? fileName.replace( '.css', '.min.css' ) : fileName }" )
            }
            else
            {
                assert false, "Resource file not recognized: [$fileName]"
            }
        }
    }
}
