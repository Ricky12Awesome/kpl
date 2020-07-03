package me.ricky.kpl.core.util

import com.mongodb.reactivestreams.client.MongoDatabase
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.getCollectionOfName

inline fun <reified T : Any> MongoDatabase.getCoroutineCollectionOfName(name: String): CoroutineCollection<T> {
  return getCollectionOfName<T>(name).coroutine
}