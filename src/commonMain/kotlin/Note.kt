import kotlinx.serialization.Serializable
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

@Serializable
data class Note(var content: String, val meta: NoteMeta)

@Serializable
data class NoteMeta(val id: String)

fun parseMd(src: String): String {
    val sanitized = src.replace("<", "&lt;")
    val flavour: MarkdownFlavourDescriptor = CommonMarkFlavourDescriptor()
    val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(sanitized)
    val html = HtmlGenerator(sanitized, parsedTree, flavour).generateHtml()
    return html.substring("<body>".length, html.length - "</body>".length)
}
