package com.example.todoapp.Dao

import androidx.room.*
import com.example.todoapp.Model.CategoryList

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(categoryList: CategoryList?)

    @Delete
    fun deleteCategory(categoryList: CategoryList?)

    @Update
    fun updateCategory(categoryList: CategoryList?)

    @Query("select category_name from category_list where category_id=:categoryId ")
    fun getCategoryName(categoryId: Int): String?

    @Query("select * from category_list")
    fun getAllCategory(): List<CategoryList>

    @Insert
    fun insertInitialData(categoryLists: List<CategoryList>)


}