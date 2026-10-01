// Standalone build that measures how much roktux adds to an app. Kept out of the main
// build so `./gradlew build` and the PR checks never compile it.
// -PuxHelperRoot points at the checkout to measure; it defaults to this repository.
pluginManagement {
    val uxHelperRoot = providers.gradleProperty("uxHelperRoot").getOrElse("../..")
    includeBuild("$uxHelperRoot/build-logic")
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

val uxHelperRoot = providers.gradleProperty("uxHelperRoot").getOrElse("../..")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("$uxHelperRoot/gradle/libs.versions.toml"))
        }
    }
}

// Substitutes com.rokt:roktux with the measured checkout's :roktux module.
includeBuild(uxHelperRoot) {
    dependencySubstitution {
        substitute(module("com.rokt:roktux")).using(project(":roktux"))
    }
}

rootProject.name = "ux-helper-size-report"
include(":app")
