import kotlinx.datetime.Instant

data class Note(var content: String, val meta: NoteMeta)

data class NoteMeta(val id: Int, val name: String, val created: Instant)
