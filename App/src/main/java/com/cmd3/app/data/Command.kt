package com.cmd3.app.data

data class Command(val cmd: String, val count: Int = 0) {
    override fun toString(): String {
        return cmd
    }
}