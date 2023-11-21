package github.astridalia.character.stats2

import kotlinx.serialization.SerialName
import org.bson.codecs.pojo.annotations.BsonId

data class Character(
    @BsonId @SerialName("_id") var name: String,
    var stats: StatSystem = StatSystem(),
) {

}
