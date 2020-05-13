package com.example.todoapp.ViewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.todoapp.Fragments.FINISH_NOTE
import com.example.todoapp.Model.CategoryList
import com.example.todoapp.Model.Note
import com.example.todoapp.Repository.NoteRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.io


fun <T> MutableLiveData<T>.init(value: T) = apply { this.value = value }


class NoteViewModel(private var noteRepository: NoteRepository) : BaseViewModel() {
    companion object {
        const val FRAG_NOTE_LIST_DASHBOARD = 1001
        const val FRAG_NOTE_GRID_DASHBOARD = 1002
        const val FRAG_ADD_NOTE = 1003
        const val FRAG_EDIT_CATEGORY=1004
        const val REQUEST_EDIT_NOTE = "Edit_Note"
        const val REQUEST_ADD_NOTE = "Add_Note"
        const val LIST = "list"
        const val GRID = "grid"
    }

    private var currentIcon: String = GRID
    var category: List<CategoryList>? = null
    var spinnerArray = ArrayList<String>()
    var spinnerArrayForAddNotes = ArrayList<String>()
    val CategoryList = MutableLiveData<ArrayList<CategoryList>>().init(arrayListOf())
    val categoryloading = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()
    var currentFragmentTag = MutableLiveData<Int>()
    var setFilterFrag = MutableLiveData<Boolean>()
    var floatingActionButton = MutableLiveData<Boolean>()
    var noteList = MutableLiveData<ArrayList<Note>>().init(arrayListOf())
    var isSaved = MutableLiveData<Boolean>()
    var currentCategoryId = MutableLiveData<Int>().init(0)
    var noteId:Long=0


    @SuppressLint("CheckResult")
    fun addCategory(c: CategoryList) {
        noteRepository.addCategory(c)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it)
                    getCategory()
            }
    }

    fun getCategory() {
        categoryloading.value = true
        bindDisposable {
            noteRepository.getCategoryList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { category ->
                    this.category = category
                    CategoryList.value = category as ArrayList<CategoryList>
                    spinnerArray.clear()
                    for (CategoryList in category) {
                        if(CategoryList.category== FINISH_NOTE) continue
                        CategoryList.category?.let { spinnerArray.add(it) }
                    }
                    spinnerArray.add(FINISH_NOTE)
                    categoryloading.value = false
                }
        }
    }

    fun getCategoryForAddNote(): ArrayList<String> {
        spinnerArrayForAddNotes.clear()
        for (string in spinnerArray) {
            if (string == "All" || string == "Finished") continue
            else spinnerArrayForAddNotes.add(string)
        }
        return spinnerArrayForAddNotes
    }


    fun perFormInitialSetUp() {
        currentFragmentTag.value = FRAG_NOTE_LIST_DASHBOARD
        setFilterFrag.value = true
        floatingActionButton.value = true
    }

    fun getCurrentSelectedFragmentCode(): Int {
        if (currentFragmentTag.value == null)
            currentFragmentTag.value= FRAG_NOTE_LIST_DASHBOARD

        return currentFragmentTag.value!!
    }

    fun onFloatingButtonClicked(value: Boolean) {
        currentFragmentTag.value = FRAG_ADD_NOTE
    }

    fun addNote(note: Note) {
        loading.value = true
        bindDisposable {
            noteRepository.addNotes(note = note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Long>() {
                    override fun onSuccess(t: Long) {
                        noteId=t
                        loading.value=false
                        isSaved.value=true
                    }
                    override fun onError(e: Throwable) {
                        loading.value=false
                    }

                })
        }
    }

    fun getAllNotes() {
        //  loading.value = true
        bindDisposable {
            noteRepository.getAllNotes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    noteList.value = it as ArrayList<Note>
                }

        }

    }

    fun getNotesByCategory(category: String, position: Int) {
        bindDisposable {
            noteRepository.getNotesByCategory(category)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    noteList.value = it as ArrayList<Note>

                }
        }
    }

    fun currentCategory(position: Int) {
        currentCategoryId.value = position
    }

    fun updateNotes(note: Note) {
        loading.value = true
        bindDisposable {
            noteRepository.updateNotes(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it>=0) {
                        isSaved.value = true
                        loading.value = false
                    }
                }
        }
    }

    fun getFinishedNotes() {
        bindDisposable {
            noteRepository.getfinishedNotes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    noteList.value = it as ArrayList<Note>
                }
        }
    }

    fun setCurrentIcon(icon: String) {
        currentIcon = icon
    }

    fun getCurrentIcon(): String {
        return currentIcon
    }


    fun deleteCategoryById(Ids: List<Int>) {
        bindDisposable {
            noteRepository.deleteSelectedNotes(Ids)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it) {
                        Log.d("Deleted", "Deleted Successfully")
                    }
                }
        }
    }

    fun deleteNote(note:Note){
        bindDisposable {
            noteRepository.deleteNote(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it) {
                        Log.d("Deleted", "Deleted Successfully")
                    }
                }


        }
    }

    fun deleteCategory(category: CategoryList){
        bindDisposable {
            noteRepository.deleteCategory(category)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }
    }


   fun  deleteNotesByCategory(category: String){
       bindDisposable {
           noteRepository.deleteNotesByCategoryName(category)
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe({
                   getAllNotes()
               })
       }
   }

    fun updateCategoryName(category: CategoryList){
        bindDisposable {
            noteRepository.updateCategoryName(category)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }
    }

    fun updateNotesCategoryName(newName:String,oldName:String){
        bindDisposable {
            noteRepository.updateNotesCategoryName(newName,oldName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    getAllNotes()
                })

        }
    }



}