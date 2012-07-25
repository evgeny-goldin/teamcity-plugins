
~~~~~~~~~~~~~~~~~
TeamCity Plugins
~~~~~~~~~~~~~~~~~


 To generate IDEA project files:
---------------------------------------------------------

    gradlew idea


 To build plugins:
---------------------------------------------------------

    gradlew
    gradlew clean build


 To deploy plugins to standalone TC server:
---------------------------------------------------------

Make sure TC server is not running!

    gradlew deploy -DteamCityDir=<TC directory>


`<TC directory>` is directory where standalone TC server is running.



 To copy static resources (jsp, js, css files) to standalone TC server:
---------------------------------------------------------

Make sure `".BuildServer/config/internal.properties"` contains `"teamcity.development.mode=true"` _(see [wiki](http://confluence.jetbrains.net/display/TCD65/Development+Environment) for more details)_

    gradlew copy -DteamCityDir=<TC directory>


`<TC directory>` is directory where standalone TC server is running.
