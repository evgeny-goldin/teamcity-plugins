package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.MessagesConfiguration
import jetbrains.buildServer.serverSide.MainConfigProcessor
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires
import org.jdom.Element
import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import org.gcontracts.annotations.Invariant


/**
 * {@link MessagesConfiguration} implementation
 */
@Invariant({ ( this.ajaxRequestInterval  > 0 ) &&
             ( this.persistencyInterval  > 0 ) &&
             ( this.messagesLimitPerUser > 0 ) &&
             ( this.messageLengthLimit   > 0 ) &&
             ( this.dateFormatPattern && this.timeFormatPattern ) })
class MessagesConfigurationImpl implements MessagesConfiguration, MainConfigProcessor
{
    private final MessagesContext     context
    private final Map<String, String> defaults

    boolean minify
    int     ajaxRequestInterval
    int     persistencyInterval
    int     messagesLimitPerUser
    int     messageLengthLimit
    String  dateFormatPattern
    String  timeFormatPattern


    @Requires({ paramName && ( config != null ) && defaults })
    @Ensures({ result })
    private String param ( String paramName, Map config = [:] )
    {
        config[ paramName ] ?: defaults[ paramName ]
    }


    MessagesConfigurationImpl ( MessagesContext context )
    {
        this.context              = context
        this.defaults             = map( new XmlParser().parse( getClass().getResourceAsStream( '/default-config.xml' ))).asImmutable()
        this.minify               = param( 'minify'               ) as boolean
        this.ajaxRequestInterval  = param( 'ajaxRequestInterval'  ) as int
        this.persistencyInterval  = param( 'persistencyInterval'  ) as int
        this.messagesLimitPerUser = param( 'messagesLimitPerUser' ) as int
        this.dateFormatPattern    = param( 'dateFormatPattern'    )
        this.timeFormatPattern    = param( 'timeFormatPattern'    )
    }


    /**
     * Converts {@link Node} or {@link Element} to {@code Map<String, String>}.
     *
     * @param o object to convert
     * @return object's mapping
     */
    @Requires({ o })
    @Ensures({ result != null })
    private Map<String, String> map ( Object o )
    {
        if ( o instanceof Node )
        {
            (( Node ) o ).children().inject( [:] ){ Map m, Node childNode ->

                String text = childNode.text().trim()
                assert childNode.name() && text

                m[ childNode.name() ] = text
                m
            }
        }
        else if ( o instanceof Element )
        {
            (( Element ) o ).children.inject( [:] ){ Map m, Element childElement ->

                String text = childElement.text.trim()
                assert childElement.name && text

                m[ childElement.name ] = text
                m
            }
        }
        else {
            assert false, "Conversion to Map for class [${ o.class.name }] is not supported"
        }
    }


    @Override
    @Requires({ root })
    void readFrom ( Element root )
    {
        Element rootNode = root.getChild( context.pluginName )

        if ( rootNode )
        {
            Map<String, String> config = map( rootNode )
            this.minify                = param( 'minify',               config ) as boolean
            this.ajaxRequestInterval   = param( 'ajaxRequestInterval',  config ) as int
            this.persistencyInterval   = param( 'persistencyInterval',  config ) as int
            this.messagesLimitPerUser  = param( 'messagesLimitPerUser', config ) as int
            this.dateFormatPattern     = param( 'dateFormatPattern',    config )
            this.timeFormatPattern     = param( 'timeFormatPattern',    config )
        }
    }


    @Override
    void writeTo ( Element root )
    {
    }
}
