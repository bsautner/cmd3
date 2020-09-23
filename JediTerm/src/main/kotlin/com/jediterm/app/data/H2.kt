package com.jediterm.app.data

import java.sql.Connection
import java.sql.DriverManager

class H2() {

    val conn : Connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "")

    fun connect() {

    }
}