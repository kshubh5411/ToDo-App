package com.example.todoapp.Repository;

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.todoapp.Dao.CategoryDao
import com.example.todoapp.Dao.NoteDao
import com.example.todoapp.Model.CategoryList
import com.example.todoapp.Model.Note
import java.util.concurrent.Executor
import java.util.concurrent.Executors


@Database(
    entities = [Note::class, CategoryList::class],
    version = 10,
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase(){
    abstract fun noteDao(): NoteDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var instance: NoteDatabase? = null
        fun getInstance(context: Context):NoteDatabase= instance?: synchronized(this){
            instance?: buildDatabase(context).also{
                instance=it
            }
        }

        private fun buildDatabase(context: Context): NoteDatabase {
                return Room.databaseBuilder(context,NoteDatabase::class.java,"Note_Database")
                    .addCallback(object:Callback(){
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Executors.newSingleThreadExecutor().execute(object: Runnable {
                                override fun run() {
                                    prePopulateDb(getInstance(context))
                                }
                            })
                        }
                    })
                    .build()
        }

        private fun prePopulateDb( instance: NoteDatabase) {


            val allCategory=CategoryList("All")
            val defaultCategory=CategoryList("Default")
            val finishedCategory=CategoryList("Finished")
            val categoryLists= arrayListOf<CategoryList>(allCategory,defaultCategory,finishedCategory)
            instance.categoryDao().insertInitialData(categoryLists)

        }

    }
}
