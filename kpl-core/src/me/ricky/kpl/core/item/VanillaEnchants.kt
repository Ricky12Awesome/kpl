package me.ricky.kpl.core.item

import org.bukkit.enchantments.Enchantment
import kotlin.reflect.KProperty

class VanillaEnchantDelegate(private val enchant: Enchantment) : EnchantDelegate {
  override operator fun getValue(enchants: Enchantments, property: KProperty<*>): Int {
    return enchants.meta.getEnchantLevel(enchant)
  }

  override operator fun setValue(enchants: Enchantments, property: KProperty<*>, level: Int) {
    enchants.changeMeta {
      addEnchant(enchant, level, true)
    }
  }
}

var Enchantments.protection by VanillaEnchantDelegate(Enchantment.PROTECTION_ENVIRONMENTAL)
var Enchantments.fireProtection by VanillaEnchantDelegate(Enchantment.PROTECTION_FIRE)
var Enchantments.featherFalling by VanillaEnchantDelegate(Enchantment.PROTECTION_FALL)
var Enchantments.blastProtection by VanillaEnchantDelegate(Enchantment.PROTECTION_EXPLOSIONS)
var Enchantments.respiration by VanillaEnchantDelegate(Enchantment.OXYGEN)
var Enchantments.aquaAffinity by VanillaEnchantDelegate(Enchantment.WATER_WORKER)
var Enchantments.thorns by VanillaEnchantDelegate(Enchantment.THORNS)
var Enchantments.depthStrider by VanillaEnchantDelegate(Enchantment.DEPTH_STRIDER)
var Enchantments.frostWalker by VanillaEnchantDelegate(Enchantment.FROST_WALKER)
var Enchantments.curseOfBinding by VanillaEnchantDelegate(Enchantment.BINDING_CURSE)
var Enchantments.sharpness by VanillaEnchantDelegate(Enchantment.DAMAGE_ALL)
var Enchantments.smite by VanillaEnchantDelegate(Enchantment.DAMAGE_UNDEAD)
var Enchantments.banOfArthropods by VanillaEnchantDelegate(Enchantment.DAMAGE_ARTHROPODS)
var Enchantments.knockback by VanillaEnchantDelegate(Enchantment.KNOCKBACK)
var Enchantments.fireAspect by VanillaEnchantDelegate(Enchantment.FIRE_ASPECT)
var Enchantments.looting by VanillaEnchantDelegate(Enchantment.LOOT_BONUS_MOBS)
var Enchantments.sweepingEdge by VanillaEnchantDelegate(Enchantment.SWEEPING_EDGE)
var Enchantments.efficiency by VanillaEnchantDelegate(Enchantment.DIG_SPEED)
var Enchantments.silkTouch by VanillaEnchantDelegate(Enchantment.SILK_TOUCH)
var Enchantments.unbreaking by VanillaEnchantDelegate(Enchantment.DURABILITY)
var Enchantments.fortune by VanillaEnchantDelegate(Enchantment.LOOT_BONUS_BLOCKS)
var Enchantments.power by VanillaEnchantDelegate(Enchantment.ARROW_DAMAGE)
var Enchantments.punch by VanillaEnchantDelegate(Enchantment.ARROW_KNOCKBACK)
var Enchantments.luckOfTheSea by VanillaEnchantDelegate(Enchantment.LUCK)
var Enchantments.lure by VanillaEnchantDelegate(Enchantment.LURE)
var Enchantments.loyalty by VanillaEnchantDelegate(Enchantment.LOYALTY)
var Enchantments.impaling by VanillaEnchantDelegate(Enchantment.IMPALING)
var Enchantments.riptide by VanillaEnchantDelegate(Enchantment.RIPTIDE)
var Enchantments.channeling by VanillaEnchantDelegate(Enchantment.CHANNELING)
var Enchantments.quickCharge by VanillaEnchantDelegate(Enchantment.QUICK_CHARGE)
var Enchantments.piercing by VanillaEnchantDelegate(Enchantment.PIERCING)
var Enchantments.mending by VanillaEnchantDelegate(Enchantment.MENDING)
var Enchantments.curseOfVanishing by VanillaEnchantDelegate(Enchantment.VANISHING_CURSE)