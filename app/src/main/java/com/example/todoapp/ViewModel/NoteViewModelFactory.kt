package com.example.todoapp.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.Repository.NoteRepository
import com.example.todoapp.Utils.SchedulerProvider
import io.reactivex.annotations.NonNull
import io.reactivex.annotations.Nullable
import org.jetbrains.annotations.NotNull

class NoteViewModelFactory(private var noteRepository: NoteRepository):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NoteViewModel(noteRepository) as T

    }
}
