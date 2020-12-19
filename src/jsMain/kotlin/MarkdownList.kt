import kotlinx.browser.window
import kotlinx.html.js.onClickFunction
import react.*
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
                key = note.id.toString()
                attrs {
                    onClickFunction = {
                        props.onSelectNote(note)
                        window.alert(props.selected.toString())
                    }
                }
                window.alert("render..." + note.toString() + props.selected?.toString())
                window.alert("" + note.hashCode() + " " + props.selected?.hashCode())
                if (note == props.selected) {
                    window.alert("BOLD!!")
                    b { +note.name }
                } else {
                    +note.name
                }
            }
        }
    }
}
