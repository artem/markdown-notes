import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Note(var content: String, val meta: NoteMeta)

@Serializable
data class NoteMeta(val id: Int, val name: String)
