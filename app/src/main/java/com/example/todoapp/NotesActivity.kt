package com.example.todoapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.todoapp.Contract.NoteContract
import com.example.todoapp.DI.DaggerNoteActivityComponent
import com.example.todoapp.DI.NotesActivityModule
import com.example.todoapp.Fragments.*
import com.example.todoapp.Model.Note
import com.example.todoapp.Utils.ActivityUtils
import com.example.todoapp.ViewModel.NoteViewModel
import com.example.todoapp.ViewModel.NoteViewModel.Companion.FRAG_ADD_NOTE
import com.example.todoapp.ViewModel.NoteViewModel.Companion.FRAG_EDIT_CATEGORY
import com.example.todoapp.ViewModel.NoteViewModel.Companion.FRAG_NOTE_GRID_DASHBOARD
import com.example.todoapp.ViewModel.NoteViewModel.Companion.FRAG_NOTE_LIST_DASHBOARD
import com.example.todoapp.ViewModel.NoteViewModel.Companion.GRID
import com.example.todoapp.ViewModel.NoteViewModel.Companion.LIST
import com.example.todoapp.ViewModel.NoteViewModel.Companion.REQUEST_ADD_NOTE
import com.example.todoapp.ViewModel.NoteViewModel.Companion.REQUEST_EDIT_NOTE
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


const val TO_DO_TITLE = "ToDo App"

