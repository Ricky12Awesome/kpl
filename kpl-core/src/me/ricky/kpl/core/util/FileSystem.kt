package me.ricky.kpl.core.util

import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.serializer
import java.nio.file.Files
import java.nio.file.Path
import kotlin.reflect.KProperty

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

class JsonFile<T>(
  val path: Path,
  val serializer: KSerializer<T>,
  createDefault: (() -> T)? = null
) {
  private val _createDefault: () -> T
  private var _cachedValue: T? = null

  init {
    _createDefault = createDefault ?: {
      json.parse(serializer, "{}")
    }
  }

  fun read(): T {
    if (Files.notExists(path)) {
      val created = _createDefault()

      write(created)

      return created
    }

    return Files.newBufferedReader(path).use {
      json.parse(serializer, it.readText())
    }
  }

  fun write(data: T) {
    path.createIfNotExists()

    Files.newBufferedWriter(path).use {
      it.write(json.stringify(serializer, data))
    }

  }

  operator fun getValue(source: Any?, property: KProperty<*>): T {
    if (_cachedValue == null) {
      _cachedValue = read()
    }

    return _cachedValue!!
  }

  operator fun setValue(source: Any?, property: KProperty<*>, value: T?) {
    _cachedValue = value

    if (value != null) {
      write(value)
    }
  }

  companion object {
    var json: Json = Json(JsonConfiguration.Stable.copy(prettyPrint = true))

    fun <T> read(path: Path, serializer: KSerializer<T>, createDefault: (() -> T)? = null): T {
      return JsonFile(path, serializer, createDefault).read()
    }

    @OptIn(ImplicitReflectionSerializer::class)
    inline fun <reified T> read(path: Path, noinline createDefault: (() -> T)? = null): T {
      return read(path, serializer(), createDefault)
    }

    fun <T> write(path: Path, serializer: KSerializer<T>, value: T) {
      JsonFile(path, serializer).write(value)
    }

    @OptIn(ImplicitReflectionSerializer::class)
    inline fun <reified T> write(path: Path, value: T) {
      write(path, serializer(), value)
    }

    @OptIn(ImplicitReflectionSerializer::class)
    inline operator fun <reified T> invoke(
      path: Path,
      noinline createDefault: (() -> T)? = null
    ): JsonFile<T> {
      return JsonFile(path, serializer(), createDefault)
    }
  }
}

