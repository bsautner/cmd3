package com.jediterm.app.data

import java.sql.Connection
import java.sql.DriverManager
import java.util.*

class H2 {

    private val connection : Connection = DriverManager.getConnection("jdbc:h2:file:~/.cmd3/test3.db", "sa", "")

    private val insertCommandSql = """
        INSERT INTO COMMANDS (command) VALUES (?);"
    """.trimIndent()

    private val createCommandsTable =
       """
create table IF NOT EXISTS COMMANDS
(
    COMMAND VARCHAR       not null,
    COUNT   INT default 0 not null
);

create unique index IF NOT EXISTS COMMANDS_UINDEX
    on CMDS (COMMAND);
       """.trimIndent()


    fun connect() {

        val c = connection.prepareStatement(createCommandsTable)
        c.executeUpdate()
        connection.commit()

        connection.close()
    }

    fun insertCommand(command : String) {

        val s = connection.prepareStatement(insertCommandSql)
        s.setString(1, command)
        s.executeUpdate()
        connection.commit()
        connection.close()
    }

}