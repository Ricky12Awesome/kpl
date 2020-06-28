import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlinx.serialization.gradle.SerializationGradleSubplugin

plugins {
  kotlin("jvm") version "1.3.72"
  kotlin("plugin.serialization") version "1.3.72"
  id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "me.ricky"
version = "1.0-SNAPSHOT"

subprojects {
  apply<KotlinPluginWrapper>()
  apply<SerializationGradleSubplugin>()
  apply<ShadowPlugin>()

  repositories {
    maven("https://raw.githubusercontent.com/JorelAli/1.13-Command-API/mvn-repo/1.13CommandAPI/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
    maven("https://oss.jfrog.org/artifactory/oss-snapshot-local/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://libraries.minecraft.net")
    maven("https://jitpack.io")
    mavenCentral()
    jcenter()
  }

  kotlin.sourceSets["main"].kotlin.srcDirs("./src")
  sourceSets["main"].resources.srcDirs("./resources")

  buildDir = rootProject.file("./build/$name/")

  tasks {
    shadowJar {
      val version = if (project.version == "unspecified") rootProject.version else project.version

      configurations = listOf(project.configurations["shadow"])
      archiveFileName.set("${project.name}-${version}.jar")
      destinationDirectory.set(rootProject.file("./server/plugins/"))
    }

    compileKotlin {
      kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + listOf("-XXLanguage:+InlineClasses", "-Xopt-in=kotlin.RequiresOptIn")
      }
    }

    compileTestKotlin {
      kotlinOptions.jvmTarget = "1.8"
    }
  }
}
