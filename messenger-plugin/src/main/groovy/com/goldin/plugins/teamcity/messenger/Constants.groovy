package com.goldin.plugins.teamcity.messenger

import groovy.util.logging.Log
import jetbrains.buildServer.web.openapi.PluginDescriptor
import org.springframework.context.ApplicationContext

/**
 * Various constants
 */
@Log
class Constants
{
    PluginDescriptor   descriptor
    ApplicationContext context

    Constants ( PluginDescriptor descriptor, ApplicationContext context )
    {
        setDescriptor( descriptor )
        setContext( context.parent )

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

        log.fine( """
 Constants loaded:
 Plugin name = [${ descriptor.pluginName }]
 [$beansCount] Spring beans available:
$beans
"""     )
    }

    String getPluginName() { descriptor.pluginName }
}
