package me.ricky.kpl.enchants

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent

fun CEtListenersDSL.onArmorChange(dsl: CEListenerDSL<PlayerArmorChangeEvent>) {
  listenerOf(dsl)
}