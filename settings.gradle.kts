pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven { url = uri ("https://jitpack.io") }

        maven {
            url = uri("https://cardinalcommerceprod.jfrog.io/artifactory/android")
            credentials {
                username = providers.gradleProperty("JFROG_USERNAME").orNull ?: ""
                password = providers.gradleProperty("JFROG_PASSWORD").orNull ?: ""
            }
        }

    }
}

rootProject.name = "SportyHub"
include(":app")
