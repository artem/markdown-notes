import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.b
import react.dom.p

external interface MarkdownListProps : RProps {
    var list: List<NoteMeta>
    var selected: NoteMeta?
    var onSelectNote: (NoteMeta) -> Unit
}

class MarkdownList : RComponent<MarkdownListProps, RState>() {
    override fun RBuilder.render() {
        for (note in props.list) {
            p {
                key = note.id
                attrs {
                    onClickFunction = {
                        props.onSelectNote(note)
                    }
                }
                if (note == props.selected) {
                    b { +note.id }
                } else {
                    +note.id
                }
            }
        }
    }
}
