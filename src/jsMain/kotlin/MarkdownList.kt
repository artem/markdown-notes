import kotlinx.browser.window
import kotlinx.datetime.Clock
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.b
import react.dom.p

external interface MarkdownListProps: RProps {
    var list: List<NoteMeta>
}

external interface MarkdownListState: RState {
    var selected: NoteMeta?
}

class MarkdownList : RComponent<MarkdownListProps, MarkdownListState>() {
    override fun RBuilder.render() {
        for (note in props.list) {
            p {
                key = note.id.toString()
                attrs {
                    onClickFunction = {
                        setState {
                            selected = note
                        }
                    }
                }

                if (note == state.selected) {
                    b { +note.name }
                } else {
                    +note.name
                }
            }
        }
    }
}
