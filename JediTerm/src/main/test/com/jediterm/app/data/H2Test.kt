package com.jediterm.app.data

import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertFalse
import org.junit.Test
import java.util.*


internal class H2Test {

    @Test
    fun connect() {
        val h2 = H2()
        h2.connect()

    }

    @Test
    fun testInsert() {
        val h2 = H2()
        h2.connect()
        h2.insertCommand(UUID.randomUUID().toString())

    }

    @Test
    fun testGetCommands() {
        val h2 = H2()
        h2.connect()
        h2.insertCommand(UUID.randomUUID().toString())
        val list = h2.getCommands()
        assertFalse(list.isEmpty())

    }
}