package github.astridalia.modules.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Bukkit
import org.bukkit.World

class WorldSerializer : KSerializer<World> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("World") {
        element<String>("name")
    }

    override fun deserialize(decoder: Decoder): World {
        val input = decoder.beginStructure(descriptor)
        val name = input.decodeStringElement(descriptor, 0)
        input.endStructure(descriptor)
        return Bukkit.getWorld(name) ?: throw IllegalArgumentException("World $name does not exist")
    }

    override fun serialize(encoder: Encoder, value: World) {
        val output = encoder.beginStructure(descriptor)
        output.encodeStringElement(descriptor, 0, value.name)
        output.endStructure(descriptor)
    }
}
