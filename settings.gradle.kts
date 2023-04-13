@file:Suppress("UnstableApiUsage")

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
    }
}

rootProject.name = "EastarMultiModule"

include(":app")
include(":base_network")
include(":base_string")
include(":base_resource")
include(":common_ktx")
include(":common_security")
include(":feature_domain_a")
include(":feature_data_a")
include(":feature_domain_b")
include(":feature_domain_c")
include(":feature_data_b")
include(":feature_data_c")
include(":feature_data_d")


// 찾은 모듈을 저장하기 위한 HashMap 설정
val modules = hashMapOf<String, String>()

data class Folder(private val folder: File) {
    init {
        require(folder.isDirectory) { "folder must be a directory" }
    }

    fun listFolders(): Array<Folder> = folder.listFiles()?.filter { it.isDirectory }?.filterNot { it.name.startsWith(".") }?.map { Folder(it) }?.toTypedArray() ?: emptyArray()
    fun listFiles(): Array<File> = folder.listFiles()?.filter { it.isFile }?.filterNot { it.isHidden }?.toTypedArray() ?: emptyArray()
}

Folder(rootProject.projectDir).setModules()

fun Folder.setModules() {
    listFiles()
        .firstOrNull {
            it.name == "build.gradle.kts" || it.name == "build.gradle"
        }?.let {
            modules[it.parentFile.name] = it.parentFile.path
        }

    listFolders()
        .forEach {
            it.setModules()
        }
}

for (project in rootProject.children) {
    if (modules.containsKey(project.name)) {
        val directory = modules[project.name] ?: continue
        project.projectDir = File(directory)
    }
}

