TeamCity Plugins
----------------

* <a href="http://evgeny-goldin.org/teamcity/viewType.html?buildTypeId=bt57&tab=buildTypeStatusDiv&guest=1"><img src="http://evgeny-goldin.org/teamcity/app/rest/builds/buildType:(id:bt57)/statusIcon"/></a>

* Run `gradlew idea` to generate IDEA files.

* Run `gradlew` to build the plugins.

* Run `gradlew cpz` to build and copy plugins to ".BuildServer/plugins".

* Run `gradlew cps -DTeamCityApp=<TC directory>` to copy plugins static resources (jsp, js, and css files).

    `<TC directory>` is a directory where TC web application is located, such as "~/TeamCity/webapps/bs". It should contain a "plugins/yourPlugin" directory with your plugin static resources.

    Make sure ".BuildServer/config/internal.properties" contains `"teamcity.development.mode=true"`, see [wiki](http://confluence.jetbrains.net/display/TCD7/Development+Environment) for more details.

