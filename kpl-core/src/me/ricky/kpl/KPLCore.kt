package me.ricky.kpl

import kotlinx.serialization.Serializable
import me.ricky.kpl.core.KPlugin
import me.ricky.kpl.core.command.CommandManager
import me.ricky.kpl.core.command.PlayerContextCreator
import me.ricky.kpl.core.command.command
import me.ricky.kpl.core.gui.MenuListener
import me.ricky.kpl.core.internal.db.MongoConfig
import me.ricky.kpl.core.util.configOf
import me.ricky.kpl.core.util.listenerOf
import me.ricky.kpl.core.util.managerOf

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
  }

  override fun enable() {
    commandManager.command("test", PlayerContextCreator) {
      executes {
        source.sendMessage()
      }
    }
  }

  override fun disable() {

  }
}