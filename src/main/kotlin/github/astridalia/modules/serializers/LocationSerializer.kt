package github.astridalia.modules.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Bukkit
import org.bukkit.Location

class LocationSerializer : KSerializer<Location> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Location") {
        element<String>("world")
        element<Double>("x")
        element<Double>("y")
        element<Double>("z")
        element<Float>("yaw")
        element<Float>("pitch")
    }

    override fun deserialize(decoder: Decoder): Location {
        val input = decoder.beginStructure(descriptor)
        val world = input.decodeStringElement(descriptor, 0)
        val x = input.decodeDoubleElement(descriptor, 1)
        val y = input.decodeDoubleElement(descriptor, 2)
        val z = input.decodeDoubleElement(descriptor, 3)
        val yaw = input.decodeFloatElement(descriptor, 4)
        val pitch = input.decodeFloatElement(descriptor, 5)
        input.endStructure(descriptor)
        return Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)
    }

    override fun serialize(encoder: Encoder, value: Location) {
        val output = encoder.beginStructure(descriptor)
        output.encodeStringElement(descriptor, 0, value.world?.name ?: "world")
        output.encodeDoubleElement(descriptor, 1, value.x)
        output.encodeDoubleElement(descriptor, 2, value.y)
        output.encodeDoubleElement(descriptor, 3, value.z)
        output.encodeFloatElement(descriptor, 4, value.yaw)
        output.encodeFloatElement(descriptor, 5, value.pitch)
        output.endStructure(descriptor)
    }

}
