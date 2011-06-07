package com.goldin.plugins.teamcity.messenger

import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.web.openapi.PluginDescriptor
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware


/**
 * Various constants
 */
class Constants implements ApplicationContextAware, InitializingBean
{
    static final String PLUGINS_CATEGORY = 'com.goldin.plugins'
    static final Logger LOG              = Logger.getInstance( PLUGINS_CATEGORY )

    
    PluginDescriptor   descriptor
    String             pluginName
    ApplicationContext context

    
    Constants ( PluginDescriptor descriptor )
    {
        setDescriptor( descriptor )
        setPluginName( descriptor.pluginName )
    }


    @Override
    void setApplicationContext ( ApplicationContext context )
    {
        setContext ( context )
    }

    
    @Override
    void afterPropertiesSet ()
    {
        if ( LOG.isDebugEnabled())
        {
            List<String> beanNames  = [ context, context.parent ]*.beanDefinitionNames.toList().flatten()
            int          beansCount = beanNames.size()
            assert       beanNames

            def digits     = Math.log10( beansCount ).next() as int
            def maxLength  = beanNames*.size().max()
            def counter    = 1
            def beans      = beanNames.inject( new StringBuilder()){
                StringBuilder b, String beanName ->

                String beanCounter     = '[' + "${ counter++ }".padLeft( digits, '0' ) + ']'
                Class  beanClass       = context.getBean( beanName ).getClass()
                String beanNameD       = "[$beanName]".padRight( maxLength + 3 )
                String beanDescription = "[$beanClass.name]${ beanClass.interfaces ? ' implements [' + beanClass.interfaces*.name.join( '][' ) + ']' : '' }"
                b << " $beanCounter$beanNameD = $beanDescription\n"
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
