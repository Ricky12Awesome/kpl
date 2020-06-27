package me.ricky.kpl.core.util

fun <T> List<T>.nullIfEmpty() = takeIf { isNotEmpty() }