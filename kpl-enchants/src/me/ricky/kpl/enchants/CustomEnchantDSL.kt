package me.ricky.kpl.enchants

import org.bukkit.event.Event
import kotlin.reflect.KClass

typealias CEListenerDSL<E> = CEListenerDSLData<E>.() -> Unit

class CEListenerDSLData<E> {
  var activate: E.(lvl: Int) -> Unit = {}
  var deactivate: E.(lvl: Int) -> Unit = {}

  fun onActivation(onActivate: E.(lvl: Int) -> Unit) {
    activate = onActivate
  }

  fun onDeactivation(onDeactivate: E.(lvl: Int) -> Unit) {
    deactivate = onDeactivate
  }
}

class CEtListenersDSL : MutableMap<KClass<*>, CEtListener<*>> by mutableMapOf() {
  inline fun <reified E : Event> listenerOf(
    noinline activate: E.(lvl: Int) -> Unit,
    noinline deactivate: E.(lvl: Int) -> Unit
  ) {
    this[E::class] = CEtListener(activate, deactivate)
  }

  inline fun <reified E : Event> listenerOf(dsl: CEListenerDSL<E>) {
    val data = CEListenerDSLData<E>().apply(dsl)
    this[E::class] = CEtListener(data.activate, data.deactivate)
  }
}

inline fun customEnchantListeners(dsl: CEtListenersDSL.() -> Unit): Map<KClass<*>, CEtListener<*>> {
  return CEtListenersDSL().apply(dsl)
}

inline fun customEnchant(
  id: String,
  name: String,
  lvlRange: IntRange,
  target: EnchantTarget,
  dsl: CEtListenersDSL.() -> Unit
): CustomEnchant {
  return SimpleCustomEnchant(id, name, lvlRange, target, customEnchantListeners(dsl))
}

inline fun CEManager.Companion.addEnchant(
  id: String,
  name: String,
  lvlRange: IntRange,
  target: EnchantTarget,
  dsl: CEtListenersDSL.() -> Unit
) {
  addEnchant(customEnchant(id, name, lvlRange, target, dsl))
}