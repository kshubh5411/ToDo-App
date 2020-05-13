package com.example.todoapp.Utils

import android.R
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.example.todoapp.NotesActivity


class NotifyService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        val mNM =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = Notification(
            R.drawable.alert_light_frame,
            "Notify Alarm strart",
            System.currentTimeMillis()
        )
        val myIntent = Intent(this, NotesActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, myIntent, 0)
        /*notification.setLatestEventInfo(this, "Notify label", "Notify text", contentIntent)
        mNM.notify(NOTIFICATION, notification)*/
    }

}
