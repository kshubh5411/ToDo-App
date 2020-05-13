package com.example.todoapp.Model;

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "category_list")
@Parcelize
data class CategoryList(
    @ColumnInfo(name = "category_name")
    var category: String?

):Parcelable
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id")
    var id: Int = 0
}


