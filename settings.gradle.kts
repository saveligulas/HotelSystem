pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "HotelSystem"

include("core", "event", "command", "query")
