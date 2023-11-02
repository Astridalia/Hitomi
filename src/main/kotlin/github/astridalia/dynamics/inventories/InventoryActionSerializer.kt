package github.astridalia.dynamics.inventories

import github.astridalia.dynamics.CustomDynamicActions
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class InventoryActionSerializer : KSerializer<CustomDynamicActions> {
    override val descriptor = String.serializer().descriptor

    override fun deserialize(decoder: Decoder): CustomDynamicActions {
        return CustomDynamicActions.valueOf(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: CustomDynamicActions) {
        encoder.encodeString(value.toString())
    }
}
