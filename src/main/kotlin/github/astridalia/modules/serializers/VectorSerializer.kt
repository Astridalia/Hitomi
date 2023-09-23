package github.astridalia.modules.serializers

import github.astridalia.modules.models.VectorSurrogate
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.util.Vector

object VectorSerializer : KSerializer<Vector> {
    override val descriptor: SerialDescriptor = VectorSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Vector) {
        encoder.encodeSerializableValue(VectorSurrogate.serializer(), VectorSurrogate(value))
    }

    override fun deserialize(decoder: Decoder): Vector {
        val surrogate = decoder.decodeSerializableValue(VectorSurrogate.serializer())
        return Vector(surrogate.x, surrogate.y, surrogate.z)
    }
}