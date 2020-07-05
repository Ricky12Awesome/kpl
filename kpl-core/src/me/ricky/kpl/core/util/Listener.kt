package me.ricky.kpl.core.util

import me.ricky.kpl.core.KPlugin
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.Plugin
import kotlin.reflect.KClass
import kotlin.reflect.full.cast

interface EventListener<T : Event> : EventExecutor, Listener{
  val type: KClass<T>
  val priority: EventPriority

  fun registerFor(plugin: Plugin) {
    plugin.pluginManager.registerEvent(type.java, this, priority, this, plugin)
  }
}

class SimpleEventListener<T : Event>(
  override val type: KClass<T>,
  val execute: T.() -> Unit,
  override val priority: EventPriority
) : EventListener<T> {
  override fun execute(listener: Listener, event: Event) {
    type.cast(event).execute()
  }
}

inline fun <reified T : Event> listener(
  priority: EventPriority = EventPriority.NORMAL,
  noinline execute: T.() -> Unit
) = SimpleEventListener(T::class, execute, priority)

inline fun <reified T : Event> KPlugin.listen(
  priority: EventPriority = EventPriority.NORMAL,
  noinline execute: T.() -> Unit
) = listen(T::class, priority, execute)

fun <T : Event> Plugin.listen(
  kClass: KClass<T>,
  priority: EventPriority = EventPriority.NORMAL,
  execute: T.() -> Unit
) = SimpleEventListener(kClass, execute, priority).also {
  it.registerFor(this)
}

fun KPlugin.addListener(listener: EventListener<*>) {
  listener.registerFor(this)
}