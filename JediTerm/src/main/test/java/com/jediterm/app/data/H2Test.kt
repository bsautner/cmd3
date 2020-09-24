package com.jediterm.app.data

import com.app.data.H2
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.util.*


internal class H2Test {
    val h2 = H2()
    @Before
    fun setup() {
        h2.connect()
    }



    @Test
    fun testInsert() {

        h2.insertCommand(UUID.randomUUID().toString())

    }

    @Test
    fun testCounterInsert() {
        val command = UUID.randomUUID().toString()

        h2.insertCommand(command)
        h2.insertCommand(command)
        h2.insertCommand(command)
        val c = h2.getCommand(command)
        assertEquals(2, c?.count)
        assertEquals(command, c?.cmd)

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