package me.ricky.kpl.core.util

import java.nio.file.Files
import java.nio.file.Path

fun <T> List<T>.nullIfEmpty() = takeIf { isNotEmpty() }


fun Path.createIfNotExists(forceDirectory: Boolean = false) {
  if (Files.exists(this)) return

  if (parent != null) {
    Files.createDirectories(parent)
  }

  if (Files.isDirectory(this) || forceDirectory) {
    Files.createDirectory(this)
  } else {
    Files.createFile(this)
  }
}