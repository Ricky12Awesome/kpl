package me.ricky.kpl.enchants

import de.tr7zw.changeme.nbtapi.NBTCompound
import de.tr7zw.changeme.nbtapi.NBTItem
import me.ricky.kpl.core.KPlugin
import me.ricky.kpl.core.item.*
import me.ricky.kpl.core.nbt.compound
import me.ricky.kpl.core.nbt.set
import me.ricky.kpl.core.util.*
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

const val CUSTOM_ENCHANT_NBT_KEY = "CustomEnchants"

fun customEnchantsFrom(item: ItemStack?): Map<String, Int> {
  return nullifAir(item)
    ?.let(::NBTItem)
    ?.let(::customEnchantsFrom)
    ?: mapOf()
}

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

fun ItemBuilder.addCustomEnchant(id: String, lvl: Int) {
  applyCustomEnchant(this, id, lvl)
}

fun applyCustomEnchant(item: ItemBuilder, id: String, lvl: Int) {
  item.meta {
    loreList = loreList + "&7${CEManager.enchants[id]?.name} $lvl"
  }

  item.nbt {
    applyCustomEnchantNBT(it, id, lvl)
  }
}

fun applyCustomEnchantNBT(item: NBTCompound, id: String, lvl: Int) {
  item[CUSTOM_ENCHANT_NBT_KEY] = item.getCompoundList(CUSTOM_ENCHANT_NBT_KEY) + compound {
    it["id"] = id
    it["lvl"] = lvl
  }
}

data class CEEvent<E : Event>(
  val event: E,
  val type: KClass<E>,
  val enchants: Map<String, CustomEnchant>
) {

  inline fun ifHasListener(id: String, execute: CEtListener<E>.() -> Unit) {
    getListener(id)?.let(execute)
  }

  @Suppress("UNCHECKED_CAST")
  fun getListener(id: String): CEtListener<E>? {
    val ce = enchants[id] ?: return null
    val listener = ce.listeners[type] ?: return null

    return listener as? CEtListener<E>
  }
}

data class CEtListener<E : Event>(
  val activate: E.(lvl: Int) -> Unit,
  val deactivate: E.(lvl: Int) -> Unit
)

data class SimpleCustomEnchant(
  override val id: String,
  override val name: String,
  override val levelRange: IntRange,
  override val target: EnchantTarget,
  override val listeners: Map<KClass<*>, CEtListener<*>>
) : CustomEnchant

interface CustomEnchant : Listener {
  val id: String
  val name: String
  val levelRange: IntRange
  val target: EnchantTarget
  val listeners: Map<KClass<*>, CEtListener<*>>
}

class CEDelegate(private val id: String) : EnchantDelegate {
  override fun getValue(enchants: Enchantments, property: KProperty<*>): Int {
    enchants.builder.nbt {
      return getEnchantLevel(it, id)
    }

    return 0
  }

  override fun setValue(enchants: Enchantments, property: KProperty<*>, level: Int) {
    applyCustomEnchant(enchants.builder, id, level)
  }
}

class CEManager : Manager {
  val listeners = mutableListOf<SimpleEventListener<*>>()

  companion object {
    private val _enchants = mutableMapOf<String, CustomEnchant>()
    val enchants by immutable(_enchants)

    fun addEnchant(enchant: CustomEnchant) {
      _enchants[enchant.id] = enchant
    }

  }

  inline fun <reified E : Event> addListener(crossinline execute: CEEvent<E>.() -> Unit) {
    listeners += listener<E> {
      CEEvent(this, E::class, enchants).execute()
    }
  }

  override fun enable(source: KPlugin) {
    applyDefaultEventListeners()
    listeners.forEach(source::addListener)
  }
}
