package me.ricky.kpl.core.util

sealed class Either<out L, out R> {
  data class Left<out L>(val value: L) : Either<L, Nothing>()
  data class Right<out R>(val value: R) : Either<Nothing, R>()
}

operator fun <L> Either<L, *>.component1(): L? = leftOrNull()
operator fun <R> Either<*, R>.component2(): R? = rightOrNull()

fun <L> L.left() = Either.Left(this)
fun <R> R.right() = Either.Right(this)

fun <L> Either<L, *>.leftOrNull(): L? {
  return if (this is Either.Left) value else null
}

fun <R> Either<*, R>.rightOrNull(): R? {
  return if (this is Either.Right) value else null
}

inline infix fun <L> Either<L, *>.ifLeft(call: (value: L) -> Unit) {
  if (this is Either.Left) {
    call(value)
  }
}

inline infix fun <R> Either<*, R>.ifRight(call: (value: R) -> Unit) {
  if (this is Either.Right) {
    call(value)
  }
}
