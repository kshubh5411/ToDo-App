package com.example.todoapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.Contract.NoteContract
import com.example.todoapp.Fragments.ALL_NOTES
import com.example.todoapp.Fragments.EditCategoryFragment
import com.example.todoapp.Fragments.FINISH_NOTE
import com.example.todoapp.Model.CategoryList
import com.example.todoapp.R
import kotlinx.android.synthetic.main.edit_category_item.view.*

class EditCategoryAdapter(
    var categoryList: ArrayList<CategoryList>?,
    mlistner: EditCategoryFragment
) : RecyclerView.Adapter<EditCategoryAdapter.EditCategoryViewHolder>() {


    var listner: NoteContract.EditCategoryClick? = null

    init {
        listner = mlistner
    }

    class EditCategoryViewHolder(
        view: View,
        var listener: NoteContract.EditCategoryClick?,
        var mcategoryList: ArrayList<CategoryList>?
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var textView: TextView
        var editIV: ImageView
        var deleteIv: ImageView
        var categoryLl: LinearLayout

        init {
            deleteIv = view.edit_category_delete
            textView = view.edit_category_text
            editIV = view.edit_category_edit_icon
            categoryLl = view.category_list_ll
            editIV.setOnClickListener(this)
            deleteIv.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.edit_category_edit_icon -> {
                    listener?.onEditItemClick(
                        mcategoryList?.get(adapterPosition)!!,
                        adapterPosition
                    )
                }
                R.id.edit_category_delete -> {
                    listener?.onDeleteItemClick(
                        mcategoryList?.get(adapterPosition)!!,
                        adapterPosition
                    )
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditCategoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.edit_category_item, parent, false)
        return EditCategoryViewHolder(view, listner, categoryList)
    }

    override fun getItemCount(): Int {
        return categoryList?.size!!
    }

    override fun onBindViewHolder(holder: EditCategoryViewHolder, position: Int) {

       if (categoryList?.get(position)?.category == "Default") {
            holder.editIV.visibility = View.GONE
            holder.deleteIv.visibility = View.GONE
        }
        holder.textView.text = categoryList?.get(position)?.category.toString()

    }

    fun remove(pos: Int) {
        categoryList?.removeAt(pos)
        notifyItemRemoved(pos)
        notifyDataSetChanged()
    }

    fun update(pos: Int) {
        notifyItemChanged(pos)
        notifyDataSetChanged()
    }


}