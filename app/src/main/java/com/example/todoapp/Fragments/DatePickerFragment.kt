package com.example.todoapp.Fragments

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*


class DatePickerFragment : DialogFragment() {
    private var dateSetListener // listener object to get calling fragment listener
            : OnDateSetListener? = null
    var myDatePicker: DatePickerDialog? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c: Calendar = Calendar.getInstance()
        val year: Int = c.get(Calendar.YEAR)
        val month: Int = c.get(Calendar.MONTH)
        val day: Int = c.get(Calendar.DAY_OF_MONTH)
        dateSetListener = getTargetFragment() as OnDateSetListener? // getting passed fragment
        myDatePicker = DatePickerDialog(
            this.requireContext(),
            dateSetListener,
            year,
            month,
            day
        ) // DatePickerDialog gets callBack listener as 2nd parameter
        // Create a new instance of DatePickerDialog and return it
        return myDatePicker as DatePickerDialog
    }
}