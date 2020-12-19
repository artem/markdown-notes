import kotlinx.datetime.Instant

class Note(val content: String, val meta: NoteMeta)

data class NoteMeta(val id: Int, val name: String, val created: Instant)