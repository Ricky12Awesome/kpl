package me.ricky.kpl.core.util

import me.ricky.kpl.core.KPlugin
import org.bukkit.event.Listener

interface Manager {
  fun enable(source: KPlugin) {}
  fun disable(source: KPlugin) {}
}

interface ManagerListener : Manager, Listener

inline val KPlugin.pluginManager inline get() = server.pluginManager

fun KPlugin.addListener(listener: Listener) {
  pluginManager.registerEvents(listener, this)
}

inline fun <reified T> KPlugin.configOf(name: String): JsonFile<T> {
  return JsonFile(dataFolder.resolve(name).toPath())
}

fun <T : Manager> KPlugin.managerOf(manager: T): T {
  onEnableListeners += manager::enable
  onDisableListeners += manager::disable

  return manager
}

fun <T : Manager> KPlugin.managerOf(construct: () -> T): T {
  return managerOf(construct())
}

fun <T : Listener> KPlugin.listenerOf(listener: T): T {
  onEnableListeners += {
    addListener(listener)
  }

  return listener
}

fun <T : Listener> KPlugin.listenerOf(construct: () -> T): T {
  return listenerOf(construct())
}

fun <T> KPlugin.managerListenerOf(managerListener: T): T
    where T : Manager,
          T : Listener {

  onEnableListeners += managerListener::enable
  onDisableListeners += managerListener::disable

  onEnableListeners += {
    addListener(managerListener)
  }

  return managerListener
}


fun <T> KPlugin.managerListenerOf(construct: () -> T): T
    where T : Manager,
          T : Listener {
  return managerListenerOf(construct())
}