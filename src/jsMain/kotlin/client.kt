import react.dom.render
import kotlinx.browser.document
import kotlinx.browser.window
import react.dom.h3

fun main() {
    window.onload = {
        render(document.getElementById("root")) {
            child(Welcome::class) {
                attrs {
                    name = "Kotlin/JS"
                }
            }
            h3 {
                +"All notes:"
            }
            child(MarkdownList::class) {}
        }
    }
}
