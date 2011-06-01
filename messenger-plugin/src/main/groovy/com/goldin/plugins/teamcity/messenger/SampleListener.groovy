package com.goldin.plugins.teamcity.messenger

import jetbrains.buildServer.serverSide.BuildServerAdapter
import jetbrains.buildServer.serverSide.BuildServerListener


class SampleListener
{
    @Delegate BuildServerListener listener = new BuildServerAdapter()
    
}