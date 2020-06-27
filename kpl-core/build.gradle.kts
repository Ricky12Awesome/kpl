fun DependencyHandlerScope.shadowApi(notation: Any) {
  shadow(notation)
  api(notation)
}

fun DependencyHandlerScope.shadowApi(notation: String, action: Action<ExternalModuleDependency>) {
  shadow(notation, action)
  api(notation, action)
}

dependencies {
  // Kotlin
  shadowApi(kotlin("stdlib-jdk8"))
  shadowApi(kotlin("reflect"))
  shadowApi("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
  shadowApi("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")

  // Spigot and Plugins
  api("com.destroystokyo.paper", "paper-api", "1.15.2-R0.1-SNAPSHOT")

  shadowApi("de.tr7zw:item-nbt-api:2.3.1")
  shadowApi("me.lucko:commodore:1.8") {
    exclude("com.mojang", "brigadier")
  }

  api("me.clip:placeholderapi:2.10.6")
  //api("io.github.jorelali:commandapi:2.3")
  api("com.github.MilkBowl:VaultAPI:1.7") {
    exclude("org.bukkit", "bukkit")
  }

  api("com.mojang:brigadier:1.0.17")

  // Other
  shadowApi("io.arrow-kt:arrow-core:0.10.5-SNAPSHOT")
}

tasks.shadowJar {
  relocate("de.tr7zw.changeme.nbtapi", "de.tr7zw.changeme.nbtapi.kpl")
  relocate("me.lucko.commodore", "me.lucko.commodore.kpl")
}