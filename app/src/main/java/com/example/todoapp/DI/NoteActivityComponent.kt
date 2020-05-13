package com.example.todoapp.DI

import com.example.todoapp.Fragments.*
import com.example.todoapp.NotesActivity
import dagger.Component


@Component(modules = [NotesActivityModule::class])
interface NoteActivityComponent {
    fun inject(notesActivity: NotesActivity)
    fun inject(noteListFragment: NoteListFragment)
    fun inject(addNoteFragment: AddNoteFragment)
    fun inject(noteFilterFragment: NoteFilterFragment)
    fun inject(noteGridFragment: NoteGridFragment)
    fun inject(editCategoryFragment: EditCategoryFragment)
}