package me.ricky.kpl.core.nbt

import de.tr7zw.changeme.nbtapi.NBTCompound
import de.tr7zw.changeme.nbtapi.NBTContainer
import de.tr7zw.changeme.nbtapi.NBTType
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.*
import me.ricky.kpl.core.util.nullIfEmpty
import org.bukkit.inventory.ItemStack

fun NBTCompound.toJson(): JsonObject {
  return JsonObject(keys.associateWith(::toJson))
}

fun NBTCompound.toJson(key: String): JsonElement {
  return when (getType(key)) {
    NBTType.NBTTagByte -> JsonPrimitive(getByte(key))
    NBTType.NBTTagShort -> JsonPrimitive(getShort(key))
    NBTType.NBTTagInt -> JsonPrimitive(getInteger(key))
    NBTType.NBTTagLong -> JsonPrimitive(getLong(key))
    NBTType.NBTTagFloat -> JsonPrimitive(getFloat(key))
    NBTType.NBTTagDouble -> JsonPrimitive(getDouble(key))
    NBTType.NBTTagByteArray -> JsonArray(getByteArray(key).map(::JsonPrimitive))
    NBTType.NBTTagIntArray -> JsonArray(getIntArray(key).map(::JsonPrimitive))
    NBTType.NBTTagString -> JsonPrimitive(getString(key))
    NBTType.NBTTagCompound -> getCompound(key).toJson()
    NBTType.NBTTagList -> {
      var list: List<JsonElement>? = null

      for (i in 0..5) {
        if (list != null) break

        list = when (i) {
          0 -> getCompoundList(key)?.nullIfEmpty()?.map(NBTCompound::toJson)
          1 -> getStringList(key)?.nullIfEmpty()?.map(::JsonPrimitive)
          2 -> getIntegerList(key)?.nullIfEmpty()?.map(::JsonPrimitive)
          3 -> getLongList(key)?.nullIfEmpty()?.map(::JsonPrimitive)
          4 -> getFloatList(key)?.nullIfEmpty()?.map(::JsonPrimitive)
          5 -> getDoubleList(key)?.nullIfEmpty()?.map(::JsonPrimitive)
          else -> null
        }
      }

      if (list != null) JsonArray(list) else JsonNull
    }
    else -> {
      error("I Don't know how we got here, but the error happened for $key")
    }
  }
}

fun JsonObject.toNBT(json: Json): NBTContainer {
  return NBTContainer(json.stringify(JsonObject.serializer(), this))
}

@OptIn(UnstableDefault::class)
fun JsonObject.toNBT(): NBTContainer {
  return NBTContainer(Json.stringify(JsonObject.serializer(), this))
}

inline fun compound(build: (NBTCompound) -> Unit): NBTCompound {
  return NBTContainer().apply(build)
}

operator fun NBTCompound.set(key: String, value: Byte) = setByte(key, value)
operator fun NBTCompound.set(key: String, value: ByteArray) = setByteArray(key, value)
operator fun NBTCompound.set(key: String, value: Short) = setShort(key, value)
operator fun NBTCompound.set(key: String, value: Int) = setInteger(key, value)
operator fun NBTCompound.set(key: String, value: IntArray) = setIntArray(key, value)
operator fun NBTCompound.set(key: String, value: Long) = setLong(key, value)
operator fun NBTCompound.set(key: String, value: Float) = setFloat(key, value)
operator fun NBTCompound.set(key: String, value: Double) = setDouble(key, value)
operator fun NBTCompound.set(key: String, value: String) = setString(key, value)
operator fun NBTCompound.set(key: String, value: ItemStack) = setItemStack(key, value)
operator fun NBTCompound.set(key: String, value: NBTCompound) = addCompound(key).mergeCompound(value)

@JvmName("setIntList")
operator fun NBTCompound.set(key: String, value: List<Int>) {
  getIntegerList(key)?.let { list ->
    list.clear()
    list.addAll(value)
  }
}

@JvmName("setLongList")
operator fun NBTCompound.set(key: String, value: List<Long>) {
  getLongList(key)?.let { list ->
    list.clear()
    list.addAll(value)
  }
}

@JvmName("setFloatList")
operator fun NBTCompound.set(key: String, value: List<Float>) {
  getFloatList(key)?.let { list ->
    list.clear()
    list.addAll(value)
  }
}

@JvmName("setDoubleList")
operator fun NBTCompound.set(key: String, value: List<Double>) {
  getDoubleList(key)?.let { list ->
    list.clear()
    list.addAll(value)
  }
}

@JvmName("setStringList")
operator fun NBTCompound.set(key: String, value: List<String>) {
  getStringList(key)?.let { list ->
    list.clear()
    list.addAll(value)
  }
}

@JvmName("setCompoundList")
operator fun NBTCompound.set(key: String, value: List<NBTCompound>) {
  getCompoundList(key)?.let { list ->
    list.clear()
    value.forEach {
      list.addCompound(it)
    }
  }
}
