package me.ricky.kpl.core.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.nio.file.Files
import java.nio.file.Path

class JsonFile<T>(
  val path: Path,
  val serializer: KSerializer<T>,
  private val json: Json = Json(JsonConfiguration.Stable.copy(prettyPrint = true))
) {
  fun read(): T {
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
}