package com.goldin.plugins.teamcity.messenger

import jetbrains.buildServer.web.openapi.PluginDescriptor

/**
 * Various constants
 */
class Constants
{
    PluginDescriptor descriptor

    Constants ( PluginDescriptor descriptor )
    {
        setDescriptor( descriptor )
    }

    String getPluginName() { descriptor.pluginName }
}
