package com.example.todoapp.Contract

import com.example.todoapp.Model.Note

interface NoteCountInterface {
    fun noteCountInterface(size:Int)
    fun addNoteOnUndo(note: Note)
}