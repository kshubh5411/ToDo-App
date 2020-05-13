package com.example.todoapp.Dao;

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.example.todoapp.Model.Note

@Dao
interface NoteDao {
    @Insert
    fun insert(note: Note?):Long

    @Update
    fun update(note: Note?)

    @Delete
    fun delete(note: Note?)

    @Query("delete from note_table where noteId in (:Ids)")
    fun deleteNoteByIds(Ids: List<Int>)

    @Query("Select * from note_table where categoryName = :categoryName and isFinish= '0' ORDER BY priority desc")
    fun getNotesByCategory(categoryName: String):List<Note>

    @Query("DELETE FROM note_table")
    fun deleteAllNotes()

    @Query("Select * from note_table where isFinish='1' ORDER BY priority desc")
    fun finishedNote(): List<Note>

    @Query("Select * from note_table where isFinish='0' ORDER BY priority desc ")
    fun allNotes(): List<Note>

    @Query("delete from note_table where categoryName= :categoryName")
    fun deleteNotesByCategory(categoryName: String)

    @Query("update note_table set categoryName= :name where categoryName = :categoryName ")
    fun updateNotesCategoryName(name:String, categoryName: String)

}
