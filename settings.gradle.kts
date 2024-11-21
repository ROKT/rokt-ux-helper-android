pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // TODO: remove this once stable version is released to maven central
        maven("https://apps.rokt.com/msdk")
    }
}

rootProject.name = "UxHelper"
include(":modelmapper")
include(":networkhelper")
include(":roktux")
include(":testutils")
include(":demoapp")
