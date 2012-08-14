
~~~~~~~~~~~~~~~~~
TeamCity Plugins
~~~~~~~~~~~~~~~~~


 To generate IDEA project files:
---------------------------------------------------------

    gradlew idea

 To build plugins:
---------------------------------------------------------

    gradlew

 To build and copy plugins plugins to ".BuildServer/plugins":
---------------------------------------------------------

    gradlew cpz

 To copy static resources (jsp, js, and css files):
---------------------------------------------------------

Make sure ".BuildServer/config/internal.properties" contains `"teamcity.development.mode=true"`, see [wiki](http://confluence.jetbrains.net/display/TCD7/Development+Environment) for more details.

    gradlew cps -DTeamCityApp=<TC directory>

`<TC directory>` is a directory where TC web application is located, such as "~/TeamCity/webapps/bs". It should contain a "plugins/yourPlugin" directory with your plugin static resources.
