package me.ricky.kpl.core.util

import kotlin.reflect.KProperty

typealias ImmutableListDelegate<T> = ImmutableDelegate<MutableList<T>, List<T>>
typealias ImmutableArrayDelegate<T> = ImmutableDelegate<Array<T>, Array<out T>>

typealias ImmutableIterableDelegate<T> = ImmutableDelegate<MutableIterable<T>, Iterable<T>>
typealias ImmutableIteratorDelegate<T> = ImmutableDelegate<MutableIterator<T>, Iterator<T>>
typealias ImmutableListIteratorDelegate<T> = ImmutableDelegate<MutableListIterator<T>, ListIterator<T>>

typealias ImmutableCollectionDelegate<T> = ImmutableDelegate<MutableCollection<T>, Collection<T>>
typealias ImmutableArrayListDelegate<T> = ImmutableDelegate<ArrayList<T>, List<T>>

typealias ImmutableSetDelegate<T> = ImmutableDelegate<MutableSet<T>, Set<T>>
typealias ImmutableLinkedHashSetDelegate<T> = ImmutableDelegate<LinkedHashSet<T>, Set<T>>
typealias ImmutableHashSetDelegate<T> = ImmutableDelegate<HashSet<T>, Set<T>>

typealias ImmutableMapDelegate<K, V> = ImmutableDelegate<MutableMap<K, V>, Map<K, V>>
typealias ImmutableLinkedHashMapDelegate<K, V> = ImmutableDelegate<LinkedHashMap<K, V>, Map<K, V>>
typealias ImmutableHashMapDelegate<K, V> = ImmutableDelegate<HashMap<K, V>, Map<K, V>>

class ImmutableDelegate<Mutable : Immutable, Immutable>(private val mutable: Mutable) {
  val value get(): Immutable = mutable

  operator fun getValue(a: Any?, p: KProperty<*>): Immutable = value
}

fun <T> immutable(source: MutableList<T>) = ImmutableListDelegate(source)
fun <T> immutable(source: ArrayList<T>) = ImmutableArrayListDelegate(source)

fun <T> immutable(source: MutableIterable<T>) = ImmutableIterableDelegate(source)
fun <T> immutable(source: MutableIterator<T>) = ImmutableIteratorDelegate(source)
fun <T> immutable(source: MutableListIterator<T>) = ImmutableListIteratorDelegate(source)

fun <T> immutable(source: MutableCollection<T>) = ImmutableCollectionDelegate(source)
fun <T> immutable(source: Array<T>) = ImmutableArrayDelegate(source)

fun <T> immutable(source: MutableSet<T>) = ImmutableSetDelegate(source)
fun <T> immutable(source: LinkedHashSet<T>) = ImmutableLinkedHashSetDelegate(source)
fun <T> immutable(source: HashSet<T>) = ImmutableHashSetDelegate(source)

fun <K, V> immutable(source: MutableMap<K, V>) = ImmutableMapDelegate(source)
fun <K, V> immutable(source: LinkedHashMap<K, V>) = ImmutableLinkedHashMapDelegate(source)
fun <K, V> immutable(source: HashMap<K, V>) = ImmutableHashMapDelegate(source)

operator fun <T> MutableList<T>.getValue(a: Any?, p: KProperty<*>): List<T> = this
operator fun <T> ArrayList<T>.getValue(a: Any?, p: KProperty<*>): List<T> = this

operator fun <T> MutableIterable<T>.getValue(a: Any?, p: KProperty<*>): Iterable<T> = this
operator fun <T> MutableIterator<T>.getValue(a: Any?, p: KProperty<*>): Iterator<T> = this
operator fun <T> MutableListIterator<T>.getValue(a: Any?, p: KProperty<*>): ListIterator<T> = this

operator fun <T> MutableCollection<T>.getValue(a: Any?, p: KProperty<*>): Collection<T> = this
operator fun <T> Array<T>.getValue(a: Any?, p: KProperty<*>): Array<out T> = this

operator fun <T> MutableSet<T>.getValue(a: Any?, p: KProperty<*>): Set<T> = this
operator fun <T> LinkedHashSet<T>.getValue(a: Any?, p: KProperty<*>): Set<T> = this
operator fun <T> HashSet<T>.getValue(a: Any?, p: KProperty<*>): Set<T> = this

operator fun <K, V> MutableMap<K, V>.getValue(a: Any?, p: KProperty<*>): Map<K, V> = this
operator fun <K, V> LinkedHashMap<K, V>.getValue(a: Any?, p: KProperty<*>): Map<K, V> = this
operator fun <K, V> HashMap<K, V>.getValue(a: Any?, p: KProperty<*>): Map<K, V> = this
