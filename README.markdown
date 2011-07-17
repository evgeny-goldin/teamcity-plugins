
~~~~~~~~~~~~~~~~~
TeamCity Plugins
~~~~~~~~~~~~~~~~~


1) To build plugins:
---------------------------------------------------------

    gradle

or

    gradle clean build



2) To deploy plugins to standalone TC server:
---------------------------------------------------------

Make sure TC server is not running!

    gradle deploy -DteamCityDir=<TC directory>


Where `<TC directory>` is directory where TC is installed.



3) To copy static resources (jsp, js, css files):
---------------------------------------------------------

Make sure `".BuildServer/config/internal.properties"` contains `"teamcity.development.mode=true"` _(see [wiki](http://confluence.jetbrains.net/display/TCD65/Development+Environment) for more details)_

    gradle copy -DteamCityDir=<TC directory>


Where `<TC directory>` is directory where TC is installed.
