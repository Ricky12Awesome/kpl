package me.ricky.kpl

import com.mojang.brigadier.arguments.IntegerArgumentType
import me.ricky.kpl.core.command.CommandManager
import me.ricky.kpl.core.command.PlayerContextCreator
import me.ricky.kpl.core.command.command
import org.bukkit.plugin.java.JavaPlugin

class KPLCore : JavaPlugin() {
  private val manager = CommandManager(this)

  override fun onEnable() {
    manager.enable()

    manager.command("test", PlayerContextCreator) {
      argument("a", IntegerArgumentType.integer())
      argument("b", IntegerArgumentType.integer())
      argument("c", IntegerArgumentType.integer())

      executes {
        source.sendMessage(input)
      }
    }
  }

  override fun onDisable() {

  }
}