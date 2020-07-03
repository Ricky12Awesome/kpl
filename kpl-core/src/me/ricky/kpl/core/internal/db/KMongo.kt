package me.ricky.kpl.core.internal.db

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import kotlinx.serialization.Serializable
import org.bson.UuidRepresentation
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.serialization.SerializationClassMappingTypeService

@Serializable
data class MongoConfig(
  val connection: String = "mongodb://localhost:27017",
  val database: String = "test"
)

lateinit var client: CoroutineClient
  internal set

lateinit var database: CoroutineDatabase
  internal set

internal fun initialize(
  config: MongoConfig,
  builder: MongoClientSettings.Builder = MongoClientSettings.builder()
) {
  val (connection, name) = config

  System.setProperty(
    "org.litote.mongo.test.mapping.service",
    SerializationClassMappingTypeService::class.qualifiedName!!
  )

  val settings = builder
    .uuidRepresentation(UuidRepresentation.STANDARD)
    .applyConnectionString(ConnectionString(connection))
    .build()

  client = KMongo.createClient(settings).coroutine
  database = client.getDatabase(name)
}