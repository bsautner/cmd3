package com.cmd3.app.data

import java.sql.Connection
import java.sql.DriverManager

class H2 {
    val connection: Connection
        get() {
            val connection: Connection = DriverManager.getConnection("jdbc:h2:file:~/.cmd3/test4.db", "sa", "")
            connection.autoCommit = true
            return connection
        }


    private val deleteCommandSql = "DELETE FROM COMMANDS WHERE (command = ?);"

    private val insertCommandSql = "INSERT INTO COMMANDS (command) VALUES (?);"

    private val updateCountSql = "UPDATE COMMANDS set COUNT = ? where COMMAND = ?"

    private val selectCommandSql = "select COMMAND, COUNT from COMMANDS where COMMAND = ? limit 1"

    private val getCommandsSql = "select * from COMMANDS order by COUNT DESC;"

    private val createCommandsTable =
        """
create table IF NOT EXISTS COMMANDS
(
    COMMAND VARCHAR       not null,
    COUNT   INT default 0 not null
);

create unique index IF NOT EXISTS COMMANDS_UINDEX
    on COMMANDS (COMMAND);
       """.trimIndent()


    fun connect() {


        val c = connection.prepareStatement(createCommandsTable)
        c.executeUpdate()

    }

    fun insertCommand(command: String) {

        val g = connection.prepareStatement(selectCommandSql)
        g.setString(1, command)
        val sample = g.executeQuery()
        if (sample.next()) {
            println("command $command exists")
            var count = sample.getInt("COUNT") + 1

            val u = connection.prepareStatement(updateCountSql)
            u.setInt(1, count)
            u.setString(2, command)

            u.executeUpdate()


        } else {

            val s = connection.prepareStatement(insertCommandSql)
            s.setString(1, command)
            val x = s.executeUpdate()
            println("command $command inserted $x")
        }

    }


    fun getCommand(command: String): Command? {

        val g = connection.prepareStatement(selectCommandSql)
        g.setString(1, command)
        val sample = g.executeQuery()
        return if (sample.next()) {
            println("command $command exists")
            val count = sample.getInt("COUNT")
            val cmd = sample.getString("COMMAND")

            Command(cmd, count)

        } else {
            null
        }

    }

    fun deleteCommand(c: Command) {

        val s = connection.prepareStatement(deleteCommandSql)
        s.setString(1, c.cmd)
        s.execute()
    }

    fun getCommands(): List<Command> {

        val s = connection.prepareStatement(getCommandsSql)
        val res = s.executeQuery()
        val list: MutableList<Command> = ArrayList()

        while (res.next()) {
            val cmd = res.getString("COMMAND")
            val count = res.getInt("COUNT")
            list.add(Command(cmd, count))
        }
        return list
    }


}