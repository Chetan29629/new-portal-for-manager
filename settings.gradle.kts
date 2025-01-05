pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS) // Ensures settings repositories take precedence
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "managerPortal"

include(":app")

