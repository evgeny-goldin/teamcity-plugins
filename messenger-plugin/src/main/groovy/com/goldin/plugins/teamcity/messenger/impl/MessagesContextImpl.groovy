package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.users.SUser
import jetbrains.buildServer.web.openapi.PluginDescriptor
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext

/**
 * {@link MessagesContext} implementation
 */
class MessagesContextImpl implements InitializingBean, MessagesContext
{
    static final String PLUGINS_CATEGORY = 'com.goldin.plugins'
    static final Logger LOG              = Logger.getInstance( PLUGINS_CATEGORY )

    final SBuildServer       server
    final PluginDescriptor   descriptor
    final String             pluginName
    final ApplicationContext springContext


    @Requires({ server && descriptor && springContext })
    MessagesContextImpl ( SBuildServer server, PluginDescriptor descriptor, ApplicationContext springContext )
    {
        this.server        = server
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
 Name      = [${ descriptor.pluginName }]
 Version   = [${ descriptor.pluginVersion }]
 Resources = [${ new File( server.serverRootPath, descriptor.pluginResourcesPath ).canonicalPath.replace( '\\', '/' ) }]
 [$beansCount] Spring beans available:
$beans"""   )
        }
    }

    @Override
    boolean isTest () { false }


    @Override
    @Requires({ username })
    @Ensures({ ( result != null ) || ( result == null )})
    SUser getUser ( String username )
    {
        server.userModel.findUserAccount( null, username )
    }


    @Override
    @Requires({ username })
    @Ensures({ result })
    Set<String> getUserGroups ( String username )
    {
        SUser user = getUser( username )
        if ( ! user ) { return [] }
        
        user.allUserGroups*.name as Set
    }
}
