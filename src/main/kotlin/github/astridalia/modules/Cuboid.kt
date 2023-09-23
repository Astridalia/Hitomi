package github.astridalia.modules

import com.mineinabyss.idofront.serialization.VectorSerializer
import com.mineinabyss.idofront.serialization.WorldSerializer
import kotlinx.serialization.Serializable
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.util.Vector

@Serializable
data class Cuboid(
    @Serializable(with = WorldSerializer::class)
    var world: World,
    @Serializable(with = VectorSerializer::class)
    val minimumPoint: Vector,
    @Serializable(with = VectorSerializer::class)
    val maximumPoint: Vector
) : Cloneable, ConfigurationSerializable, Iterable<Block> {

    constructor(cuboid: Cuboid) : this(cuboid.world, cuboid.minimumPoint, cuboid.maximumPoint)

    constructor(world: World, x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double) :
            this(
                world, Vector(x1.coerceAtMost(x2), y1.coerceAtMost(y2), z1.coerceAtMost(z2)),
                Vector(x1.coerceAtLeast(x2), y1.coerceAtLeast(y2), z1.coerceAtLeast(z2))
            )

    constructor(blockPosOne: Location, blockPosTwo: Location) :
            this(
                blockPosOne.world ?: throw IllegalArgumentException("Location has no world"),
                Vector(
                    blockPosOne.x.coerceAtMost(blockPosTwo.x), blockPosOne.y.coerceAtMost(blockPosTwo.y),
                    blockPosOne.z.coerceAtMost(blockPosTwo.z)
                ),
                Vector(
                    blockPosOne.x.coerceAtLeast(blockPosTwo.x), blockPosOne.y.coerceAtLeast(blockPosTwo.y),
                    blockPosOne.z.coerceAtLeast(blockPosTwo.z)
                )
            )

    fun contains(location: Location) =
        location.world?.name == world.name && location.toVector().isInAABB(minimumPoint, maximumPoint)

    fun contains(vector: Vector) = vector.isInAABB(minimumPoint, maximumPoint)

    override fun serialize(): MutableMap<String, Any> = mutableMapOf(
        "name" to world.name,
        "x1" to minimumPoint.x,
        "x2" to maximumPoint.x,
        "y1" to minimumPoint.y,
        "y2" to maximumPoint.y,
        "z1" to minimumPoint.z,
        "z2" to maximumPoint.z
    )

    override fun iterator(): Iterator<Block> = blocks.iterator()

    val blocks: List<Block>
        get() = (minimumPoint.blockX..maximumPoint.blockX).flatMap { x ->
            (minimumPoint.blockY..maximumPoint.blockY).flatMap { y ->
                (minimumPoint.blockZ..maximumPoint.blockZ).mapNotNull { z ->
                    world.getBlockAt(x, y, z)
                }
            }
        }


    val lowerLocation: Location
        get() = world.let { minimumPoint.toLocation(it) }
    val lowerX: Double
        get() = minimumPoint.x
    val lowerY: Double
        get() = minimumPoint.y
    val lowerZ: Double
        get() = minimumPoint.z
    val upperLocation: Location
        get() = world.let { maximumPoint.toLocation(it) }
    val upperX: Double
        get() = maximumPoint.x
    val upperY: Double
        get() = maximumPoint.y
    val upperZ: Double
        get() = maximumPoint.z
    val volume: Int
        get() = ((upperX - lowerX + 1) * (upperY - lowerY + 1) * (upperZ - lowerZ + 1)).toInt()
}
