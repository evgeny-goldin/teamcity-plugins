package com.goldin.plugins.teamcity.messenger

import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.web.openapi.PluginDescriptor
import org.springframework.context.ApplicationContext

/**
 * Various constants
 */
class Constants
{
    static final String PLUGINS_CATEGORY = 'com.goldin.plugins'
    static final Logger LOG              = Logger.getInstance( PLUGINS_CATEGORY )

    
    PluginDescriptor   descriptor
    ApplicationContext context
    String             pluginName

    Constants ( PluginDescriptor descriptor, ApplicationContext context )
    {
        setDescriptor( descriptor )
        setContext   ( context.parent )
        setPluginName( descriptor.pluginName )

        if ( LOG.isDebugEnabled())
        {
            def beansCount = getContext().beanDefinitionCount
            def beanNames  = getContext().beanDefinitionNames.sort()

            assert beansCount == beanNames.size()

            def digits     = Math.log10( beansCount ).next() as int
            def maxLength  = beanNames*.size().max()
            def counter    = 1
            def beans      = beanNames.inject( new StringBuilder()){
                StringBuilder b, String beanName ->
                String beanClass = getContext().getBean( beanName ).getClass().name
                beanName         = "[$beanName]".padRight( maxLength + 3 )
                b << " [${ String.valueOf( counter++ ).padLeft( digits, '0' ) }]$beanName = [$beanClass]\n"
                b
            }

            LOG.debug( """
 Plugin loaded:
 Name          = [${ descriptor.pluginName }]
 Version       = [${ descriptor.pluginVersion }]
 Resource path = [${ descriptor.pluginResourcesPath }]
 [$beansCount] Spring beans available:
$beans"""   )
        }
    }
}
