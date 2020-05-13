package com.example.todoapp.Fragments


import android.app.Application
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.todoapp.Adapter.SpinnerAdapter
import com.example.todoapp.DI.DaggerNoteActivityComponent
import com.example.todoapp.DI.NotesActivityModule
import com.example.todoapp.Model.CategoryList
import com.example.todoapp.NotesActivity
import com.example.todoapp.R
import com.example.todoapp.ViewModel.NoteViewModel
import kotlinx.android.synthetic.main.fragment_note_filter.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */

const val ALL_NOTES = "All"
const val FINISH_NOTE = "Finished"

class NoteFilterFragment : Fragment() {


    @Inject
    lateinit var noteViewModel: NoteViewModel

    fun newInstance(): NoteFilterFragment {
        return NoteFilterFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setUpDaggerComponent()
        val view = inflater.inflate(R.layout.fragment_note_filter, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        add_category_button.setOnClickListener(View.OnClickListener {
            openDialog()
        })

        noteViewModel.categoryloading.observe(this, Observer {
            setSpinner()
        })

        noteViewModel.currentCategoryId.observe(this, Observer {
            setSpinner()
            category_spinner.setSelection(it, true)
        })


        category_spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val item: String = parent?.getItemAtPosition(position).toString()
                    if (item == ALL_NOTES)
                        noteViewModel.getAllNotes()
                    else if (item == FINISH_NOTE) {
                        noteViewModel.getFinishedNotes()
                    } else
                        noteViewModel.getNotesByCategory(item, position)
                }
            }

    }


    fun setSpinner() {
        val spinnerAdapter =
            SpinnerAdapter(
                requireContext(),
                noteViewModel.spinnerArray
            )
        category_spinner.adapter = spinnerAdapter
    }


    fun openDialog() {
        val dialog = Dialog(this.requireContext())
        dialog.setContentView(R.layout.add_category_dialog)
        val add =
            dialog.findViewById<Button>(R.id.add_button_dialog)
        val cancel =
            dialog.findViewById<Button>(R.id.cancel_button_dialog)
        add.setOnClickListener {
            val stringText = dialog.findViewById<EditText>(R.id.add_new_category)
            val category = stringText.text.toString()
            if (category.trim { it <= ' ' }.length != 0) {
                var categorySpinner = noteViewModel.spinnerArray
                val duplicateElement =
                    categorySpinner.find { it.toLowerCase().contains(category.toLowerCase()) }
                if (duplicateElement == null) {
                    val categoryList = CategoryList(category)
                    noteViewModel.addCategory(categoryList)
                    dialog.dismiss()
                } else {
                    Toast.makeText(
                        this.requireContext(),
                        "Category Already Present",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this.requireContext(),
                    "Empty!! Category Not Added",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        cancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }


    fun setUpDaggerComponent() {
        val addNoteComponent = DaggerNoteActivityComponent.builder()
            .notesActivityModule(
                NotesActivityModule(
                    activity as NotesActivity,
                    application = Application()
                )
            )
            .build()
        addNoteComponent.inject(this)
    }

}
