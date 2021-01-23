import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.li
import react.dom.ul

external interface MarkdownListProps : RProps {
    var list: List<NoteMeta>
    var selected: NoteMeta?
    var onSelectNote: (NoteMeta) -> Unit
}

class MarkdownList : RComponent<MarkdownListProps, RState>() {
    override fun RBuilder.render() {
        ul("list-group flex-column") {
            for (note in props.list) {
                li("list-group-item") {
                    key = note.id

                    attrs {
                        if (note == props.selected) {
                            classes = setOf("list-group-item", "active")
                        }
                        onClickFunction = {
                            props.onSelectNote(note)
                        }
                    }

                    +note.id
                }
            }
        }
    }
}
