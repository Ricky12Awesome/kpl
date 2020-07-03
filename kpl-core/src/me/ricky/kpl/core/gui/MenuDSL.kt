package me.ricky.kpl.core.gui

import me.ricky.kpl.core.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

class MenuItemDSL : MenuItem {
  var item: ItemStack? = null
  var onClick: MenuClickEvent.() -> Unit = {}
    private set

  inline fun item(
    material: Material = Material.DIRT,
    amount: Int = 1,
    build: ItemBuilder.() -> Unit = {}
  ) {
    val builder = ItemBuilder(ItemStack(material, amount))

    builder.apply(build)

    item = builder.item
  }

  fun onClick(click: MenuClickEvent.() -> Unit) {
    onClick = click
  }

  override fun onClick(event: MenuClickEvent) {
    event.onClick()
  }
}

class MenuDSL(
  title: String,
  rows: Int,
  canMoveItems: Boolean
) : AbstractMenu(title, rows, canMoveItems) {
  override val items: MutableMap<Int, MenuItem> = mutableMapOf()

  fun slot(
    slot: Int = inventory.firstEmpty(),
    dsl: MenuItemDSL.() -> Unit
  ) {
    val item = MenuItemDSL().apply(dsl)

    items[slot] = item
    inventory.setItem(slot, item.item)
  }

  override fun onClick(event: MenuClickEvent) {

  }

  override fun onOpen(event: MenuOpenEvent) {

  }

  override fun onClose(event: MenuCloseEvent) {

  }
}

inline fun menu(
  title: String,
  rows: Int = 3,
  canMoveItems: Boolean = false,
  dsl: MenuDSL.() -> Unit
): Menu {
  return MenuDSL(title, rows, canMoveItems).apply(dsl)
}

inline fun Player.openMenu(
  title: String,
  rows: Int = 3,
  canMoveItems: Boolean = false,
  dsl: MenuDSL.() -> Unit
) {
  openMenu(menu(title, rows, canMoveItems, dsl))
}

fun Player.openMenu(menu: Menu) {
  openInventory(menu.inventory)
}