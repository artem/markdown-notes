import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.request.*
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.html.js.onClickFunction
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import react.*
import react.dom.br
import react.dom.button
import react.dom.div
import react.dom.h2

suspend fun fetchNote(id: String): Note {
    val responsePromise = window.fetch("/api/notes/$id")
    val response = responsePromise.await()
    val jsonPromise = response.text()
    val json = jsonPromise.await()
    return Json.decodeFromString(json)
}

suspend fun fetchNotes(): List<NoteMeta> {
    val responsePromise = window.fetch("/api/notes/")
    val response = responsePromise.await()
    val jsonPromise = response.text()
    val json = jsonPromise.await()
    return Json.decodeFromString(json)
}

var curNoteContent: Note? = null

external interface CompState : RState {
    var currentNote: NoteMeta?
    var notesList: List<NoteMeta>
}

class Container : RComponent<RProps, CompState>() {
    override fun CompState.init() {
        notesList = listOf()

        val mainScope = MainScope()
        mainScope.launch {
            val notes = fetchNotes()
            setState {
                notesList = notes
            }
        }
    }

    override fun RBuilder.render() {
        div("row") {
            div("col-md-3 m-3") {
                h2 {
                    +"All notes:"
                }

                child(MarkdownList::class) {
                    attrs {
                        list = state.notesList
                        selected = state.currentNote
                        onSelectNote = { note ->
                            val mainScope = MainScope() // TODO check coroutines
                            mainScope.launch {
                                curNoteContent = fetchNote(note.id)
                                setState {
                                    currentNote = note
                                }
                            }
                        }
                    }
                }


                button(classes = "btn btn-success my-2") {
                    attrs {
                        onClickFunction = {
                            val name = window.prompt("Enter name for new note:", "choco cooky")
                            if (name != null && name.isNotBlank()) {
                                val client = HttpClient(Js)
                                val mainScope = MainScope()
                                mainScope.launch {
                                    val resp = client.post<String> {
                                        url("${window.location.origin}/api/notes/")
                                        body = name
                                    }
                                    val parsed = Json.decodeFromString<NoteMeta>(resp)
                                    curNoteContent = fetchNote(parsed.id)
                                    setState {
                                        notesList += parsed
                                        currentNote = parsed
                                    }
                                }
                            }
                        }
                    }
                    +"New note"
                }

                +"\n"

                button(classes = "btn btn-outline-secondary my-2") {
                    attrs {
                        onClickFunction = {
                            val mainScope = MainScope()
                            mainScope.launch {
                                val notes = fetchNotes()
                                setState {
                                    notesList = notes
                                }
                            }
                        }
                    }
                    +"Reload"
                }
            }

            state.currentNote?.let {
                div("col-md-8") {
                    child(Editor::class) {
                        attrs {
                            note = curNoteContent!!
                            onRemoveNote = { note ->
                                setState {
                                    currentNote = null
                                    notesList -= note
                                }
                            }
                            key = it.hashCode().toString()
                        }
                    }
                }
            }
        }
    }
}
