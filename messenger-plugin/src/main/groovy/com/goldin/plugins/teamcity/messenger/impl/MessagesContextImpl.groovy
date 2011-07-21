package com.goldin.plugins.teamcity.messenger.impl

import com.goldin.plugins.teamcity.messenger.api.MessagesContext
import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.users.SUser
import jetbrains.buildServer.web.openapi.PluginDescriptor
import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Invariant
import org.gcontracts.annotations.Requires
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import com.goldin.plugins.teamcity.messenger.api.MessagesConfiguration

/**
 * {@link MessagesContext} implementation
 */
@Invariant({ this.server && this.descriptor && this.springContext && this.pluginName })
class MessagesContextImpl implements InitializingBean, MessagesContext
{

    private final SBuildServer          server
    private final PluginDescriptor      descriptor
    private final ApplicationContext    springContext
            final String                pluginName
    private       MessagesConfiguration config
                  Logger                log


    @Requires({ server && descriptor && springContext })
    MessagesContextImpl ( SBuildServer server, PluginDescriptor descriptor, ApplicationContext springContext )
    {
        this.server        = server
        this.descriptor    = descriptor
        this.springContext = springContext
        this.pluginName    = descriptor.pluginName

        /**
         * {@link MessagesConfiguration} bean depends on {@link MessagesContext} so we can't inject it in constructor.
         * Instead, it is pulled from the context after all properties are set.
         * @see #afterPropertiesSet
         */
        this.config = null
        this.log    = null
    }


    @Requires({ ( this.config == null ) && ( this.log == null ) })
    @Ensures({ this.config && this.log })
    @Override
    void afterPropertiesSet ()
    {
        this.config = ( MessagesConfiguration ) springContext.getBean( 'messagesConfiguration', MessagesConfiguration )
        this.log    = Logger.getInstance( config.logCategory )

        if ( log.isDebugEnabled())
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

            log.debug( """
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
    Locale getLocale () { Locale.US }


    @Override
    SUser getUser ( String username )
    {
        server.userModel.findUserAccount( null, username )
    }


    @Override
    Set<String> getUserGroups ( String username )
    {
        SUser user = getUser( username )
        if ( ! user ) { return [] }

        user.allUserGroups*.name as Set
    }
}
