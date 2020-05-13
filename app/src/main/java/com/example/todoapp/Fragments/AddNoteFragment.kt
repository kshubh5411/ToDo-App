package com.example.todoapp.Fragments


import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Application
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.view.View.GONE
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.todoapp.Adapter.SpinnerAdapter
import com.example.todoapp.DI.DaggerNoteActivityComponent
import com.example.todoapp.DI.NotesActivityModule
import com.example.todoapp.Model.Note
import com.example.todoapp.NotesActivity
import com.example.todoapp.R
import com.example.todoapp.Service.AlarmReceiver
import com.example.todoapp.ViewModel.NoteViewModel
import com.example.todoapp.ViewModel.NoteViewModel.Companion.REQUEST_ADD_NOTE
import com.example.todoapp.ViewModel.NoteViewModel.Companion.REQUEST_EDIT_NOTE
import com.getbase.floatingactionbutton.FloatingActionButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_add_note.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue


/**
 * A simple [Fragment] subclass.
 */

//TODO Add BootReceiver when device is booted, then enable all alarm again

const val NOTE_ITEM = "note_item_list"
const val ACTION_TYPE = "action_type"
const val ADD_NOTE = "ADD NOTE"
const val EDIT_NOTE = "EDIT NOTE"
const val EXTRA_REMINDER_ID = "reminder_id"
const val EXTRA_REMINDER_TITLE = "reminder_title"
const val EXTRA_REMINDER_DESCRIPTION = "reminder_description"

// Constant values in milliseconds
private const val milMinute = 60000L
private const val milHour = 3600000L
private const val milDay = 86400000L
private const val milWeek = 604800000L
private const val milMonth = 2592000000L

class AddNoteFragment : Fragment() {

    @Inject
    lateinit var noteViewModel: NoteViewModel

    var ringtone: Uri? = null
    lateinit var editTitle: EditText
    lateinit var editDescription: EditText
    lateinit var progressBar: ProgressBar
    private var scroll: ScrollView? = null
    private var mDateText: TextView? = null
    private var mTimeText: TextView? = null
    private var mRepeatText: TextView? = null
    private var mRepeatNoText: TextView? = null
    private var mRepeatTypeText: TextView? = null
    private var ringToneText: TextView? = null
    private var mFAB1: FloatingActionButton? = null
    private var mActive: Boolean? = null
    private var mAlarmStatus: TextView? = null

    private var mCalendar: Calendar? = null
    private var mYear = 0
    private var mMonth: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0
    private var mDay: Int = 0
    private var mRepeatTime: Long = 0
    private var mTime: String? = null
    private var mDate: String? = null
    private var mRepeat: String? = null
    private var mRepeatNo: String? = null
    private var mRepeatType: String? = null

    private var pwindo1: PopupWindow? = null

    private var dateRl: RelativeLayout? = null
    private var timeRl: RelativeLayout? = null
    private var repeatSwitch: Switch? = null
    private var repeatNoRl: RelativeLayout? = null
    private var repeatTypeRl: RelativeLayout? = null
    private var selectSoundRl: RelativeLayout? = null


    var note: Note? = null
    var actionType: String? = REQUEST_ADD_NOTE.toString()
    lateinit var spinnerAdapter: ArrayAdapter<String>
    var spinnerArrayForAddNote = ArrayList<String>()
    var originalSpinnerArray = ArrayList<String>()

    private var mlistener: SaveButtonClickListener? = null


    fun newInstance(note: Note?, actionType: String): AddNoteFragment {
        var fragment = AddNoteFragment()
        var bundle = Bundle()
        bundle.putParcelable(NOTE_ITEM, note)
        bundle.putString(ACTION_TYPE, actionType)
        fragment.arguments = bundle
        return fragment
    }


    fun setSaveButtonClickListene(mlistener: SaveButtonClickListener) {
        this.mlistener = mlistener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setUpDaggerComponent()
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_add_note, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView(view)
        setView()
        setAlarmData()
        setData()
    }


