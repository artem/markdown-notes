import io.ktor.application.*
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.html.*

const val CDN_LINK = "https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta1/dist/"

fun HTML.index() {
    head {
        title("Markdown notes")
        link {
            rel = "stylesheet"
            href = CDN_LINK + "css/bootstrap.min.css"
        }
    }
    body("bg-light") {
        div("container") {
            id = "root"
        }
        script(src = "/static/output.js") {}
        script(src = CDN_LINK + "js/bootstrap.bundle.min.js") {}
    }
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        install(ContentNegotiation) {
            json()
        }
        routing {
            get("/") {
                call.respondHtml(HttpStatusCode.OK, HTML::index)
            }
            route("/api/notes/") {
                get {
                    call.respond(dummy)
                }
                post {
                    newNote(call)
                }
                get("{id}") {
                    getNote(call)
                }
                post("{id}") {
                    setNote(call)
                }
                delete("{id}") {
                    deleteNote(call)
                }
            }
            route("/notes/") {
                get {
                    call.respondHtml {
                        body {
                            ul {
                                for (note in dummy) {
                                    li {
                                        a {
                                            href = "/notes/${note.id}"
                                            +note.id
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                get("{id}") {
                    val note = dummy1[call.parameters["id"]]
                    if (note != null) {
                        call.respondHtml {
                            head {
                                title("Markdown notes")
                                link {
                                    rel = "stylesheet"
                                    href = CDN_LINK + "css/bootstrap.min.css"
                                }
                            }
                            body("bg-light") {
                                div("container bg-white") {
                                    unsafe {
                                        +parseMd(note.content)
                                    }
                                }
                            }
                        }
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Not found")
                    }
                }
            }
            static("/static") {
                resources()
            }
        }
    }.start(wait = true)
}

suspend fun newNote(call: ApplicationCall) {
    val name = call.receiveText()
    val newNoteMeta = NoteMeta(name)
    dummy.add(newNoteMeta)
    dummy1[newNoteMeta.id] = Note("Put *your* __text__ `here`", newNoteMeta)
    call.respond(HttpStatusCode.Accepted, newNoteMeta)
}

suspend fun getNote(call: ApplicationCall) {
    val id = call.parameters["id"]
    val note = dummy1[id]
    if (note != null) {
        call.respond(note)
    } else {
        call.respond(HttpStatusCode.NotFound, "Not found")
    }
}

suspend fun setNote(call: ApplicationCall) {
    val id = call.parameters["id"]
    val note = dummy1[id]
    if (note != null) {
        note.content = call.receiveText()
        call.respondText("Updated successfully!", status = HttpStatusCode.Accepted)
    } else {
        call.respond(HttpStatusCode.NotFound, "Unknown note!")
    }
}

suspend fun deleteNote(call: ApplicationCall) {
    val id = call.parameters["id"]
    val note = dummy1[id]
    if (note != null) {
        dummy.remove(note.meta)
        dummy1.remove(id)
        call.respondText("Deleted successfully!", status = HttpStatusCode.Accepted)
    } else {
        call.respond(HttpStatusCode.NotFound, "Unknown note!")
    }
}

val dummy = mutableListOf(
    NoteMeta("first note"),
    NoteMeta("kek"),
    NoteMeta("firs note"),
    NoteMeta("Hello w"),
)

val dummy1 = mutableMapOf(
    dummy[0].id to Note("1", dummy[0]),
    dummy[1].id to Note("`2`", dummy[1]),
    dummy[2].id to Note("_3_", dummy[2]),
    dummy[3].id to Note("~4~", dummy[3]),
)
