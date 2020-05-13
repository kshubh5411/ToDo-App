package com.example.todoapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.todoapp.Fragments.ALL_NOTES
import com.example.todoapp.Fragments.FINISH_NOTE
import com.example.todoapp.R

class SpinnerAdapter(context: Context, mCategory: ArrayList<String>) :
    ArrayAdapter<String>(context, 0, mCategory) {
    var spinnerArray: ArrayList<String>? = null

    init {
        spinnerArray = mCategory
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        return initView(position, convertView, parent)
    }


    fun initView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var retView: View?
        if (convertView == null) {
            retView = LayoutInflater.from(context).inflate(R.layout.spinner_category, parent, false)
        } else {
            retView = convertView
        }
        var catImage: ImageView? = retView?.findViewById(R.id.categoryImage)
        var catName: TextView? = retView?.findViewById(R.id.categoryText)
        var categoryName = spinnerArray?.get(position)?.trim().toString()

        if (categoryName.isNotEmpty()) {
            if (categoryName == FINISH_NOTE) {
                catImage?.setImageResource(R.drawable.ic_finished_category_icon)
            } else if ((categoryName == ALL_NOTES)) {
                catImage?.setImageResource(R.drawable.ic_all_category_icon)

            } else {
                catImage?.setImageResource(R.drawable.ic_category_default_icon)
            }
            catName?.text = categoryName.toString()
        }

        return retView
    }

}