package github.astridalia.modules.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.util.Vector

object VectorSerializer : KSerializer<Vector> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("vector") {
        element<Double>("x")
        element<Double>("y")
        element<Double>("z")
    }

    override fun deserialize(decoder: Decoder): Vector {
        val input = decoder.beginStructure(descriptor)
        val x = input.decodeDoubleElement(descriptor, 0)
        val y = input.decodeDoubleElement(descriptor, 1)
        val z = input.decodeDoubleElement(descriptor, 2)
        input.endStructure(descriptor)
        return Vector(x, y, z)
    }

    override fun serialize(encoder: Encoder, value: Vector) {
        val output = encoder.beginStructure(descriptor)
        output.encodeDoubleElement(descriptor, 0, value.x)
        output.encodeDoubleElement(descriptor, 1, value.y)
        output.encodeDoubleElement(descriptor, 2, value.z)
        output.endStructure(descriptor)
    }
}