import kotlinx.browser.window
import kotlinx.datetime.Clock
import react.*
import react.dom.h3

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
                list = listOf(
                    NoteMeta(1, "first note", Clock.System.now()),
                    NoteMeta(2, "kek", Clock.System.now()),
                    NoteMeta(9, "first note", Clock.System.now()),
                    NoteMeta(10, "Hello w", Clock.System.now()),
                )
                selected = state.currentNote
                onSelectNote = { note ->
                    window.alert("Called")
                    setState {
                        window.alert("called as well")
                        currentNote = note
                    }
                }
            }
        }
    }
}