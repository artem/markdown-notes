import kotlinx.browser.document
import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.*

external interface WelcomeProps : RProps {
    var name: String
}

data class WelcomeState(val name: String, val compiled: String) : RState

@JsExport
class Welcome(props: WelcomeProps) : RComponent<WelcomeProps, WelcomeState>(props) {

    init {
        state = WelcomeState(props.name, props.name)
    }

    override fun RBuilder.render() {
        div {
            attrs["dangerouslySetInnerHTML"] = InnerHTML(state.compiled)
            //+"Hello, ${state.compiled}"
        }
        textArea {
            attrs {
                id = "note-content"
                +state.name
            }

        }

        button {
            attrs {
                onClickFunction = { _ ->
                    val typed = (document.getElementById("note-content") as HTMLTextAreaElement).value
                    setState(
                        WelcomeState(name = typed, compiled = parseMd(typed))
                    )
                }
            }
            +"Preview"
        }
    }
}

fun parseMd(src: String): String {
    val flavour: MarkdownFlavourDescriptor = CommonMarkFlavourDescriptor()
    val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(src)
    val html = HtmlGenerator(src, parsedTree, flavour).generateHtml()
    return html.substring("<body>".length, html.length - "</body>".length)
}
