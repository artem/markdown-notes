import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.html.*
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

fun HTML.index() {
    head {
        title("Hello from Ktor!")
    }
    body {
        div {
            unsafe {
                val src = "Some *Markdown*"
                val flavour: MarkdownFlavourDescriptor = CommonMarkFlavourDescriptor()
                val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(src)
                val html = HtmlGenerator(src, parsedTree, flavour).generateHtml()
                val htmlWithoutBody = html.substring("<body>".length, html.length - "</body>".length)
                raw(htmlWithoutBody)
            }
        }
        div {
            id = "root"
        }
        script(src = "/static/output.js") {}
    }
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        routing {
            get("/") {
                call.respondHtml(HttpStatusCode.OK, HTML::index)
            }
            static("/static") {
                resources()
            }
        }
    }.start(wait = true)
}