package com.example.todoapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.Contract.NoteContract
import com.example.todoapp.Fragments.NoteGridFragment
import com.example.todoapp.Model.Note
import com.example.todoapp.R
import kotlinx.android.synthetic.main.note_item_grid.view.*
import kotlinx.android.synthetic.main.note_item_list.view.note_checkbox
import java.util.*
import kotlin.collections.ArrayList

class NoteGridAdapter(var mnotes: ArrayList<Note>) :
    RecyclerView.Adapter<NoteGridAdapter.MyViewHolder>() {

    lateinit var mlistner: NoteContract.noteClick
    lateinit var noteContract: NoteContract.checkBox
    private var filterdList = ArrayList<Note>()

    init {
        filterdList = mnotes

    }

    override fun getItemCount(): Int {
        noteContract.checktoShowEmptyImageList(filterdList.size)
        return filterdList.size
    }

    fun update(pos: Int) {
        notifyItemRemoved(pos)
        filterdList.removeAt(pos)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.description.text = filterdList[position].description
        holder.title.text = filterdList[position].title

        holder.checkbox.isChecked = filterdList[position].isFinish == true

        if (holder.checkbox.isChecked) {
            holder.checkbox.setOnClickListener(View.OnClickListener {
                noteContract.onCheckBoxCheckedClick(
                    filterdList[position],
                    position,
                    holder.itemView
                )
            })

        } else if (!holder.checkbox.isChecked) {
            holder.checkbox.setOnClickListener(View.OnClickListener {
                noteContract.onCheckBoxUncheckedClick(
                    filterdList[position],
                    position,
                    holder.itemView
                )
            })
        }
        if (filterdList[position].categoryName != "Finished") {
            holder.itemView.setOnClickListener(View.OnClickListener {
                mlistner.onNoteClicked(filterdList[position])
            })
        }

        if (filterdList[position].alarmActive == false)
            holder.ringImage.visibility = View.GONE
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NoteGridAdapter.MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.note_item_grid, parent, false)
        return MyViewHolder(view)
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title = view.note_title
        var checkbox = view.note_checkbox
        var description = view.note_description
        var ringImage = view.ring_image_grid
    }

    fun setlistener(mlistner: NoteContract.noteClick) {
        this.mlistner = mlistner
    }

    fun subscribeContact(noteGridFragment: NoteGridFragment) {
        this.noteContract = noteGridFragment
    }

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filterdList = mnotes
                } else {
                    val resultList = ArrayList<Note>()
                    for (row in mnotes) {
                        if (row.title?.toLowerCase(Locale.ROOT)
                                ?.contains(charSearch.toLowerCase(Locale.ROOT))!!
                        ) {
                            resultList.add(row)
                        }
                        if (row.description?.toLowerCase(Locale.ROOT)
                                ?.contains(charSearch.toLowerCase(Locale.ROOT))!!
                        ) {
                            resultList.add(row)
                        }
                    }
                    filterdList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterdList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterdList = results?.values as ArrayList<Note>
                notifyDataSetChanged()
            }

        }
    }

}