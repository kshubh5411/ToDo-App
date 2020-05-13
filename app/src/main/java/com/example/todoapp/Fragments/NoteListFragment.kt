package com.example.todoapp.Fragments


import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.Adapter.NoteListAdapter
import com.example.todoapp.Contract.NoteContract
import com.example.todoapp.Contract.NoteCountInterface
import com.example.todoapp.DI.DaggerNoteActivityComponent
import com.example.todoapp.DI.NotesActivityModule
import com.example.todoapp.Model.Note
import com.example.todoapp.NotesActivity
import com.example.todoapp.R
import com.example.todoapp.Service.AlarmReceiver
import com.example.todoapp.ViewModel.NoteViewModel
import kotlinx.android.synthetic.main.note_list_fragment.view.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class NoteListFragment : Fragment(), NoteContract.checkBox, NoteCountInterface {
    @Inject
    lateinit var noteViewModel: NoteViewModel
    lateinit var mlistener: NoteContract.noteClick
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: NoteListAdapter
    lateinit var layoutManager: LinearLayoutManager
    lateinit var deleteIcon: Drawable
    var actionMode: ActionMode? = null
    private var swipeBackground: ColorDrawable = ColorDrawable(Color.parseColor("#FF0000"))
    var imageView: LinearLayout? = null

    companion object {
        var isMultiSelection = false
    }

    fun newInstance(): NoteListFragment {
        return NoteListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setUpDaggerComponent()
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.note_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView = view.findViewById(R.id.emptyListImage)
        deleteIcon =
            ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_delete_black_24dp)!!

        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.removeItem(viewHolder)
                noteViewModel.deleteNote(adapter.removedItem)
                AlarmReceiver().cancelAlarm(context, adapter.removedItem.id)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
                if (dX > 0) {
                    swipeBackground.setBounds(
                        itemView.left,
                        itemView.top,
                        dX.toInt(),
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.left + iconMargin,
                        itemView.top + iconMargin,
                        itemView.left + iconMargin + deleteIcon.intrinsicWidth,
                        itemView.bottom - iconMargin
                    )
                } else {
                    swipeBackground.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.right - iconMargin - deleteIcon.intrinsicWidth,
                        itemView.top + iconMargin,
                        itemView.right - iconMargin,
                        itemView.bottom - iconMargin
                    )
                }

                swipeBackground.draw(c)
                c.save()
                if (dX > 0) {
                    c.clipRect(
                        itemView.left,
                        itemView.top,
                        dX.toInt(),
                        itemView.bottom
                    )
                } else {
                    c.clipRect(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                }
                c.restore()
                deleteIcon.draw(c)
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }
        noteViewModel.noteList.observe(this, Observer {
            isMultiSelection = false
            recyclerView = view.recycler_view
            adapter = NoteListAdapter(it, this)
            layoutManager = LinearLayoutManager(context)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
            adapter.setlistener(mlistener)
            adapter.subscribeContact(this)
            adapter.notifyDataSetChanged()
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(recyclerView)
            runLayoutAnimation(recyclerView)
        })
    }


    fun setUpDaggerComponent() {
        val noteListFragment = DaggerNoteActivityComponent.builder()
            .notesActivityModule(
                NotesActivityModule(
                    activity as NotesActivity,
                    application = Application()
                )
            )
            .build()
        noteListFragment.inject(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NoteContract.noteClick) {
            mlistener = context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement RoomFilterInteractionListener"
            )
        }
    }

    override fun onCheckBoxCheckedClick(note: Note, pos: Int, view: View) {
        val animationUtils =
            AnimationUtils.loadAnimation(this.requireContext(), android.R.anim.slide_out_right)
        view.startAnimation(animationUtils)
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                adapter.update(pos)
            }
        }, 400)

        note.isFinish = false
        animationUtils.cancel()
        noteViewModel.updateNotes(note)
        Toast.makeText(
            this.requireContext(),
            "Task Undo to ${note.categoryName} Category ",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onCheckBoxUncheckedClick(note: Note, pos: Int, view: View) {
        val animationUtils =
            AnimationUtils.loadAnimation(this.requireContext(), android.R.anim.slide_out_right)
        view.startAnimation(animationUtils)
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                adapter.update(pos)
            }
        }, 400)

        note.isFinish = true
        animationUtils.cancel()
        noteViewModel.updateNotes(note)
        Toast.makeText(this.requireContext(), "Task Finished", Toast.LENGTH_LONG).show()
    }

    override fun checktoShowEmptyImageList(size: Int) {
        if (size > 0)
            imageView?.visibility = View.GONE
        else
            imageView?.visibility = View.VISIBLE
    }


    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        val context = recyclerView.context
        val controller: LayoutAnimationController =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        recyclerView.layoutAnimation = controller
        recyclerView.adapter!!.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.recycleview_search, menu)
        val searchItem = menu.findItem(R.id.app_bar_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.getFilter().filter(newText);
                return false
            }
        })
    }

    inner class ActionModeCallback : ActionMode.Callback {
        var shouldResetRecyclerView = true
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.action_delete -> {
                    shouldResetRecyclerView = false
                    adapter.removeNotesOnMultiSelection()
                    noteViewModel.deleteCategoryById(adapter.selectedIds)
                    mode?.setTitle("") //remove item count from action mode.
                    mode?.finish()
                    return true
                }
            }
            return false
        }


        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val inflater = mode?.menuInflater
            inflater?.inflate(R.menu.menu_delete, menu)

            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            if (shouldResetRecyclerView) {
                adapter.selectedIds.clear()
                adapter.notifyDataSetChanged()
            }
            isMultiSelection = false
            actionMode = null
            shouldResetRecyclerView = true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menu?.findItem(R.id.action_delete)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            return true
        }
    }

    override fun noteCountInterface(size: Int) {
        if (actionMode == null)
            actionMode = (context as Activity).startActionMode(ActionModeCallback())
        if (size > 0)
            actionMode?.setTitle("${size} selected")
        else
            actionMode?.finish()
    }

    override fun addNoteOnUndo(note: Note) {
        noteViewModel.addNote(note)
    }

    override fun onDestroy() {
        setHasOptionsMenu(false)
        super.onDestroy()
    }

    /**
     * itemview.right+dx+marginsize,itemviewtop+margin, itemview.right+dx+marginsize+icon+intrinsic_width,itemviewtop-margin
     */

}

