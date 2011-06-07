package com.goldin.plugins.teamcity.messenger

import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.web.openapi.PluginDescriptor
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext

/**
 * Spring and TC context-related properties
 */
class Context implements InitializingBean
{
    static final String PLUGINS_CATEGORY = 'com.goldin.plugins'
    static final Logger LOG              = Logger.getInstance( PLUGINS_CATEGORY )

    
    final PluginDescriptor   descriptor
    final String             pluginName
    final ApplicationContext springContext


    Context ( PluginDescriptor descriptor, ApplicationContext springContext )
    {
        this.descriptor    = descriptor
        this.pluginName    = descriptor.pluginName
        this.springContext = springContext
    }


    @Override
    void afterPropertiesSet ()
    {
        if ( LOG.isDebugEnabled())
        {
            List<String> beanNames  = [ springContext, springContext.parent ]*.beanDefinitionNames.toList().flatten()
            int          beansCount = beanNames.size()
            assert       beanNames

            def digits     = Math.log10( beansCount ).next() as int
            def maxLength  = beanNames*.size().max()
            def counter    = 1
            def beans      = beanNames.inject( new StringBuilder()){
                StringBuilder b, String beanName ->

                String beanCounter     = '[' + "${ counter++ }".padLeft( digits, '0' ) + ']'
                Class  beanClass       = springContext.getBean( beanName ).class
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
