package com.example.todoapp.Model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(
    tableName = "note_table"
)
@Parcelize
data class Note(
    val title: String?,
    val description: String?,
    val priority: Int,
    var categoryName: String?,
    var isFinish: Boolean,
    var date:String?,
    var time:String?,
    var repeat:String?,
    var repeatNo:String?,
    var repeatType:String?,
    var alarmActive:Boolean?
):Parcelable {
    @ColumnInfo(name = "noteId")
    @PrimaryKey(autoGenerate = true)
    var id = 0
  }