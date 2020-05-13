package com.example.todoapp.Adapter

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.Contract.NoteContract
import com.example.todoapp.Contract.NoteCountInterface
import com.example.todoapp.Contract.NoteHolderClickListener
import com.example.todoapp.Fragments.NoteListFragment
import com.example.todoapp.Model.Note
import com.example.todoapp.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.note_item_list.view.*
import java.util.*
import kotlin.collections.ArrayList


class NoteListAdapter(var mnotes: ArrayList<Note>, val noteCountInterface: NoteCountInterface) :
    RecyclerView.Adapter<NoteListAdapter.NoteViewHolder>(), NoteHolderClickListener {

    lateinit var mlistner: NoteContract.noteClick
    lateinit var noteContract: NoteContract.checkBox
    private var filterdList = ArrayList<Note>()
    lateinit var removedItem: Note
    var removedPosition: Int = 0
    val selectedIds: MutableList<Int> = ArrayList<Int>()

    init {
        filterdList = mnotes
    }

    class NoteViewHolder(view: View, val noteHolderClickListener: NoteHolderClickListener) :
        RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {
        val priority: TextView
        val title: TextView
        val checkbox: CheckBox
        val description: TextView
        val layout: MaterialCardView
        val ringImage: ImageView

        init {
            priority = view.txt_view_priority
            title = view.txt_view_title
            checkbox = view.note_checkbox
            description = view.txt_view_description
            layout = view.note_item_card_view
            ringImage = view.ring_image
            layout.setOnClickListener(this)
            layout.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            noteHolderClickListener.onTap(adapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            noteHolderClickListener.onLongTap(adapterPosition)
            return true
        }

    }

    fun update(pos: Int) {
        notifyItemRemoved(pos)
        mnotes.removeAt(pos)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.note_item_list, parent, false)
        return NoteViewHolder(view, this)
    }


    override fun getItemCount(): Int {
        noteContract.checktoShowEmptyImageList(filterdList.size)
        return filterdList.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.priority.text = (filterdList[position].priority).toString()
        holder.description.text = filterdList[position].description
        holder.title.text = filterdList[position].title

        holder.checkbox.isChecked = filterdList[position].isFinish == true

        if (holder.checkbox.isChecked) {
            holder.checkbox.setOnClickListener(View.OnClickListener {
                noteContract.onCheckBoxCheckedClick(filterdList[position], position, holder.layout)
            })

        } else if (!holder.checkbox.isChecked) {
            holder.checkbox.setOnClickListener(View.OnClickListener {
                noteContract.onCheckBoxUncheckedClick(
                    filterdList[position],
                    position,
                    holder.layout
                )
            })
        }

        val id = filterdList[position].id
        if (selectedIds.contains(id))
            holder.layout.setCardBackgroundColor(Color.parseColor("#9e9e9e"))
        else {
            holder.layout.setCardBackgroundColor(Color.parseColor("#212121"))
        }

        if (filterdList[position].alarmActive == false)
            holder.ringImage.visibility = View.GONE
    }

    private fun addIDIntoSelectedIds(pos: Int) {
        val id = mnotes[pos].id
        if (selectedIds.contains(id)) selectedIds.remove(id)
        else selectedIds.add(id)
        if (selectedIds.size < 1) NoteListFragment.isMultiSelection = false
        notifyItemChanged(pos)
        noteCountInterface.noteCountInterface(selectedIds.size)
    }


    fun setlistener(mlistner: NoteContract.noteClick) {
        this.mlistner = mlistner
    }

    fun subscribeContact(noteListFragment: NoteListFragment) {
        this.noteContract = noteListFragment
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


    fun removeNotesOnMultiSelection() {
        var size = filterdList.size
        outer@ for (i in 0 until selectedIds.size) {
            val id = selectedIds[i]
            for (j in 0 until size) {
                if (j < size && id == filterdList[j].id) {
                    filterdList.removeAt(j)
                    size = size - 1
                    notifyItemRemoved(j);continue@outer
                }
            }
        }
        NoteListFragment.isMultiSelection = false
    }

    override fun onLongTap(pos: Int) {
        if (!NoteListFragment.isMultiSelection)
            NoteListFragment.isMultiSelection = true
        addIDIntoSelectedIds(pos)
    }

    override fun onTap(pos: Int) {
        if (NoteListFragment.isMultiSelection)
            addIDIntoSelectedIds(pos)
        else {
            mlistner.onNoteClicked(filterdList[pos])
        }
    }

    fun removeItem(viewHolder: RecyclerView.ViewHolder) {
        removedPosition = viewHolder.adapterPosition
        removedItem = filterdList[viewHolder.adapterPosition]
        filterdList.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)
        Snackbar.make(viewHolder.itemView, "${removedItem.title} deleted.", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                filterdList.add(removedPosition, removedItem)
                noteCountInterface.addNoteOnUndo(removedItem)
                notifyItemInserted(removedPosition)
            }.show()

    }

    private fun convertDpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().getDisplayMetrics().density) as Int
    }
}