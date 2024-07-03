pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        //jcenter() // 額外添加如果需要
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        //jcenter() 額外添加如果需要
    }
}

rootProject.name = "Calendarapi"
include(":app")