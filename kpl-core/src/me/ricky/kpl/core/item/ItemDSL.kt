package me.ricky.kpl.core.item

import de.tr7zw.changeme.nbtapi.NBTCompound
import de.tr7zw.changeme.nbtapi.NBTContainer
import de.tr7zw.changeme.nbtapi.NBTItem
import me.ricky.kpl.core.util.colorize
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import kotlin.reflect.KProperty

class ItemBuilder(item: ItemStack) {
  var item = item
    private set

  inline fun change(change: ItemStack.() -> Unit) {
    overwrite(item.apply(change))
  }

  inline fun changeMeta(change: ItemMeta.() -> Unit) {
    change {
      itemMeta = itemMeta.apply(change)
    }
  }

  inline fun <reified T : ItemMeta> changeMetaOf(change: T.() -> Unit) {
    change {
      itemMeta = (itemMeta as T).apply(change)
    }
  }

  fun overwrite(item: ItemStack) {
    this.item = item
  }
}

interface ItemComponent {
  val builder: ItemBuilder
}

interface EnchantDelegate {
  operator fun getValue(enchants: Enchantments, property: KProperty<*>): Int
  operator fun setValue(enchants: Enchantments, property: KProperty<*>, level: Int)
}

class Enchantments(override val builder: ItemBuilder) : ItemComponent

fun nullifAir(item: ItemStack?): ItemStack? {
  if (item?.type == Material.AIR) return null

  return item
}

inline val ItemComponent.item inline get() = builder.item
inline val ItemComponent.meta inline get() = item.itemMeta!!

inline var ItemMeta.name: String
  inline get() = displayName
  inline set(value) {
    setDisplayName(value.colorize())
  }

inline var ItemMeta.loreList: List<String>
  inline get() = lore ?: emptyList()
  inline set(value) {
    lore = value.colorize()
  }

inline var ItemMeta.loreText: String
  inline get() = loreList.joinToString("\n")
  inline set(value) {
    loreList = value.split("\n").colorize()
  }

fun ItemMeta.resetName() {
  setDisplayName(null)
}

inline fun ItemComponent.change(change: ItemStack.() -> Unit) {
  builder.change(change)
}

inline fun ItemComponent.changeMeta(change: ItemMeta.() -> Unit) {
  builder.changeMeta(change)
}

inline fun <reified T : ItemMeta> ItemComponent.changeMetaOf(change: T.() -> Unit) {
  builder.changeMetaOf(change)
}

inline fun item(
  from: ItemStack,
  build: ItemBuilder.() -> Unit = {}
): ItemStack {
  val builder = ItemBuilder(from)

  builder.apply(build)

  return builder.item
}

inline fun item(
  material: Material = Material.DIRT,
  amount: Int = 1,
  build: ItemBuilder.() -> Unit = {}
): ItemStack {
  return item(ItemStack(material, amount), build)
}

inline fun ItemBuilder.meta(apply: ItemMeta.() -> Unit) {
  changeMeta(apply)
}

inline fun <reified T : ItemMeta> ItemBuilder.metaOf(apply: T.() -> Unit) {
  changeMetaOf(apply)
}

inline fun ItemBuilder.enchants(apply: Enchantments.() -> Unit) {
  Enchantments(this).apply(apply)
}

inline fun ItemBuilder.nbt(apply: (NBTCompound) -> Unit) {
  overwrite(NBTItem(item).apply(apply).item)
}