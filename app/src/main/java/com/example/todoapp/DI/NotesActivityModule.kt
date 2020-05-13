package com.example.todoapp.DI

import android.app.Application
import androidx.lifecycle.ViewModelProviders
import com.example.todoapp.NotesActivity
import com.example.todoapp.Repository.NoteRepository
import com.example.todoapp.ViewModel.NoteViewModel
import com.example.todoapp.ViewModel.NoteViewModelFactory
import dagger.Module
import dagger.Provides


@Module
class NotesActivityModule(
    private var notesActivity: NotesActivity,
    private val application: Application
) {

    @Provides
    fun provideNoteActivityViewModel(noteViewModelFactory: NoteViewModelFactory): NoteViewModel {
        return ViewModelProviders.of(notesActivity, noteViewModelFactory)
            .get(NoteViewModel::class.java)
    }

    @Provides
    fun provideNoteViewModelFactory(noteRepository: NoteRepository): NoteViewModelFactory {
        return NoteViewModelFactory(noteRepository)
    }

    @Provides
    fun provideRepository(): NoteRepository {
        return NoteRepository(application)
    }



    /*@Provides
    fun schedulerProvider(): SchedulerProvider {
        return SchedulerProvider.instance
    }*/
}