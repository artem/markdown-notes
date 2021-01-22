import kotlinx.datetime.Clock
import react.*
import react.dom.h3

val dummy = listOf(
    NoteMeta(1, "first note", Clock.System.now()),
    NoteMeta(2, "kek", Clock.System.now()),
    NoteMeta(9, "first note", Clock.System.now()),
    NoteMeta(10, "Hello w", Clock.System.now()),
)

val dummy1 = mapOf(
    dummy[0] to Note("1", dummy[0]),
    dummy[1] to Note("`2`", dummy[1]),
    dummy[2] to Note("_3_", dummy[2]),
    dummy[3] to Note("~4~", dummy[3]),
)

external interface CompState : RState {
    var currentNote: NoteMeta?
}

class Container : RComponent<RProps, CompState>() {
    override fun RBuilder.render() {
        state.currentNote?.let { currentNote ->
            child(Welcome::class) {
                attrs {
                    note = dummy1[currentNote]!!//TODO fetch from server
                    key = currentNote.hashCode().toString()
                }
            }
        }

        h3 {
            +"All notes:"
        }
        child(MarkdownList::class) {
            attrs {
                list = dummy
                selected = state.currentNote
                onSelectNote = { note ->
                    setState {
                        currentNote = note
                    }
                }
            }
        }
    }
}
