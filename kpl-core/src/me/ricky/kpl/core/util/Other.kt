package me.ricky.kpl.core.util

import java.nio.file.Files
import java.nio.file.Path

fun <T> List<T>.nullIfEmpty() = takeIf { isNotEmpty() }

