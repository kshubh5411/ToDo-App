package com.example.todoapp.Fragments

import android.app.AlertDialog
import android.app.Application
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.Adapter.EditCategoryAdapter
import com.example.todoapp.Contract.NoteContract
import com.example.todoapp.DI.DaggerNoteActivityComponent
import com.example.todoapp.DI.NotesActivityModule
import com.example.todoapp.Model.CategoryList
import com.example.todoapp.NotesActivity
import com.example.todoapp.R
import com.example.todoapp.ViewModel.NoteViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import javax.inject.Inject

class EditCategoryFragment : Fragment(), NoteContract.EditCategoryClick {
    fun getInstance(): EditCategoryFragment {
        return EditCategoryFragment()
    }

    @Inject
    lateinit var noteViewModel: NoteViewModel

    var recyclerView: RecyclerView? = null
    var adapter: EditCategoryAdapter? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var categoryList: ArrayList<CategoryList>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        setUpDaggerComponent()
        val view = inflater.inflate(R.layout.edit_category_fragment, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setToolbar()
        categoryList = noteViewModel.CategoryList.value
        var categoryListForEdit=categoryList?.filterNot{categoryList -> (categoryList.category.toString() == ALL_NOTES || categoryList.category.toString() == FINISH_NOTE)
        } as ArrayList<CategoryList>
        recyclerView = view.findViewById(R.id.edit_category_recyclerview)
        adapter = EditCategoryAdapter(categoryListForEdit, this)
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = linearLayoutManager
        recyclerView?.adapter = adapter
        adapter?.notifyDataSetChanged()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setToolbar() {

        var toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)
        toolbar?.title = "Edit Category"
        toolbar?.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_black_24dp, null)
        toolbar?.setNavigationOnClickListener {
            activity?.onBackPressed()
            toolbar.navigationIcon = null
        }
    }


    fun setUpDaggerComponent() {
        var noteActivityComponet = DaggerNoteActivityComponent.builder().notesActivityModule(
            NotesActivityModule(activity as NotesActivity, Application())
        ).build()
        noteActivityComponet.inject(this)
    }

    override fun onDeleteItemClick(Category: CategoryList, pos: Int) {


        val dialog = MaterialAlertDialogBuilder(this.context,R.style.AlertDialogTheme)
        dialog.setTitle("Delete")

        dialog.setMessage("Are you sure to delete this?")
        dialog.setPositiveButton("Yes") { dialog, which ->
            noteViewModel.deleteCategory(Category)
            Toast.makeText(this.context, "Deleted", Toast.LENGTH_SHORT).show()
            adapter?.remove(pos)
            noteViewModel.getCategory()
            noteViewModel.deleteNotesByCategory(category = Category.category!!)
        }
        dialog.setNegativeButton(
            "No"
        ) { _: DialogInterface?, _: Int -> }
        dialog.show()
    }


    override fun onEditItemClick(Category: CategoryList, pos: Int) {
        val dialog = Dialog(this.requireContext())
        dialog.setContentView(R.layout.add_category_dialog)
        val title = dialog.findViewById<TextView>(R.id.dialog_title)
        val save = dialog.findViewById<Button>(R.id.add_button_dialog)
        val cancel = dialog.findViewById<Button>(R.id.cancel_button_dialog)
        val input = dialog.findViewById<EditText>(R.id.add_new_category)
        save.text = "Save"
        title.text = "Edit " + "${Category.category}"
        var oldName = Category.category!!
        input.setText(oldName)

        save.setOnClickListener(View.OnClickListener {
            val text = input.text.trim().toString()
            if (text.trim { it <= ' ' }.length != 0) {
                var categorySpinner = noteViewModel.spinnerArray
                val duplicateElement = categorySpinner.find { it.toLowerCase().contains(text.toLowerCase())
                }
                if (duplicateElement == null) {
                    Category.category = text
                    noteViewModel.updateCategoryName(Category)
                    noteViewModel.updateNotesCategoryName(text, oldName)
                    noteViewModel.getCategory()
                    adapter?.update(pos)
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
        })

        cancel.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }


    /**
     * Hide Options menu
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }


}