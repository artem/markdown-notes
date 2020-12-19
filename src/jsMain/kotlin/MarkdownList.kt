import kotlinx.browser.window
import kotlinx.datetime.Clock
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.p

data class NoteListState(val list: List<NoteMeta>) : RState

class MarkdownList : RComponent<RProps, NoteListState>() {
    init {
        state = NoteListState(
            listOf(
                NoteMeta(1, "first note", Clock.System.now()),
                NoteMeta(2, "kek", Clock.System.now()),
                NoteMeta(9, "first note", Clock.System.now()),
                NoteMeta(10, "Hello w", Clock.System.now()),
            )
        )
    }

    override fun RBuilder.render() {
        for (note in state.list) {
            p {
                key = note.id.toString()
                attrs {
                    onClickFunction = {
                        window.alert("created on ${note.created}")
                    }
                }
                +note.name
            }
        }
    }
}
