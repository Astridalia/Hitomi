package github.astridalia.modules.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

class PersistentDataTypeSerializer : KSerializer<PersistentDataType<*, *>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("PersistentDataType") {
        element("primitiveType", String.serializer().descriptor)
        element("complexType", String.serializer().descriptor)
    }

    override fun serialize(encoder: Encoder, value: PersistentDataType<*, *>) {
        val composite = encoder.beginStructure(descriptor)
        composite.encodeStringElement(descriptor, 0, value.primitiveType.name)
        composite.encodeStringElement(descriptor, 1, value.complexType.name)
        composite.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): PersistentDataType<*, *> {
        val composite = decoder.beginStructure(descriptor)

        lateinit var primitiveType: Class<*>
        lateinit var complexType: Class<*>

        loop@ while (true) {
            when (val index = composite.decodeElementIndex(descriptor)) {
                CompositeDecoder.DECODE_DONE -> break@loop
                0 -> primitiveType = Class.forName(composite.decodeStringElement(descriptor, 0))
                1 -> complexType = Class.forName(composite.decodeStringElement(descriptor, 1))
                else -> throw SerializationException("Unknown index $index")
            }
        }

        composite.endStructure(descriptor)

        return when {
            primitiveType == Byte::class.java && complexType == Byte::class.java -> PersistentDataType.BYTE
            primitiveType == Short::class.java && complexType == Short::class.java -> PersistentDataType.SHORT
            primitiveType == Integer::class.java && complexType == Integer::class.java -> PersistentDataType.INTEGER
            primitiveType == Long::class.java && complexType == Long::class.java -> PersistentDataType.LONG
            primitiveType == Float::class.java && complexType == Float::class.java -> PersistentDataType.FLOAT
            primitiveType == Double::class.java && complexType == Double::class.java -> PersistentDataType.DOUBLE
            primitiveType == Byte::class.java && complexType == Boolean::class.java -> PersistentDataType.BOOLEAN
            primitiveType == String::class.java && complexType == String::class.java -> PersistentDataType.STRING
            primitiveType == ByteArray::class.java && complexType == ByteArray::class.java -> PersistentDataType.BYTE_ARRAY
            primitiveType == IntArray::class.java && complexType == IntArray::class.java -> PersistentDataType.INTEGER_ARRAY
            primitiveType == LongArray::class.java && complexType == LongArray::class.java -> PersistentDataType.LONG_ARRAY
            primitiveType == PersistentDataContainer::class.java && complexType == PersistentDataContainer::class.java -> PersistentDataType.TAG_CONTAINER
            else -> throw SerializationException("Unsupported PersistentDataType: $primitiveType, $complexType")
        }
    }
}