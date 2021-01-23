import java.sql.Connection
import java.sql.DriverManager

class DatabaseInstance(path: String) {
    val connection: Connection = DriverManager.getConnection("jdbc:sqlite:$path")

    fun insertNote(content: String, meta: NoteMeta) {
        val statement = connection.createStatement()
        statement.queryTimeout = 30
        statement.executeUpdate("INSERT INTO NotesMeta (id)\n" +
                "VALUES ('${meta.id.replace("'", "''")}');"
        )
        statement.executeUpdate("INSERT INTO Notes (noteId, content)\n" +
                "VALUES ('${meta.id.replace("'", "''")}', " +
                "'${content.replace("'", "''")}');"
        )
    }

    fun updateNote(content: String, note: Note) {
        val statement = connection.createStatement()
        statement.queryTimeout = 30
        statement.executeUpdate("UPDATE Notes\n" +
                "SET content = '${content.replace("'", "''")}'\n" +
                "WHERE noteId = '${note.meta.id.replace("'", "''")}'"
        )
    }

    fun removeNote(meta: NoteMeta) {
        val statement = connection.createStatement()
        statement.queryTimeout = 30
        val id = meta.id.replace("'", "''")
        statement.executeUpdate("DELETE FROM NotesMeta " +
                "WHERE id = '$id';"
        )
        statement.executeUpdate("DELETE FROM Notes " +
                "WHERE noteId = '$id';"
        )
    }

    fun getNotesList(): List<NoteMeta> {
        val statement = connection.createStatement()
        statement.queryTimeout = 30
        val rs = statement.executeQuery("select * from NotesMeta")
        val res = mutableListOf<NoteMeta>()
        while (rs.next()) {
            res.add(NoteMeta(rs.getString("id")))
        }

        return res
    }

    fun getNote(id: String): Note? {
        val escaped = id.replace("'", "''")
        val statement = connection.createStatement()
        statement.queryTimeout = 30
        val rs = statement.executeQuery("select noteId,content from Notes WHERE noteID = '$escaped'")
        while (rs.next()) {
            return Note(rs.getString("content"), NoteMeta(rs.getString("noteId")))
        }

        return null
    }
}