package com.cmd3.app.data

data class Command(val cmd: String, val count: Int) {
    override fun toString(): String {
        return cmd
    }
}