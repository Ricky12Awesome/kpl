package me.ricky.kpl.core.gui

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class MenuClickEvent(
  from: InventoryClickEvent
) : InventoryClickEvent(from.view, from.slotType, from.rawSlot, from.click, from.action)

class MenuOpenEvent(
  from: InventoryOpenEvent
) : InventoryOpenEvent(from.view)

class MenuCloseEvent(
  from: InventoryCloseEvent
) : InventoryCloseEvent(from.view)

interface MenuItem {
  fun onClick(event: MenuClickEvent)
}

interface Menu : InventoryHolder {
  val title: String
  val rows: Int
  val canMoveItems: Boolean
  val items: Map<Int, MenuItem>

  fun onClick(event: MenuClickEvent) {

  }

  fun onOpen(event: MenuOpenEvent) {

  }

  fun onClose(event: MenuCloseEvent) {

  }
}

abstract class AbstractMenu(
  final override val title: String,
  final override val rows: Int,
  final override val canMoveItems: Boolean
) : Menu {
  private val _inventory: Inventory by lazy {
    Bukkit.createInventory(this, rows * 9, title)
  }

  final override fun getInventory(): Inventory {
    return _inventory
  }
}

class MenuListener : Listener {

  @EventHandler
  fun InventoryClickEvent.onClick() {
    val menu = view.topInventory.holder as? Menu ?: return

    if (!menu.canMoveItems) {
      isCancelled = true
    }

    if (clickedInventory?.holder == menu) {
      val item = menu.items[slot]
      val event = MenuClickEvent(this)

      menu.onClick(event)
      item?.onClick(event)
    }
  }

  @EventHandler
  fun InventoryOpenEvent.onOpen() {
    val menu = inventory.holder as? Menu ?: return

    menu.onOpen(MenuOpenEvent(this))
  }

  @EventHandler
  fun InventoryCloseEvent.onClose() {
    val menu = inventory.holder as? Menu ?: return

    menu.onClose(MenuCloseEvent(this))
  }
}


