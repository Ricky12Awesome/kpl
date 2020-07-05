package me.ricky.kpl.enchants

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent

fun CEManager.applyDefaultEventListeners() {
  armorChangeEventListener()
}

fun CEManager.armorChangeEventListener() {
  addListener<PlayerArmorChangeEvent> {
    with(event) {
      val oldEnchants = customEnchantsFrom(oldItem)
      val newEnchants = customEnchantsFrom(newItem)

      oldEnchants.forEach { (id, lvl) ->
        ifHasListener(id) {
          deactivate(lvl)
        }
      }

      newEnchants.forEach { (id, lvl) ->
        ifHasListener(id) {
          activate(lvl)
        }
      }
    }
  }
}