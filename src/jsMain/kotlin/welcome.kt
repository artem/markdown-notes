import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.request.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import org.w3c.dom.HTMLTextAreaElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.InnerHTML
import react.dom.button
import react.dom.div
import react.dom.textArea

external interface WelcomeProps : RProps {
    var note: Note
    var onRemoveNote: (NoteMeta) -> Unit
}

data class WelcomeState(val compiled: String) : RState

class Welcome(props: WelcomeProps) : RComponent<WelcomeProps, WelcomeState>(props) {
    init {
        state = WelcomeState(parseMd(props.note.content))
    }

    private fun recompileMd() {
        val typed = (document.getElementById("note-content") as HTMLTextAreaElement).value
        setState(
            WelcomeState(compiled = parseMd(typed))
        )
    }

    override fun RBuilder.render() {
        div {
            attrs["dangerouslySetInnerHTML"] = InnerHTML(state.compiled)
        }
        textArea {
            attrs {
                id = "note-content"
                +props.note.content
            }
        }

        button {
            attrs {
                onClickFunction = { recompileMd() }
            }
            +"Preview"
        }

        button {
            attrs {
                onClickFunction = {
                    val typed = (document.getElementById("note-content") as HTMLTextAreaElement).value
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

        button {
            attrs {
                onClickFunction = {
                    val obj = document.getElementById("note-content") as HTMLTextAreaElement
                    val client = HttpClient(Js)

                    val mainScope = MainScope()
                    mainScope.launch {
                        val resp = client.get<String>("${window.location.origin}/api/notes/${props.note.meta.id}")
                        obj.value = Json.decodeFromString<Note>(resp).content
                        recompileMd()
                    }
                }
            }
            +"Reload"
        }

        button {
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

private fun parseMd(src: String): String {
    val sanitized = src.replace("<", "&lt;")
    val flavour: MarkdownFlavourDescriptor = CommonMarkFlavourDescriptor()
    val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(sanitized)
    val html = HtmlGenerator(sanitized, parsedTree, flavour).generateHtml()
    return html.substring("<body>".length, html.length - "</body>".length)
}
