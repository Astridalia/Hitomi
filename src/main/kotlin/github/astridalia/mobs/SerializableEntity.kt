package github.astridalia.mobs

import com.mineinabyss.idofront.serialization.LocationSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.UUID

@Serializable
data class SerializableEntity(
    val _id: String = UUID.randomUUID().toString(),
    val behaviorTypes: EntityBehaviorTypes = EntityBehaviorTypes.FRIENDLY,
    val type: String = EntityType.ZOMBIE.toString(),
    val name: String = _id,
    @Serializable(with = LocationSerializer::class)
    val location: Location,

    @SerialName("properties")
    val properties: HashMap<String, String> = hashMapOf(),

    var dropTable: DropTable = DropTable()
) {

    // NOTE: entities have persistent-containers?
    fun summon(player: Player): Entity? = MobManager.spawnCustomMob(player.location, type, name)
}

