package github.astridalia.modules.serializers

import com.fasterxml.jackson.databind.ser.std.StringSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.event.inventory.InventoryType

object InventoryTypeSerializer : KSerializer<InventoryType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("InventoryType", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): InventoryType {
        return InventoryType.entries[decoder.decodeInt()]
    }

    override fun serialize(encoder: Encoder, value: InventoryType) {
        encoder.encodeInt(value.defaultSize)
    }
}