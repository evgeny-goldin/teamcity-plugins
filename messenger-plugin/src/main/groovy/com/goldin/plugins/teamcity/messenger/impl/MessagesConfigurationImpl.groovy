package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.MessagesConfiguration
import jetbrains.buildServer.serverSide.MainConfigProcessor
import org.gcontracts.annotations.Requires
import org.jdom.Element
import org.jdom.Text

/**
 * {@link MessagesConfiguration} implementation
 */
class MessagesConfigurationImpl implements MessagesConfiguration, MainConfigProcessor
{
    boolean minify
    int     ajaxRequestInterval
    int     persistencyInterval
    int     messagesLimitPerUser
    int     messageLengthLimit
    String  dateFormatPattern
    String  timeFormatPattern


    @Override
    @Requires({ root })
    void readFrom ( Element root )
    {
        Node   defaults = new XmlParser().parse( getClass().getResourceAsStream( '/default-config.xml' ))
        assert defaults

        def configMap = root.children.inject( [:] ){ Map m, Element node -> m[ node.name ] = node.textTrim; m }
        def get       = { String paramName -> ( configMap[ paramName ] ?: defaults.get( paramName ).text()) }

        minify               = get( 'minify'               ) as boolean
        ajaxRequestInterval  = get( 'ajaxRequestInterval'  ) as int
        persistencyInterval  = get( 'persistencyInterval'  ) as int
        messagesLimitPerUser = get( 'messagesLimitPerUser' ) as int
        dateFormatPattern    = get( 'dateFormatPattern'    )
        timeFormatPattern    = get( 'timeFormatPattern'    )
    }


    @Override
    void writeTo ( Element root )
    {
        def  element = { String tagName, String tagValue -> new Element( tagName ).setContent( new Text( tagValue )) }
        root.content = [ element( 'minify',               minify               as String ),
                         element( 'ajaxRequestInterval',  ajaxRequestInterval  as String ),
                         element( 'persistencyInterval',  persistencyInterval  as String ),
                         element( 'messagesLimitPerUser', messagesLimitPerUser as String ),
                         element( 'dateFormatPattern',    dateFormatPattern ),
                         element( 'timeFormatPattern',    timeFormatPattern )]
    }
}

