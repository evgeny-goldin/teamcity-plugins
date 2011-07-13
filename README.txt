~~~~~~~~~~~~~~~~~
TeamCity Plugins
~~~~~~~~~~~~~~~~~


1) To build plugins:
---------------------------------------------------------

* Run "gradle" or "gradle clean build"


2) To deploy plugins:
---------------------------------------------------------

* Make sure TC server is not running
* Run "gradle deploy -DteamCityDir=<TC directory>"


3) To copy static resources (jsp, js, css files):
---------------------------------------------------------

* Make sure ".BuildServer/config/internal.properties" contains "teamcity.development.mode=true"
  (see http://confluence.jetbrains.net/display/TCD65/Development+Environment)
* Run "gradle copy -DteamCityDir=<TC directory>"
