package me.ricky.kpl

import com.mojang.brigadier.arguments.StringArgumentType
import kotlinx.serialization.Serializable
import me.ricky.kpl.core.KPlugin
import me.ricky.kpl.core.command.CommandManager
import me.ricky.kpl.core.command.PlayerContextCreator
import me.ricky.kpl.core.command.command
import me.ricky.kpl.core.command.getArgument
import me.ricky.kpl.core.serializable.ItemStackSerializer
import me.ricky.kpl.core.util.*
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

    commandManager.command("kit", PlayerContextCreator) {
      onCommand("get") {
        dynamicArgument("kit") { kits.keys }

        executes {
          val key = getArgument<String>("kit")
          val response = kits["$key.json"]?.left()
            ?: ErrorMessage("&cKit with name of &a$key&c was not found.").right()

          when (response) {
            is Either.Left -> {
              val kit= response.value.read()

              source.inventory.addItem(*kit.items.toTypedArray())
              source.sendColoredMessage("&aYou've received &e$key")
            }
            is Either.Right -> response.value.sendTo(source)
          }
        }
      }

      onCommand("create") {
        argument("kit", StringArgumentType.word())

        executes {
          val name = getArgument<String>("kit")
          val file = JsonFile(kitDirectory.resolve("$name.json"), Kit.serializer())

          file.write(Kit(items = source.inventory.contents.filterNotNull().toList()))
        }
      }
    }
  }

}