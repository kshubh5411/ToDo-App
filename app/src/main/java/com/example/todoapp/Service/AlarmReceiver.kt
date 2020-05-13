package com.example.todoapp.Service

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todoapp.Fragments.EXTRA_REMINDER_DESCRIPTION
import com.example.todoapp.Fragments.EXTRA_REMINDER_ID
import com.example.todoapp.Fragments.EXTRA_REMINDER_TITLE
import com.example.todoapp.Model.Note
import com.example.todoapp.R
import java.util.*


private const val CHANNEL_ID = " to_do_id"
const val RING_TONE = "ring"

class AlarmReceiver : BroadcastReceiver() {

    private var alarmManager: AlarmManager? = null
    private val NOTIFICATION_ID = 123
    private var pendingIntent: PendingIntent? = null

    override fun onReceive(context: Context, intent: Intent) {

        var receivedId: Int = intent.getIntExtra(EXTRA_REMINDER_ID, 0)
        var ring: Uri? = intent.getParcelableExtra<Uri>(RING_TONE)
        if(ring==null){
            ring= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        var note_title = intent.getStringExtra(EXTRA_REMINDER_TITLE)
        var note_description = intent.getStringExtra(EXTRA_REMINDER_DESCRIPTION)
        val icon =
            BitmapFactory.decodeResource(context.resources, R.drawable.ic_access_time_grey600_24dp)
        createNotificationChannel(context, ring)
        val notication = NotificationCompat.Builder(context, CHANNEL_ID)
            .setLargeIcon(icon)
            .setContentTitle(note_title)
            .setSmallIcon(R.drawable.ic_alarm_on_white_24dp)
            .setContentText(note_description)
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(
                        BitmapFactory.decodeResource(
                            context.getResources(),
                            R.mipmap.ic_launcher
                        )
                    )
                    .bigLargeIcon(null)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notication)


    }

    fun setAlarm(context: Context?, mCalendar: Calendar?, noteId: Int, ringtone: Uri?, note: Note) {
        alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //put remider Id in Intent Extra
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(EXTRA_REMINDER_ID, noteId)
        intent.putExtra(RING_TONE, ringtone)
        intent.putExtra(EXTRA_REMINDER_TITLE, note.title)
        intent.putExtra(EXTRA_REMINDER_DESCRIPTION, note.description)

        pendingIntent =
            PendingIntent.getBroadcast(context, noteId, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        // Calculate notification time
        val c = Calendar.getInstance()
        var currentTime = c.timeInMillis
        var diffTime = mCalendar?.timeInMillis?.minus(currentTime)!!

        //Start Alarm using Note Time
        alarmManager?.set(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + diffTime,
            pendingIntent
        )

    }


    fun setRepeatingAlarm(
        context: Context?,
        mCalendar: Calendar?,
        noteId: Int,
        ringtone: Uri?,
        note: Note,
        repeatTime: Long
    ) {
        alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //put remider Id in Intent Extra
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(EXTRA_REMINDER_ID, noteId)
        intent.putExtra(RING_TONE, ringtone)
        intent.putExtra(EXTRA_REMINDER_TITLE, note.title)
        intent.putExtra(EXTRA_REMINDER_DESCRIPTION, note.description)

        pendingIntent =
            PendingIntent.getBroadcast(context, noteId, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        // Calculate notification time
        val c = Calendar.getInstance()
        var currentTime = c.timeInMillis
        var diffTime = mCalendar?.timeInMillis?.minus(currentTime)!!

        //Start Alarm using Note Time
        alarmManager?.setRepeating(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + diffTime,
            repeatTime,
            pendingIntent
        )
    }

    fun cancelAlarm(context: Context?,noteId:Int){
        alarmManager=context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        pendingIntent= PendingIntent.getBroadcast(context,noteId,Intent(context,AlarmReceiver::class.java),PendingIntent.FLAG_NO_CREATE)
        if (pendingIntent != null && alarmManager != null) {
            alarmManager?.cancel(pendingIntent)
        }
    }


    fun createNotificationChannel(context: Context, ringtone: Uri?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_ID;
            val descriptionText = "channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            channel.setSound(ringtone, audioAttributes)
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }


}
