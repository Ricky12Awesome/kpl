package me.ricky.kpl.enchants

import de.tr7zw.changeme.nbtapi.NBTCompound
import de.tr7zw.changeme.nbtapi.NBTItem
import me.ricky.kpl.core.KPlugin
import me.ricky.kpl.core.item.EnchantDelegate
import me.ricky.kpl.core.item.Enchantments
import me.ricky.kpl.core.item.nbt
import me.ricky.kpl.core.item.nullifAir
import me.ricky.kpl.core.nbt.compound
import me.ricky.kpl.core.nbt.set
import me.ricky.kpl.core.util.Manager
import me.ricky.kpl.core.util.addListener
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KProperty

const val CUSTOM_ENCHANT_NBT_KEY = "CustomEnchants"

fun customEnchantsFrom(item: NBTCompound): Map<String, Int> {
  val enchants = mutableMapOf<String, Int>()
  val list = item.getCompoundList(CUSTOM_ENCHANT_NBT_KEY)

  list.forEach {
    val id = it.getString("id") ?: ""
    val lvl = it.getInteger("lvl") ?: 0

    if (id.isNotEmpty()) {
      enchants[id] = lvl
    }
  }

  return enchants
}

fun getEnchantLevel(item: NBTCompound, id: String): Int {
  val list = item.getCompoundList(CUSTOM_ENCHANT_NBT_KEY) ?: return 0
  val enchant = list.firstOrNull { it.getString("id") == id } ?: return 0

  return enchant.getInteger("lvl") ?: 0
}

fun applyCustomEnchant(item: NBTCompound, id: String, lvl: Int) {
  item[CUSTOM_ENCHANT_NBT_KEY] = item.getCompoundList(CUSTOM_ENCHANT_NBT_KEY) + compound {
    it["id"] = id
    it["lvl"] = lvl
  }
}

inline fun CustomEnchant.ifHasEnchantFor(item: ItemStack?, execute: (lvl: Int) -> Unit) {
  if (nullifAir(item) == null) return
  val lvl = getEnchantLevel(NBTItem(item), id)

  if (lvl != 0) {
    execute(lvl)
  }
}

interface CustomEnchant : Listener {
  val id: String
  val levelRange: IntRange
  val target: EnchantmentTarget
}

class CustomEnchantDelegate(private val id: String) : EnchantDelegate {
  override fun getValue(enchants: Enchantments, property: KProperty<*>): Int {
    enchants.builder.nbt {
      return getEnchantLevel(it, id)
    }

    return 0
  }

  override fun setValue(enchants: Enchantments, property: KProperty<*>, level: Int) {
    enchants.builder.nbt {
      applyCustomEnchant(it, id, level)
    }
  }

}

class CustomEnchantManager : MutableList<CustomEnchant> by mutableListOf(), Manager {

  override fun enable(source: KPlugin) {
    forEach(source::addListener)
  }

}
