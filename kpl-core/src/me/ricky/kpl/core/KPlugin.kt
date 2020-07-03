package me.ricky.kpl.core

import org.bukkit.plugin.java.JavaPlugin

abstract class KPlugin : JavaPlugin() {
  val onEnableListeners: MutableList<KPlugin.() -> Unit> = mutableListOf()
  val onDisableListeners: MutableList<KPlugin.() -> Unit> = mutableListOf()

  open fun init() {

  }

  open fun enable() {

  }

  open fun disable() {

  }

  final override fun onEnable() {
    init()

    onEnableListeners.forEach {
      it()
    }
    
    enable()
  }

  final override fun onDisable() {
    onDisableListeners.forEach {
      it()
    }

    disable()
  }
}