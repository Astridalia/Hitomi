package github.astridalia.modules.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object ItemStackSerializer : KSerializer<ItemStack> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ItemStack") {
        element<String>("type")
        element<Int>("amount")
        element<Short>("durability")
        element<String?>("displayName")
        element<List<String>?>("lore")
        element<Int>("customModelId")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: ItemStack) {
        encoder.encodeStructure(descriptor) {
           encodeStringElement(descriptor, 0, value.type.name)
            encodeIntElement(descriptor, 1, value.amount)
            encodeShortElement(descriptor, 2, value.durability)
            encodeNullableSerializableElement(descriptor, 3, String.serializer().nullable, value.itemMeta!!.displayName)
            encodeNullableSerializableElement(descriptor, 4, ListSerializer(String.serializer()).nullable, value.itemMeta!!.lore)
            encodeIntElement(descriptor, 5, value.itemMeta!!.customModelData)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): ItemStack {
        return decoder.decodeStructure(descriptor) {
            var type = ""
            var amount = 0
            var durability: Short = 0
            var displayName: String? = null
            var lore: List<String>? = null
            var modelId = 0

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> type = decodeStringElement(descriptor, 0)
                    1 -> amount = decodeIntElement(descriptor, 1)
                    2 -> durability = decodeShortElement(descriptor, 2)
                    3 -> displayName = decodeNullableSerializableElement(descriptor, 3, String.serializer().nullable)
                    4 -> lore =
                        decodeNullableSerializableElement(descriptor, 4, ListSerializer(String.serializer()).nullable)

                    5 -> modelId = decodeIntElement(descriptor, 5)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }

            val itemStack = ItemStack(Material.matchMaterial(type) ?: Material.STONE, amount)
            val itemMeta = itemStack.itemMeta!!
            itemMeta.setDisplayName(displayName)
            itemMeta.lore = lore
            itemMeta.setCustomModelData(modelId)
            itemStack.durability=durability
            
            
            itemStack.setItemMeta(itemMeta)
            
            itemStack
            
        }
    }

    
}