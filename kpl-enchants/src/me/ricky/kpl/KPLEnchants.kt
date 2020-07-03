package me.ricky.kpl

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import me.ricky.kpl.core.KPlugin
import me.ricky.kpl.core.command.CommandManager
import me.ricky.kpl.core.command.PlayerContextCreator
import me.ricky.kpl.core.command.command
import me.ricky.kpl.core.item.Enchantments
import me.ricky.kpl.core.item.enchants
import me.ricky.kpl.core.item.item
import me.ricky.kpl.core.util.managerOf
import me.ricky.kpl.enchants.CustomEnchant
import me.ricky.kpl.enchants.CustomEnchantDelegate
import me.ricky.kpl.enchants.CustomEnchantManager
import me.ricky.kpl.enchants.ifHasEnchantFor
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.event.EventHandler
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class SpeedEnchant : CustomEnchant {
  override val id: String = "speed"
  override val levelRange: IntRange = 1..5
  override val target: EnchantmentTarget = EnchantmentTarget.ARMOR_FEET

  @EventHandler
  fun PlayerArmorChangeEvent.onChange() {
    ifHasEnchantFor(oldItem) {
      player.removePotionEffect(PotionEffectType.SPEED)
    }

    if (newItem == null) {
      println("Test")
    }

    ifHasEnchantFor(newItem) {
      player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 99999999, it - 1, false, false))
    }
  }
}

var Enchantments.speed by CustomEnchantDelegate("speed")

class KPLEnchants : KPlugin() {
  val enchants = managerOf(::CustomEnchantManager)
  val commands = managerOf(::CommandManager)

  override fun init() {
    enchants += SpeedEnchant()
  }

  override fun enable() {
    commands.command("ce", PlayerContextCreator) {
      executes {
        source.inventory.setItemInMainHand(item(source.inventory.itemInMainHand) {
          enchants {
            speed = 3
          }
        })
      }
    }
  }

}