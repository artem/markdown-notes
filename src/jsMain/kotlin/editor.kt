import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.request.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.html.contentEditable
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.dom.HTMLDivElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.*
import react.dom.button

external interface EditorProps : RProps {
    var note: Note
    var onRemoveNote: (NoteMeta) -> Unit
}

data class EditorState(val compiled: String) : RState

class Editor(props: EditorProps) : RComponent<EditorProps, EditorState>(props) {
    init {
        state = EditorState(parseMd(props.note.content))
    }

    private fun recompileMd() {
        val typed = (document.getElementById("note-content") as HTMLDivElement).innerText
        setState(
            EditorState(compiled = parseMd(typed))
        )
    }

    override fun RBuilder.render() {
        h2 {
            +"Preview:"
        }
        div("bg-white") {
            attrs["dangerouslySetInnerHTML"] = InnerHTML(state.compiled)
        }

        div {
            div("form-control w-100") {
                attrs {
                    id = "note-content"
                    contentEditable = true
                    +props.note.content
                }
            }

            br {}

            button(classes = "btn btn-success") {
                attrs {
                    onClickFunction = {
                        val typed = (document.getElementById("note-content") as HTMLDivElement).innerText
                        val client = HttpClient(Js)

                        val mainScope = MainScope()
                        mainScope.launch {
                            client.post<Unit> {
                                url("${window.location.origin}/api/notes/${props.note.meta.id}")
                                body = typed
                            }
                        }
                        recompileMd()
                    }
                }
                +"Save"
            }

            +"\n"

            button(classes = "btn btn-outline-secondary") {
                attrs {
                    onClickFunction = { recompileMd() }
                }
                +"Preview"
            }

            +"\n"

            button(classes = "btn btn-outline-secondary") {
                attrs {
                    onClickFunction = {
                        val obj = document.getElementById("note-content") as HTMLDivElement
                        val client = HttpClient(Js)

                        val mainScope = MainScope()
                        mainScope.launch {
                            val resp =
                                client.get<String>("${window.location.origin}/api/notes/${props.note.meta.id}")
                            obj.innerText = Json.decodeFromString<Note>(resp).content
                            recompileMd()
                        }
                    }
                }
                +"Reload"
            }

            +"\n"

            button(classes = "btn btn-outline-danger") {
                attrs {
                    onClickFunction = {
                        val client = HttpClient(Js)
                        val mainScope = MainScope()
                        mainScope.launch {
                            client.delete<Unit>("${window.location.origin}/api/notes/${props.note.meta.id}")
                            props.onRemoveNote(props.note.meta)
                        }
                    }
                }
                +"Delete"
            }
        }
    }
}
