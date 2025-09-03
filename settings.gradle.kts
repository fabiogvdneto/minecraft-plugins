dependencyResolutionManagement {
    repositories {
        maven {
            name = "papermc"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        maven {
            name = "jitpack"
            url = uri("https://jitpack.io")
        }
        maven {
            name = "extendedclip"
            url = uri("https://repo.extendedclip.com/releases/")
        }
    }
}

rootProject.name = "minecraft-plugins"
include("common")
include("warps")
include("kits")