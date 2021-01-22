import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
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
}

data class WelcomeState(val content: String, val compiled: String) : RState

class Welcome(props: WelcomeProps) : RComponent<WelcomeProps, WelcomeState>(props) {
    init {
        state = WelcomeState(props.note.content, parseMd(props.note.content))
    }

    override fun RBuilder.render() {
        div {
            attrs["dangerouslySetInnerHTML"] = InnerHTML(state.compiled)
            //+"Hello, ${state.compiled}"
        }
        textArea {
            attrs {
                id = "note-content"
                +state.content
            }
        }

        button {
            attrs {
                onClickFunction = {
                    val typed = (document.getElementById("note-content") as HTMLTextAreaElement).value
                    setState(
                        WelcomeState(content = typed, compiled = parseMd(typed))
                    )
                }
            }
            +"Preview"
        }

        button {
            attrs {
                onClickFunction = {
                    window.alert("TODO")
                }
            }
            +"Save"
        }

        button {
            attrs {
                onClickFunction = {
                    window.alert("TODO")
                }
            }
            +"Reload"
        }
    }
}

fun parseMd(src: String): String {
    val sanitized = src.replace("<", "&lt;")
    val flavour: MarkdownFlavourDescriptor = CommonMarkFlavourDescriptor()
    val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(sanitized)
    val html = HtmlGenerator(sanitized, parsedTree, flavour).generateHtml()
    return html.substring("<body>".length, html.length - "</body>".length)
}
