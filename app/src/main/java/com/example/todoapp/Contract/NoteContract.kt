package com.example.todoapp.Contract

import android.view.View
import com.example.todoapp.Model.CategoryList
import com.example.todoapp.Model.Note

interface NoteContract {

    interface checkBox
    {
        fun onCheckBoxCheckedClick(note: Note, pos: Int,view: View)
        fun onCheckBoxUncheckedClick(note: Note, pos: Int,view: View)
        fun checktoShowEmptyImageList(size:Int)
    }

    interface noteClick{
        fun onNoteClicked(note:Note)
    }

    interface EditCategoryClick{
        fun onDeleteItemClick(category: CategoryList,pos:Int)
        fun onEditItemClick(category: CategoryList,pos:Int)
    }


}