package com.example.todoapp.Contract

interface NoteHolderClickListener {
    fun onLongTap(pos:Int)
    fun onTap(pos:Int)
}