    private fun initView(view: View) {
        editTitle = view.findViewById(R.id.edit_title)
        editDescription = view.findViewById(R.id.edit_description)
        progressBar = view.findViewById(R.id.progress_bar_add_note)
        mFAB1 = view.findViewById(R.id.starred1) as FloatingActionButton
        mAlarmStatus = view.findViewById(R.id.alarmStatus)
        var toolbar = activity?.findViewById<Toolbar>(R.id.toolbar) as androidx.appcompat.widget.Toolbar
        toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_black_24dp, null)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            activity?.onBackPressed()
            toolbar?.navigationIcon = null
        })

    }

    private fun setView() {
        number_picker.minValue = 1
        number_picker.maxValue = 10
        spinnerArrayForAddNote = noteViewModel.getCategoryForAddNote()
        originalSpinnerArray = noteViewModel.spinnerArray
        spinnerAdapter = SpinnerAdapter(
            this.requireContext(),
            spinnerArrayForAddNote
        )
        category_spinner.adapter = spinnerAdapter
        category_spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    var text = view?.findViewById<TextView>(R.id.categoryText)
                    text?.setTextColor(Color.WHITE)
                }
            }
    }


    private fun setAlarmData() {
        val inflater1 =
            this.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout1: View = inflater1.inflate(R.layout.pop_up_alarm_layout, null)
        val display: Display? = activity?.windowManager?.defaultDisplay
        val height1 = display?.height
        val width1 = display?.width

        mDateText = layout1.findViewById(R.id.set_date) as TextView
        mTimeText = layout1.findViewById(R.id.set_time) as TextView
        mRepeatText = layout1.findViewById(R.id.set_repeat) as TextView
        mRepeatNoText = layout1.findViewById(R.id.set_repeat_no) as TextView
        mRepeatTypeText = layout1.findViewById(R.id.set_repeat_type) as TextView
        ringToneText = layout1.findViewById(R.id.sound_text) as TextView

        dateRl = layout1.findViewById(R.id.date)
        timeRl = layout1.findViewById(R.id.time)
        repeatSwitch = layout1.findViewById(R.id.repeat_switch)
        repeatNoRl = layout1.findViewById(R.id.RepeatNo)
        repeatTypeRl = layout1.findViewById(R.id.RepeatType)
        selectSoundRl = layout1.findViewById(R.id.selectSound)

        scroll = layout1.findViewById(R.id.scroll) as ScrollView


        // Initialize default values
        mActive = false
        mRepeat = "false"
        mRepeatNo = Integer.toString(1)
        mRepeatType = "Hour"



        mCalendar = Calendar.getInstance()
        mHour = mCalendar!!.get(Calendar.HOUR_OF_DAY)
        mMinute = mCalendar!!.get(Calendar.MINUTE)
        mYear = mCalendar!!.get(Calendar.YEAR)
        mMonth = mCalendar!!.get(Calendar.MONTH) + 1
        mDay = mCalendar!!.get(Calendar.DATE)

        mDate = "$mDay/$mMonth/$mYear"
        mTime = "$mHour:$mMinute"

        mDateText?.text = mDate
        mTimeText?.text = mTime

        mFAB1?.setOnClickListener {
            try {
                mActive = true
                pwindo1 = PopupWindow(
                    layout1,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
                )
                pwindo1?.showAtLocation(layout1, Gravity.CENTER, 0, 0)
                val cancelbutton =
                    layout1.findViewById<View>(R.id.canceli) as Button
                val submitbutton =
                    layout1.findViewById<View>(R.id.submiti) as Button
                submitbutton.setOnClickListener {
                    pwindo1?.dismiss()
                    mAlarmStatus?.setText("Alarm On")
                }
                cancelbutton.setOnClickListener {
                    mActive = false
                    pwindo1?.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        dateRl?.setOnClickListener(View.OnClickListener {
            setDateDialog()
        })

        timeRl?.setOnClickListener(View.OnClickListener {
            setTime()
        })

        repeatSwitch?.setOnClickListener(View.OnClickListener {
            setRepeatSwitch()
        })
        repeatNoRl?.setOnClickListener(View.OnClickListener {
            setRepeatNo()
        })
        repeatTypeRl?.setOnClickListener(View.OnClickListener {
            setRepeatType()
        })

        selectSoundRl?.setOnClickListener(View.OnClickListener {
            setRingSound()
        })
    }

    private fun setRingSound() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtone)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, ringtone)
        startActivityForResult(intent, 1)
    }

    private fun setRepeatType() {
        val items = arrayOfNulls<String>(5)
        items[0] = "Minute"
        items[1] = "Hour"
        items[2] = "Day"
        items[3] = "Week"
        items[4] = "Month"

        val builder = AlertDialog.Builder(this.requireContext())
        builder.setTitle("Select Type")
        builder.setItems(items) { dialog, item ->
            mRepeatType = items[item]
            mRepeatTypeText?.text = mRepeatType
            mRepeatText?.text = "Every $mRepeatNo $mRepeatType(s)"
        }


        val alert = builder.create()
        alert.show()
    }

    private fun setRepeatNo() {
        val alert = MaterialAlertDialogBuilder(this.requireContext(),R.style.AlertDialogTheme);
        alert.setTitle("Enter Number")
        // Create EditText box to input repeat number
        val input = EditText(this.requireContext())
        input.setInputType(InputType.TYPE_CLASS_NUMBER)
        alert.setView(input)
        alert.setPositiveButton(
            "OK"
        ) { dialogInterface: DialogInterface, i: Int ->
            if (input.text.toString().trim().length == 0) {
                mRepeatNo = Integer.toString(1)
                mRepeatNoText?.text = mRepeatNo
                mRepeatText?.text = "Every $mRepeatNo $mRepeatType(s)"
            } else {
                mRepeatNo = input.text.trim().toString()
                mRepeatNoText?.text = mRepeatNo
                mRepeatText?.text = "Every $mRepeatNo $mRepeatType(s)"
            }

        }
        alert.setNegativeButton(
            "Cancel",
            DialogInterface.OnClickListener() { dialog: DialogInterface?, which: Int -> })
        alert.show()
    }

    private fun setRepeatSwitch() {
        var switchOn = repeatSwitch?.isChecked
        if (switchOn == true) {
            mRepeat = "true"
            mRepeatText?.text = "Every $mRepeatNo $mRepeatType(s)"
        } else {
            mRepeat = "false"
            mRepeatText?.text = getString(R.string.repeatOff)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                1 -> {
                    ringtone = data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                    val ring = RingtoneManager.getRingtone(this.requireContext(), ringtone)
                    val title = ring.getTitle(this.requireContext())
                    ringToneText?.text = title.toString()
                }
            }
        }
    }

    private fun setTime() {
        val cal = Calendar.getInstance()
        val h = cal.get(Calendar.HOUR_OF_DAY)
        val m = cal.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            this.requireContext(),
            TimePickerDialog.OnTimeSetListener { view, hour, minute ->
                mHour = hour
                mMinute = minute
                if (mMinute < 10)
                    mTime = ("" + mHour + ":" + "0" + mMinute)
                else
                    mTime = ("" + mHour + ":" + mMinute)

                mTimeText?.text = mTime
            },
            h,
            m, true
        )
        timePickerDialog.show()
    }

    private fun setDateDialog() {
        val cal = Calendar.getInstance()
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH)
        val d = cal.get(Calendar.DAY_OF_MONTH)

        val datepickerdialog = DatePickerDialog(
            this.requireContext(),
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                mDay = dayOfMonth
                mMonth = monthOfYear + 1
                mYear = year
                mDate = ("" + mDay + "/" + mMonth + "/" + mYear)
                mDateText?.text = mDate

            },
            y,
            m,
            d
        )
        datepickerdialog.show()
    }


    private fun setData() {
        actionType = arguments?.getString(ACTION_TYPE, "note_item_list")
        note = arguments?.getParcelable(NOTE_ITEM)
        if (actionType == REQUEST_ADD_NOTE.toString()) {
            activity?.title = ADD_NOTE
        } else {
            activity?.title = EDIT_NOTE
            edit_title.setText(note?.title)
            edit_description.setText(note?.description)
            number_picker.value = note?.priority?.absoluteValue!!
            var id: Int = 0
            for (i in 0 until spinnerArrayForAddNote.size) {
                if (spinnerArrayForAddNote[i] == note?.categoryName) {
                    id = i
                    break
                }
            }
            category_spinner.setSelection(id)

            /**
             * Set Notification Alarm Data
             */
            if (note?.alarmActive == true)
                mAlarmStatus?.text = "Alarm On"
            else
                mAlarmStatus?.text = "Alarm Off"

            mDate = note?.date
            mTime = note?.time
            mRepeat = note?.repeat
            mRepeatNo = note?.repeatNo
            mRepeatType = note?.repeatType

            //set Data to reminder alarm
            mDateText?.text = mDate
            mTimeText?.text = mTime
            mRepeatNoText?.text = mRepeatNo
            mRepeatTypeText?.text = mRepeatType
            mRepeatText?.text = "Every $mRepeatNo $mRepeatType(s)"

            //setSwitch Button
            if (mRepeat == "true")
                repeatSwitch?.isChecked = true
            else {
                repeatSwitch?.isChecked = false
                mRepeatText?.text = getString(R.string.repeatOff)
            }
            //Set date and time

            val mDateSplit = mDate?.split("/")
            val mTimeSplit = mTime?.split(":")
            mDay = mDateSplit!![0].toInt()
            mMonth = mDateSplit[1].toInt()
            mYear = mDateSplit[2].toInt()
            mHour = mTimeSplit!![0].toInt()
            mMinute = mTimeSplit[1].toInt()

            //Cancel  Past
            AlarmReceiver().cancelAlarm(context, note?.id!!)
        }
        onClickSave(actionType)
    }

    private fun onClickSave(actionType: String?) {
        SaveButton.setOnClickListener(View.OnClickListener {
            val title = edit_title.text.toString()
            val description = edit_description.text.toString()
            val priority = number_picker.value
            val category = category_spinner?.selectedItem.toString()
            var currentCategoryId: Int = 0

            for (i in 0 until originalSpinnerArray.size) {
                if (originalSpinnerArray[i] == category) {
                    currentCategoryId = i
                    break
                }
            }
            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this.context, "Title or Description is Empty", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val newNote = Note(
                    title,
                    description,
                    priority,
                    category,
                    false,
                    mDate,
                    mTime,
                    mRepeat,
                    mRepeatNo,
                    mRepeatType,
                    mActive
                )

                mCalendar = Calendar.getInstance()

                // Set up calender for creating the notification
                mCalendar?.set(Calendar.MONTH, --mMonth)
                mCalendar?.set(Calendar.YEAR, mYear)
                mCalendar?.set(Calendar.DAY_OF_MONTH, mDay)
                mCalendar?.set(Calendar.HOUR_OF_DAY, mHour)
                mCalendar?.set(Calendar.MINUTE, mMinute)
                mCalendar?.set(Calendar.SECOND, 0)


                // Check repeat type
                if (mRepeatType == "Minute") {
                    mRepeatTime = mRepeatNo?.toInt()?.times(milMinute) ?: 0
                } else if (mRepeatType == "Hour") {
                    mRepeatTime = mRepeatNo?.toInt()?.times(milHour) ?: 0
                } else if (mRepeatType == "Day") {
                    mRepeatTime = mRepeatNo?.toInt()?.times(milDay) ?: 0
                } else if (mRepeatType == "Week") {
                    mRepeatTime = mRepeatNo?.toInt()?.times(milWeek) ?: 0
                } else if (mRepeatType == "Month") {
                    mRepeatTime = mRepeatNo?.toInt()?.times(milMonth) ?: 0
                }

                if (actionType == REQUEST_EDIT_NOTE) {
                    if (note != null)
                        newNote.id = note?.id!!
                    noteViewModel.updateNotes(newNote)
                } else {
                    noteViewModel.addNote(newNote)
                }
                noteViewModel.loading.observe(this, Observer {
                    if (it) progressBar.visibility = View.VISIBLE
                    else {
                        progressBar.visibility = GONE
                        noteViewModel.isSaved.observe(this, Observer {
                            if (it) {

                                if (mActive == true && mRepeat == "false") {
                                    AlarmReceiver().setAlarm(
                                        context,
                                        mCalendar,
                                        noteViewModel.noteId.toInt(),
                                        ringtone,
                                        newNote
                                    )
                                } else if (mActive == true && mRepeat == "true") {
                                    AlarmReceiver().setRepeatingAlarm(
                                        context,
                                        mCalendar,
                                        noteViewModel.noteId.toInt(),
                                        ringtone,
                                        newNote,
                                        mRepeatTime
                                    )
                                }
                                Toast.makeText(this.context, "Note Saved", Toast.LENGTH_SHORT)
                                    .show()
                                noteViewModel.currentCategory(currentCategoryId)
                                mlistener?.onSaveButtonClicked()
                            } else
                                Toast.makeText(
                                    this.context,
                                    "Something went wrong",
                                    Toast.LENGTH_SHORT
                                ).show()
                        })
                    }
                })

            }
        })
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

    interface SaveButtonClickListener {
        fun onSaveButtonClicked()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

}
