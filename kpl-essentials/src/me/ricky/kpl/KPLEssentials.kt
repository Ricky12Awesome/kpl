package me.ricky.kpl

import kotlinx.serialization.Serializable
import me.ricky.kpl.core.command.CommandManager
import me.ricky.kpl.core.serializable.ItemStackSerializer
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

@Serializable
data class Kit(
  val items: List<@Serializable(ItemStackSerializer::class) ItemStack>,
  val cooldown: Double
)

class KPLEssentials : JavaPlugin() {
  private val kitDirectory = dataFolder.resolve("./kits").toPath()
  private val commandManager = CommandManager(this)

  override fun onEnable() {

  }

}