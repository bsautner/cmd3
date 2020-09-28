package com.app.data

data class Command(val cmd : String, val count : Int) {
    override fun toString(): String {
        return cmd
    }
}