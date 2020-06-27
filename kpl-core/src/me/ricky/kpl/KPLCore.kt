package me.ricky.kpl

import com.mojang.brigadier.arguments.IntegerArgumentType
import me.ricky.kpl.core.command.CommandManager
import me.ricky.kpl.core.command.PlayerContextCreator
import me.ricky.kpl.core.command.command
import me.ricky.kpl.core.item.*
import me.ricky.kpl.core.util.sendColoredMessage
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

class KPLCore : JavaPlugin() {
  private val manager = CommandManager(this)

  override fun onEnable() {
    manager.enable()

    manager.command("test", PlayerContextCreator) {
      argument("a", IntegerArgumentType.integer())
      argument("b", IntegerArgumentType.integer())
      onArgument("c", IntegerArgumentType.integer()) {
      }
      argument("d", IntegerArgumentType.integer())
      argument("e", IntegerArgumentType.integer())

      executes {

        source.sendColoredMessage("&eHmm")
      }
    }
  }

  override fun onDisable() {

  }
}