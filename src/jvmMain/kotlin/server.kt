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
import kotlin.random.Random

fun HTML.index() {
    head {
        title("Hello from Ktor!")
    }
    body {
        div {
            id = "root"
        }
        script(src = "/static/output.js") {}
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
            static("/static") {
                resources()
            }
        }
    }.start(wait = true)
}

suspend fun newNote(call: ApplicationCall) {
    val name = call.receiveText()
    val newNoteMeta = NoteMeta(Random.nextInt(), name)
    dummy.add(newNoteMeta)
    dummy1[newNoteMeta.id] = Note("Put *your* __text__ `here`", newNoteMeta)
    call.respond(HttpStatusCode.Accepted, newNoteMeta)
}

suspend fun getNote(call: ApplicationCall) {
    try {
        val id = call.parameters["id"]?.toInt()
        val note = dummy1[id]
        if (note != null) {
            call.respond(note)
        } else {
            call.respond(HttpStatusCode.NotFound, "Unknown note!")
        }
    } catch (e: NumberFormatException) {
        call.respond(HttpStatusCode.BadRequest, "Illegal id!")
    }
}

suspend fun setNote(call: ApplicationCall) {
    try {
        val id = call.parameters["id"]?.toInt()
        val note = dummy1[id]
        if (note != null) {
            note.content = call.receiveText()
            call.respondText("Updated successfully!", status = HttpStatusCode.Accepted)
        } else {
            call.respond(HttpStatusCode.NotFound, "Unknown note!")
        }
    } catch (e: NumberFormatException) {
        call.respond(HttpStatusCode.BadRequest, "Illegal id!")
    }
}

suspend fun deleteNote(call: ApplicationCall) {
    try {
        val id = call.parameters["id"]?.toInt()
        val note = dummy1[id]
        if (note != null) {
            dummy.remove(note.meta)
            dummy1.remove(id)
            call.respondText("Deleted successfully!", status = HttpStatusCode.Accepted)
        } else {
            call.respond(HttpStatusCode.NotFound, "Unknown note!")
        }
    } catch (e: NumberFormatException) {
        call.respond(HttpStatusCode.BadRequest, "Illegal id!")
    }
}

val dummy = mutableListOf(
    NoteMeta(1, "first note"),//, Clock.System.now()),
    NoteMeta(2, "kek"),//, Clock.System.now()),
    NoteMeta(9, "first note"),//, Clock.System.now()),
    NoteMeta(10, "Hello w"),//, Clock.System.now()),
)

val dummy1 = mutableMapOf(
    dummy[0].id to Note("1", dummy[0]),
    dummy[1].id to Note("`2`", dummy[1]),
    dummy[2].id to Note("_3_", dummy[2]),
    dummy[3].id to Note("~4~", dummy[3]),
)
