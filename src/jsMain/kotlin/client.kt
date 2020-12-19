import kotlinx.browser.document
import kotlinx.datetime.Clock
import react.dom.h3
import react.dom.render

fun main() {
    render(document.getElementById("root")) {
        child(Welcome::class) {
            attrs {
                name = "Kotlin/JS"
            }
        }
        h3 {
            +"All notes:"
        }
        child(MarkdownList::class) {
            attrs {
                list = listOf(
                    NoteMeta(1, "first note", Clock.System.now()),
                    NoteMeta(2, "kek", Clock.System.now()),
                    NoteMeta(9, "first note", Clock.System.now()),
                    NoteMeta(10, "Hello w", Clock.System.now()),
                )
            }
        }
    }
}
