package com.example.todoapp.Fragments


import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.Adapter.AutoFitGridLayoutManager
import com.example.todoapp.Adapter.NoteGridAdapter
import com.example.todoapp.Contract.NoteContract
import com.example.todoapp.DI.DaggerNoteActivityComponent
import com.example.todoapp.DI.NotesActivityModule
import com.example.todoapp.Model.Note
import com.example.todoapp.NotesActivity
import com.example.todoapp.R
import com.example.todoapp.ViewModel.NoteViewModel
import kotlinx.android.synthetic.main.fragment_note_grid.view.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class NoteGridFragment : Fragment(), NoteContract.checkBox {

    @Inject
    lateinit var noteViewModel: NoteViewModel
    lateinit var mlistener: NoteContract.noteClick
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: NoteGridAdapter
    lateinit var layoutManager: LinearLayoutManager
    var imageView: LinearLayout? = null


    fun newInstance(): NoteGridFragment {
        return NoteGridFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setUpDaggerComponent()
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_note_grid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView = view.findViewById(R.id.emptyListImage)
        noteViewModel.noteList.observe(this, Observer {
            recyclerView = view.recyclerView
            adapter = NoteGridAdapter(it)
            layoutManager = AutoFitGridLayoutManager(context, 500)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
            adapter.setlistener(mlistener)
            adapter.subscribeContact(this)
            adapter.notifyDataSetChanged()
            runLayoutAnimation(recyclerView)
        })
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

    fun setUpDaggerComponent() {
        val noteGridFragment = DaggerNoteActivityComponent.builder()
            .notesActivityModule(
                NotesActivityModule(
                    activity as NotesActivity,
                    application = Application()
                )
            )
            .build()
        noteGridFragment.inject(this)
    }

    override fun onCheckBoxCheckedClick(note: Note, pos: Int, view: View) {
        adapter.update(pos)
        note.isFinish = false
        noteViewModel.updateNotes(note)
    }

    override fun onCheckBoxUncheckedClick(note: Note, pos: Int, view: View) {
        adapter.update(pos)
        note.isFinish = true
        noteViewModel.updateNotes(note)
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

}