class NotesActivity : AppCompatActivity(), NoteContract.noteClick,
    AddNoteFragment.SaveButtonClickListener, View.OnClickListener {
    @Inject
    lateinit var noteViewModel: NoteViewModel
    private var noteListFragment: NoteListFragment? = null
    private var noteGridFragment: NoteGridFragment? = null
    private var notefilterFragment: NoteFilterFragment? = null
    private var editCategoryFragment: EditCategoryFragment? = null
    private var addNoteFragment: AddNoteFragment? = null
    private var listIcon: ImageView? = null
    private var gridIcon: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpDaggerComponent()
        initview()
        setUpView()
        initData()
    }


    fun initview() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayShowTitleEnabled(true);

        listIcon = findViewById(R.id.list_icon)
        gridIcon = findViewById(R.id.grid_icon)
        listIcon?.setOnClickListener(this)
        gridIcon?.setOnClickListener(this)
    }


    private fun setUpView() {

        if (noteViewModel != null) {
            noteViewModel.perFormInitialSetUp()
            val frag_code = noteViewModel.getCurrentSelectedFragmentCode()
            showThisFragment(getFragmentByCode(frag_code), frag_code)
        }

        floatingButton.setOnClickListener(View.OnClickListener {
            if (noteViewModel != null) {
                noteViewModel.onFloatingButtonClicked(false)
                val frag_code = noteViewModel.getCurrentSelectedFragmentCode()
                showThisFragment(getFragmentByCode(frag_code), frag_code)
            }
        })
    }

    fun initData() {

        if (noteViewModel != null) {
            noteViewModel.getCategory()
        }
    }


    fun showThisFragment(fragment: Fragment, tag: Int) {
        when (tag) {
            FRAG_NOTE_LIST_DASHBOARD -> {
                loadDashBoardFragment(fragment, tag)
            }
            FRAG_NOTE_GRID_DASHBOARD -> {
                loadDashBoardFragment(fragment, tag)
            }
            FRAG_EDIT_CATEGORY -> {
                loadEditCategoryFragment(fragment, tag)
            }
            else -> {
                loadAddNoteFragment(fragment, tag)
            }

        }
    }


    fun getFragmentByCode(fragmentCode: Int): Fragment {
        when (fragmentCode) {
            FRAG_ADD_NOTE -> {
                if (addNoteFragment != null && addNoteFragment?.fragmentManager != null) {
                    addNoteFragment?.fragmentManager?.beginTransaction()
                        ?.remove(addNoteFragment as Fragment)?.commitAllowingStateLoss();
                }
                addNoteFragment = AddNoteFragment().newInstance(null, REQUEST_ADD_NOTE)
                return addNoteFragment as Fragment
            }
            FRAG_NOTE_GRID_DASHBOARD -> {
                if (noteGridFragment != null && noteGridFragment?.fragmentManager != null) {
                    noteGridFragment?.fragmentManager?.beginTransaction()
                        ?.remove(noteGridFragment as Fragment)?.commitAllowingStateLoss();
                }
                noteGridFragment = NoteGridFragment().newInstance()
                return noteGridFragment as Fragment
            }
            FRAG_EDIT_CATEGORY -> {
                if (editCategoryFragment != null && editCategoryFragment?.fragmentManager != null) {
                    editCategoryFragment?.fragmentManager?.beginTransaction()
                        ?.remove(editCategoryFragment as Fragment)?.commitAllowingStateLoss()
                }
                editCategoryFragment = EditCategoryFragment().getInstance()
                return editCategoryFragment as Fragment
            }
            else -> {
                if (noteListFragment != null && noteListFragment?.fragmentManager != null) {
                    noteListFragment?.fragmentManager?.beginTransaction()
                        ?.remove(noteListFragment as Fragment)?.commitAllowingStateLoss();
                }
                noteListFragment = NoteListFragment().newInstance()
                return noteListFragment as Fragment
            }

        }
    }

    fun loadAddNoteFragment(fragment: Fragment, tag: Int) {
        floatingButton.hide()
        filter_fragment_container.visibility = View.GONE
        gridIcon?.visibility = View.GONE
        listIcon?.visibility = View.GONE
        ActivityUtils.replaceFragmentToActivityBackstack(
            supportFragmentManager,
            fragment,
            R.id.fragment_container_main,
            tag.toString()
        )
        if (fragment is AddNoteFragment)
            fragment.setSaveButtonClickListene(this)
    }

    fun loadDashBoardFragment(fragment: Fragment, tag: Int) {

        if (notefilterFragment == null) {
            notefilterFragment = NoteFilterFragment().newInstance()
            ActivityUtils.replaceFragmentToActivity(
                supportFragmentManager,
                notefilterFragment!!,
                R.id.filter_fragment_container,
                tag.toString()
            )
        }

        ActivityUtils.replaceFragmentToActivity(
            supportFragmentManager,
            fragment,
            R.id.fragment_container_main,
            tag.toString()
        )
    }


    fun loadEditCategoryFragment(fragment: Fragment, tag: Int) {
        floatingButton.hide()
        filter_fragment_container.visibility = View.GONE
        gridIcon?.visibility = View.GONE
        listIcon?.visibility = View.GONE
        ActivityUtils.replaceFragmentToActivityBackstack(
            supportFragmentManager,
            fragment,
            R.id.fragment_container_main,
            tag.toString()
        )
    }


    fun setUpDaggerComponent() {
        val noteActivityComponent = DaggerNoteActivityComponent.builder()
            .notesActivityModule(NotesActivityModule(this, application))
            .build()
        noteActivityComponent.inject(this)
    }

    override fun onNoteClicked(note: Note) {
        var addNoteFragment = AddNoteFragment().newInstance(note, REQUEST_EDIT_NOTE.toString())
        loadAddNoteFragment(addNoteFragment, FRAG_ADD_NOTE)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount >= 1) resetView()
        else this.finish()
    }

    override fun onSaveButtonClicked() {
        resetView()
    }

    fun resetView() {
        filter_fragment_container.visibility = View.VISIBLE
        floatingButton.show()
        toolbar?.navigationIcon = null
        title = TO_DO_TITLE
        if (noteViewModel.getCurrentIcon() == GRID)
            gridIcon?.visibility = View.VISIBLE
        else
            listIcon?.visibility = View.VISIBLE
        supportFragmentManager.popBackStack()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.list_icon -> {
                listIcon?.visibility = View.GONE
                gridIcon?.visibility = View.VISIBLE
                noteViewModel.setCurrentIcon(GRID)
                noteViewModel.currentFragmentTag.value = FRAG_NOTE_LIST_DASHBOARD
                val frag_code = noteViewModel.getCurrentSelectedFragmentCode()
                showThisFragment(getFragmentByCode(frag_code), frag_code)
            }
            R.id.grid_icon -> {
                listIcon?.visibility = View.VISIBLE
                gridIcon?.visibility = View.GONE
                noteViewModel.setCurrentIcon(LIST)
                noteViewModel.currentFragmentTag.value = FRAG_NOTE_GRID_DASHBOARD
                val frag_code = noteViewModel.getCurrentSelectedFragmentCode()
                showThisFragment(getFragmentByCode(frag_code), frag_code)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_category_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_category_menu -> {
                noteViewModel.currentFragmentTag.value= FRAG_EDIT_CATEGORY
                val frag_code=noteViewModel.getCurrentSelectedFragmentCode()
                loadEditCategoryFragment(getFragmentByCode(frag_code),FRAG_EDIT_CATEGORY)
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }
}
