import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.InnerHTML
import react.dom.div
import react.dom.input

external interface WelcomeProps : RProps {
    var name: String
}

data class WelcomeState(val name: String, val compiled: String) : RState

@JsExport
class Welcome(props: WelcomeProps) : RComponent<WelcomeProps, WelcomeState>(props) {

    init {
        state = WelcomeState(props.name, "ok")
    }

    override fun RBuilder.render() {
        div {
            attrs["dangerouslySetInnerHTML"] = InnerHTML("Hello, ${state.compiled}")
            //+"Hello, ${state.compiled}"
        }
        input {
            attrs {
                type = InputType.text
                value = state.name
                onChangeFunction = { event ->
                    val typed = (event.target as HTMLInputElement).value
                    setState(
                            WelcomeState(name = typed, compiled = parseMd(typed))
                    )
                }
            }
        }
    }
}

fun parseMd(src: String): String {
    val flavour: MarkdownFlavourDescriptor = CommonMarkFlavourDescriptor()
    val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(src)
    val html = HtmlGenerator(src, parsedTree, flavour).generateHtml()
    return html.substring("<body>".length, html.length - "</body>".length)
}
