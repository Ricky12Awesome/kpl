package me.ricky.kpl

import kotlinx.serialization.Serializable
import me.ricky.kpl.core.KPlugin
import me.ricky.kpl.core.command.CommandManager
import me.ricky.kpl.core.serializable.ItemStackSerializer
import me.ricky.kpl.core.util.JsonFile
import me.ricky.kpl.core.util.createIfNotExists
import me.ricky.kpl.core.util.managerOf
import org.bukkit.inventory.ItemStack
import java.nio.file.Files
import kotlin.streams.asSequence

@Serializable
data class Kit(
  val items: List<@Serializable(ItemStackSerializer::class) ItemStack> = listOf(),
  val cooldown: Double = 0.0
)

class KPLKits : KPlugin() {
  private val kitDirectory = dataFolder.resolve("kits/").toPath()
  private val commandManager = managerOf(::CommandManager)

  private var kits: Map<String, JsonFile<Kit>> = mapOf()

  override fun enable() {
    kitDirectory.createIfNotExists(true)

    server.scheduler.scheduleSyncRepeatingTask(this, {
      kits = Files
        .list(kitDirectory)
        .asSequence()
        .map { it.fileName.toString() }
        .filter { it.endsWith(".json") }
        .map { it.removeSuffix(".json") }
        .associateWith {
          JsonFile(kitDirectory.resolve(it), Kit.serializer())
        }
    }, 0, 20 * 30)

  }

}