import kotlinx.browser.window
import kotlinx.datetime.Clock
import react.*
import react.dom.h3

val dummy = listOf(
    NoteMeta(1, "first note", Clock.System.now()),
    NoteMeta(2, "kek", Clock.System.now()),
    NoteMeta(9, "first note", Clock.System.now()),
    NoteMeta(10, "Hello w", Clock.System.now()),
)

external interface CompState : RState {
    var currentNote: NoteMeta?
}

class Container : RComponent<RProps, CompState>() {
    override fun RBuilder.render() {
        child(Welcome::class) {
            attrs {
                name = "Kotlin/JS"
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