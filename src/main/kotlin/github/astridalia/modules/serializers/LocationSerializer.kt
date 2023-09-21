package github.astridalia.modules.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import org.bukkit.Bukkit
import org.bukkit.Location

object LocationSerializer : KSerializer<Location> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Location") {
        element<String>("worldName")
        element<Double>("x")
        element<Double>("y")
        element<Double>("z")
        element<Float>("yaw")
        element<Float>("pitch")
    }

    override fun serialize(encoder: Encoder, value: Location) {
        val locationData = buildJsonObject {
            put("worldName", value.world?.name ?: "")
            put("x", value.x)
            put("y", value.y)
            put("z", value.z)
            put("yaw", value.yaw)
            put("pitch", value.pitch)
        }
        encoder.encodeString(Json.encodeToString(locationData))
    }

    override fun deserialize(decoder: Decoder): Location {
        val locationData = Json.parseToJsonElement(decoder.decodeString()).jsonObject
        val worldName = locationData["worldName"]?.jsonPrimitive?.content
        val x = locationData["x"]?.jsonPrimitive?.doubleOrNull ?: 0.0
        val y = locationData["y"]?.jsonPrimitive?.doubleOrNull ?: 0.0
        val z = locationData["z"]?.jsonPrimitive?.doubleOrNull ?: 0.0
        val yaw = locationData["yaw"]?.jsonPrimitive?.floatOrNull ?: 0.0f
        val pitch = locationData["pitch"]?.jsonPrimitive?.floatOrNull ?: 0.0f
        val world = worldName?.let { Bukkit.getWorld(it) }
        return Location(world, x, y, z, yaw, pitch)
    }
}