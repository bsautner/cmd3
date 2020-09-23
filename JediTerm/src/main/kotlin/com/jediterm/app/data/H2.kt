package com.jediterm.app.data

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.util.*

class H2() {

    val conn : Connection = DriverManager.getConnection("jdbc:h2:file:~/test3.db", "sa", "")
    private val IN = "INSERT INTO CMDS" +
            "  (command) VALUES " +
            " (?);"

    val cc =
       """
create table IF NOT EXISTS CMDS
(
    COMMAND VARCHAR       not null,
    COUNT   INT default 0 not null
);

create unique index IF NOT EXISTS CMDS2_UINDEX
    on CMDS (COMMAND);
       """.trimIndent()


    fun connect() {

        val c = conn.prepareStatement(cc)
        c.executeUpdate()
        conn.commit()

        val s = conn.prepareStatement(IN)
        s.setString(1, "test ${UUID.randomUUID()}")
        s.executeUpdate()
        conn.commit()
        conn.close()
    }

}