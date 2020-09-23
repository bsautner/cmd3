package com.jediterm.app.data

import org.junit.Test


internal class H2Test {

    @Test
    fun connect() {
        val h2 = H2()
        h2.connect()

    }
}