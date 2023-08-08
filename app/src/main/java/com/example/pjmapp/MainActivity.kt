package com.example.pjmapp

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val p2 = sharedPref.getBoolean("sync2", true)

        if (p2) {
            // uruchom powiadomienia
            //Toast.makeText(this, "Powiadomienia!", Toast.LENGTH_SHORT).show()
            val intentNotify : Intent = Intent(this, ReminderBroadcastReceiver::class.java)
            val pendingIntent : PendingIntent = PendingIntent.getBroadcast(this, 0, intentNotify, 0)
            val alarmManager : AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            var time = System.currentTimeMillis()
            var cal : Calendar = Calendar.getInstance()
            cal.timeInMillis = time
            cal.set(Calendar.HOUR_OF_DAY, 8)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            //alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
        }

        supportActionBar?.setTitle("PJM app")
        // binding:
        val btnLekcje = findViewById<Button>(R.id.btn_lekcje)
        val btnPostepy = findViewById<Button>(R.id.btn_postepy)
        val btnUstawienia = findViewById<Button>(R.id.btn_ustawienia)
        val btnQuiz = findViewById<Button>(R.id.btn_quiz)
        // instent do każdego kierunku
        val intentLekcje = Intent(this, LessonsListActivity::class.java)
        val intentPostepy = Intent(this, ProgressActivity::class.java)
        val intentQuiz = Intent(this, QuizzesActivity::class.java)
        val intentUstawienia = Intent(this, SettingsActivity::class.java)
        // listenery do przycisków
        btnLekcje.setOnClickListener{
            startActivity( intentLekcje, null )
        }

        btnPostepy.setOnClickListener{
            startActivity( intentPostepy, null )
        }
        btnUstawienia.setOnClickListener{
            startActivity( intentUstawienia, null )
        }
        btnQuiz.setOnClickListener{
            startActivity(intentQuiz)
        }
    }

    // funkcja do stworzenia kanału dla powiadomień
    @SuppressLint("ServiceCast")
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "PJMappReminderChannel"
            val desc = "PJMapp channel"
            val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
            val channel: NotificationChannel = NotificationChannel("notifyPJMapp", name, importance)
            channel.description = desc
            val notificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }
}