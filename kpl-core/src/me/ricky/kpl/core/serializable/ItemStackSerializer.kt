package me.ricky.kpl.core.serializable

import de.tr7zw.changeme.nbtapi.NBTItem
import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonObject
import me.ricky.kpl.core.item.item
import me.ricky.kpl.core.item.nbt
import me.ricky.kpl.core.nbt.toJson
import me.ricky.kpl.core.nbt.toNBT
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@Serializer(ItemStack::class)
object ItemStackSerializer : KSerializer<ItemStack> {
  override val descriptor: SerialDescriptor = SerialDescriptor(ItemStackSerializer::class.qualifiedName!!) {
    element("type", String.serializer().descriptor)
    element("amount", Int.serializer().descriptor, isOptional = true)
    element("nbt", JsonObject.serializer().descriptor, isOptional = true)
  }

  override fun serialize(encoder: Encoder, value: ItemStack) {
    val co = encoder.beginStructure(descriptor)
    co.encodeStringElement(descriptor, 0, value.type.toString())
    co.encodeIntElement(descriptor, 1, value.amount)
    co.encodeSerializableElement(descriptor, 2, JsonObject.serializer(), NBTItem(value).toJson())
    co.endStructure(descriptor)
  }

  override fun deserialize(decoder: Decoder): ItemStack {
    val cd = decoder.beginStructure(descriptor)
    var type: Material? = null
    var amount: Int? = null
    var nbt: JsonObject? = null

    loop@ while (true) {
      when (val i = cd.decodeElementIndex(descriptor)) {
        CompositeDecoder.READ_DONE -> break@loop
        0 -> type = Material.getMaterial(cd.decodeStringElement(descriptor, i))
        1 -> amount = cd.decodeIntElement(descriptor, i)
        2 -> nbt = cd.decodeSerializableElement(descriptor, i, JsonObject.serializer())
      }
    }

    cd.endStructure(descriptor)

    return item(type ?: Material.AIR, amount ?: 1) {
      nbt {
        nbt?.toNBT()?.let(it::mergeCompound)
      }
    }
  }
}