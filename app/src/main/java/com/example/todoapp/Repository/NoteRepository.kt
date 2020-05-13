package com.example.todoapp.Repository;

import android.app.Application
import android.telecom.Call
import androidx.lifecycle.MutableLiveData
import com.example.todoapp.Dao.CategoryDao
import com.example.todoapp.Dao.NoteDao
import com.example.todoapp.Model.CategoryList
import com.example.todoapp.Model.Note
import com.example.todoapp.Utils.RxUtils
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.Callable


class NoteRepository(application: Application) {
    private  var noteDao: NoteDao
    private  var categoryDao: CategoryDao
    private var finishedNotes: List<Note> = arrayListOf()
    private var categoryList: List<CategoryList> = arrayListOf()
    private var allNotes: List<Note>   =     arrayListOf()
    private lateinit var categoryNotes:List<Note>

    init {
        val noteDatabase=NoteDatabase.getInstance(application)
        noteDao=noteDatabase.noteDao()
        categoryDao=noteDatabase.categoryDao()
    }


    fun getAllNotes(): Observable<List<Note>> {
        return RxUtils.makeObservable(getAll())
    }

    fun getAll(): Callable<List<Note>> {
        return Callable {
            allNotes=noteDao.allNotes()
            allNotes
        }
    }

    fun getfinishedNotes():Observable<List<Note>>{
        return RxUtils.makeObservable(finishedNotes())
    }

    fun finishedNotes():Callable<List<Note>>{
        return Callable {
            finishedNotes=noteDao.finishedNote()
            finishedNotes
        }
    }

    fun getNotesByCategory(catName:String):Observable<List<Note>>{
        return RxUtils.makeObservable(NotesByCategory(catName))
    }

    fun NotesByCategory(catName:String):Callable<List<Note>>{
        return Callable {
            categoryNotes=noteDao.getNotesByCategory(catName)
            categoryNotes
        }
    }

    fun getCategoryList():Observable<List<CategoryList>>{
        return RxUtils.makeObservable(getAllCategory())
    }

    fun getAllCategory():Callable<List<CategoryList>>{
        return Callable {
            categoryList=categoryDao.getAllCategory()
            categoryList
        }
    }

    fun addCategory(C:CategoryList):Observable<Boolean> {
        return Observable.create { e ->
            categoryDao.insertCategory(C)
            e.onNext(true)
    }}


    fun addNotes(note:Note): Single<Long> {
        return Single.fromCallable(
            Callable {noteDao.insert(note)}
        )
    }


    fun updateNotes(note:Note):Observable<Int>{
        return Observable.create { e ->
                noteDao.update(note)
               e.onNext(note.id)}
        }

    fun deleteSelectedNotes(Ids:List<Int>):Observable<Boolean>{
        return Observable.create{
            noteDao.deleteNoteByIds(Ids)
            it.onNext(true)
        }
    }
    fun deleteNote(note:Note):Observable<Boolean>{
        return Observable.create{
            noteDao.delete(note)
            it.onNext(true)
        }
    }
    fun deleteCategory(category: CategoryList):Observable<Boolean>{
        return Observable.create {
            categoryDao.deleteCategory(category)
            it.onNext(true)
        }
    }

    fun deleteNotesByCategoryName(categoryName:String):Observable<Boolean>{
        return Observable.create{
            noteDao.deleteNotesByCategory(categoryName)
        }
    }

    fun updateNotesCategoryName(newName:String, oldName:String ):Observable<Boolean>{
        return Observable.create{
            noteDao.updateNotesCategoryName(newName,oldName)
        }
    }

    fun updateCategoryName(category: CategoryList):Observable<Boolean>{
        return Observable.create{
            categoryDao.updateCategory(category)
        }
    }

}