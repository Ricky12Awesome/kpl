package me.ricky.kpl

import com.mojang.brigadier.arguments.IntegerArgumentType
import me.ricky.kpl.core.KPlugin
import me.ricky.kpl.core.command.*
import me.ricky.kpl.core.item.item
import me.ricky.kpl.core.util.managerOf
import me.ricky.kpl.enchants.*
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class KPLEnchants : KPlugin() {
  val enchants = managerOf(::CEManager)
  val commands = managerOf(::CommandManager)

  override fun init() {
    CEManager.addEnchant("life", "Life", 1..5, EnchantTarget.armor) {
      onArmorChange {
        onActivation {
          player.addPotionEffect(PotionEffect(PotionEffectType.HEALTH_BOOST, Int.MAX_VALUE, it - 1, false, false))
        }

        onDeactivation {
          player.removePotionEffect(PotionEffectType.HEALTH_BOOST)
        }
      }
    }

    CEManager.addEnchant("force_field", "Force Field", 1..2, EnchantTarget.chestplate) {
      onArmorChange {
        onActivation {
          player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Int.MAX_VALUE, it - 1, false, false))
        }

        onDeactivation {
          player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE)
        }
      }
    }

    CEManager.addEnchant("speed", "Speed", 1..5, EnchantTarget.boots) {
      onArmorChange {
        onActivation {
          player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Int.MAX_VALUE, it - 1, false, false))
        }

        onDeactivation {
          player.removePotionEffect(PotionEffectType.SPEED)
        }
      }
    }
  }

  override fun enable() {
    commands.command("ce", PlayerContextCreator) {
      dynamicArgument("enchant") { CEManager.enchants.keys }
      argument("level", IntegerArgumentType.integer())

      executes {
        val enchant: String by argument()
        val level: Int by argument()

        val main = source.inventory.itemInMainHand
        val item = item(main) {
          addCustomEnchant(enchant, level)
        }

        source.inventory.setItemInMainHand(item)
      }
    }
  }

}