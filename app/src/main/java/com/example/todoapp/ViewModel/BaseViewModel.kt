package com.example.todoapp.ViewModel

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseViewModel : ViewModel() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun bindDisposable(action: () -> Disposable){
        compositeDisposable.add(action())
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}


