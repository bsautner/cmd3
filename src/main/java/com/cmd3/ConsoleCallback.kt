package com.cmd3

interface ConsoleCallback {

    fun prompt(enter: Boolean)
    fun setTitle(value: String)
    fun print(output: String)

}