package app.data

import java.sql.Connection
import java.sql.DriverManager
import java.util.*
import kotlin.collections.ArrayList

class H2 {



    private val insertCommandSql =
        """
        INSERT INTO COMMANDS (command) VALUES (?);
        """.trimIndent()

    private val getCommandsSql =
        """
            select * from COMMANDS order by COUNT;
        """.trimIndent()

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
        val connection : Connection = getConnection()
        val c = connection.prepareStatement(createCommandsTable)
        c.executeUpdate()
        connection.commit()

        connection.close()
    }

    fun insertCommand(command : String) {
        val connection : Connection = getConnection()
        val s = connection.prepareStatement(insertCommandSql)
        s.setString(1, command)
        s.executeUpdate()
        connection.commit()
        connection.close()
    }

    fun getCommands() : List<Command> {
        val connection : Connection = getConnection()
        val s = connection.prepareStatement(getCommandsSql)
        val res = s.executeQuery()
        val list : MutableList<Command> = ArrayList()

        while (res.next()) {
            val cmd = res.getString("COMMAND")
            val count = res.getInt("COUNT")
            list.add(Command(cmd, count))
        }
        return list
    }

    companion object {
        fun getConnection() : Connection {
            return  DriverManager.getConnection("jdbc:h2:file:~/.cmd3/test4.db", "sa", "")
        }
    }

}