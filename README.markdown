~~~~~~~~~~~~~~~~~
TeamCity Plugins
~~~~~~~~~~~~~~~~~


1) To build plugins:
---------------------------------------------------------

    gradle

or

    gradle clean build



2) To deploy plugins to standalone TC installation:
---------------------------------------------------------

 Make sure TC server is not running

    gradle deploy -DteamCityDir=<TC directory>



3) To copy static resources (jsp, js, css files):
---------------------------------------------------------

Make sure ".BuildServer/config/internal.properties" contains "teamcity.development.mode=true" _(see [wiki](http://confluence.jetbrains.net/display/TCD65/Development+Environment))_

    gradle copy -DteamCityDir=<TC directory>
