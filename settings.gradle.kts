pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
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
include(":core")
include(":networkhelper")
include(":roktux")
include(":testutils")
include(":demoapp")
