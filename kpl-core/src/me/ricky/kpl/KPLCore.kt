package me.ricky.kpl

import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.LocationArgument
import dev.jorel.commandapi.arguments.LocationType
import dev.jorel.commandapi.arguments.StringArgument
import kotlinx.serialization.Serializable
import me.ricky.kpl.core.KPlugin
import me.ricky.kpl.core.command.CommandManager
import me.ricky.kpl.core.command.command
import me.ricky.kpl.core.gui.MenuListener
import me.ricky.kpl.core.internal.db.MongoConfig
import me.ricky.kpl.core.util.configOf
import me.ricky.kpl.core.util.listenerOf
import me.ricky.kpl.core.util.managerOf
import me.ricky.kpl.core.util.sendColoredMessage
import org.bukkit.Location

@Serializable
data class KPLCoreConfig(
  val mongo: MongoConfig = MongoConfig()
)

class KPLCore : KPlugin() {
  private val commandManager = managerOf(::CommandManager)
  private val menuListener = listenerOf(::MenuListener)
  private val config by configOf<KPLCoreConfig>("config.json")

  override fun init() {
//    initialize(config.mongo)

    commandManager.command("test") {
      arguments["a"] = StringArgument()
      arguments["b"] = IntegerArgument()
      arguments["c"] = LocationArgument(LocationType.BLOCK_POSITION)

      playerExecutor {
        val a: String by arguments
        val b: Int by arguments
        val c: Location by arguments

        sender.sendColoredMessage("&aYou've entered &e$a&a, &e$b, and &e${c.toVector()}")
      }
    }
  }

  override fun enable() {

  }

  override fun disable() {

  }
}