package com.example.pjmapp

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.URLUtil
import android.widget.LinearLayout.LayoutParams
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class LessonActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lekcja)

        // odebranie dodatkowych informacji z putExtra:
        val lekcjaId : Int = intent.getIntExtra("Id", 1)
        val lekcjaNazwa: String? = intent.getStringExtra("Name")

        if (lekcjaNazwa.isNullOrEmpty()) {
            supportActionBar?.setTitle("Lekcja")
        }
        else {
            supportActionBar?.setTitle(lekcjaNazwa)
        }

        // sprawdzanie osiągnięcia
        if (lekcjaId == 1) {
            oznaczeniePierwszaLekcja()
        }

        // do layoutu ustawienia:
        val opisLekcjiTextView = findViewById<TextView>(R.id.opis_lekcji_test)
        val videoView = findViewById<VideoView>(R.id.videoView)

        //TODO: nawigacja między lekcjami bezpośrednio
        //var btnDalej = findViewById<Button>(R.id.btn_dalej)
        // var btnWstecz = findViewById<Button>(R.id.btn_wstecz)

        // ustawienia do layoutu - paramsy
        val txtParams : LayoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        txtParams.setMargins(10,10,10,10)

        // numer tematu
        var index = lekcjaId

        // pobranie z bazy zawartości dla lekcji:
        val sciezka = getSciezkaFromDB(index)

        // FILMIK
        val videoName = sciezka
        val controller = MediaController(this)
        controller.setMediaPlayer(videoView)
        videoView.setMediaController(controller)
        initPlayer(videoName, videoView)

        // OPIS
        opisLekcjiTextView.layoutParams = txtParams
        val txtFileName = sciezka + "_txt"
        opisLekcjiTextView.text = readTxtFile(txtFileName)

        // OBSŁUGA PRZYCISKU
        // TODO: Obsługa przycisków aby mozliwe było przechodzenie między lekcjami
        /*
        btnDalej.setOnClickListener {
            // btn dalej listener : następna jest lekcja - załaduj ją, jeśli nie - wróc do poprzedniej aktywności

            // sprawdź czy następny element z listy to jest lekcja

            if (index == odebranaTresc1.size -1) {
                btnDalej.isEnabled = false
                // jeśli ostatni temat - załaduj 1 temat następnej lekcji
                    // jesli jest następny temat w liście - załaduj zawartość następnego tematy

            }
            else { // jeśli następny element jest dostępny - załaduj następną zawartość
                index = index + 1

                if (index > 0){
                    btnWstecz.isEnabled = true
                }
                if (index ==  odebranaTresc1.size -1){
                    btnDalej.isEnabled = false
                }

                sv_zawartosc.fullScroll(ScrollView.FOCUS_UP)
                tytulLekcjiTextView.text = odebranaTresc1[index]
                initPlayer(odebranaTresc2[index].toString(), videoView)
                var txtFileName = odebranaTresc3[index].toString() + "_txt"
                opisLekcjiTextView.text = readTxtFile(txtFileName)


            }

            // jeśli następnej lekcji nie ma - załaduj zawartosc 1 pytania testowego

        }

        btnWstecz.setOnClickListener {
            // btn dalej listener : jeśli poprzedni element jest dostępny - załaduj poprzednią zawartość
            if (index == 0) {
                btnWstecz.isEnabled = false

            }
            else { // jeśli następny element jest dostępny - załaduj następną zawartość
                index = index - 1
                if ( index < odebranaTresc1.size) {
                    btnDalej.isEnabled = true
                }
                if (index == 0){
                    btnWstecz.isEnabled = false
                }
                sv_zawartosc.fullScroll(ScrollView.FOCUS_UP)
                tytulLekcjiTextView.text = odebranaTresc1[index]
                initPlayer(odebranaTresc2[index].toString(), videoView)
                var txtFileName = odebranaTresc3[index].toString() + "_txt"
                opisLekcjiTextView.text = readTxtFile(txtFileName)

            }



            // jeśli następnej lekcji nie ma - załaduj zawartosc 1 pytania testowego

        }

*/
    }

    // DO OZNACZEŃ
    private fun oznaczeniePierwszaLekcja() {
        // sprawdzić w bazie czy już było
        val dbAccess : DatabaseAccess = DatabaseAccess.getInstance(applicationContext)
        dbAccess.openDB()
        // najpierw sprawdź czy to co klikniete to było test czy lekcja
        val czyZal = dbAccess.checkOsiag1().toInt()
        if (czyZal==0) //nie zaliczone
        {
            dbAccess.editOsiag()
            // TODO: POWIADOMIENIE
//            createNotificationChannel()
//
//            val sharedPref = this?.getPreferences(Context.MODE_PRIVATE) ?: return
//            val p1 = sharedPref.getBoolean("sync1", true)
//            //val p2 = sharedPref.getBoolean("sync2", true)
//
//            if (p1) {
//                // uruchom powiadomienia
//                val intentNotify : Intent = Intent(this, ReminderBroadcastReceiver::class.java)
//                val pendingIntent : PendingIntent = PendingIntent.getBroadcast(this, 0, intentNotify, 0)
//                val alarmManager : AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
//                var time = System.currentTimeMillis()
//                alarmManager.set(AlarmManager.RTC_WAKEUP, time * 10000, pendingIntent)
//            }

        }
        dbAccess.closeDB()
    }
//    @SuppressLint("ServiceCast")
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = "PJMappReminderChannel"
//            val desc = "PJMapp channel"
//            val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
//            val channel: NotificationChannel = NotificationChannel("notifyPJMapp", name, importance)
//            channel.description = desc
//            val notificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//
//        }
//    }
    // ZWRÓCENIE URI DO FILMU NA PODSTAWIE NAZWY
    private fun getUri(name : String) : Uri {
        // sprawdzenie poprawności
        if (URLUtil.isValidUrl(name)) {
            return Uri.parse(name)
        }
        else {
            return Uri.parse("android.resource://" + getPackageName() + "/raw/" + name)
        }
    }
    // INITPLAYER -> ODWOŁUJE SIĘ DO GETURI I SETVIDEOURI ŻEBY USTAWIĆ URI KTÓRE BĘDZIE
    // WYŚWIETLANIE W VIDEOVIEW I START ŻEBY ODPALIĆ FILM
    private fun initPlayer(videoName: String, videoView: VideoView) {
        val videoUri : Uri = getUri(videoName)
        videoView.setVideoURI(videoUri)
        videoView.start()
    }
    // ZATRZYMAJ FILMIK
    private fun releasePlayer(videoView: VideoView) {
        videoView.stopPlayback()
    }

    protected fun onStop(videoView: VideoView) {
        super.onStop()
        releasePlayer(videoView)
    }

    protected  fun onStart(videoName : String, videoView: VideoView) {
        super.onStart()
        if (videoName != null) {
            initPlayer(videoName, videoView)
        }
    }

    protected fun onPause(videoView: VideoView) {
        super.onPause()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            videoView.pause()
        }
    }

    // WCZYTAJ PLIK TXT
    private fun readTxtFile (fileName : String) : String {
        val resId : Int = resources.getIdentifier(fileName, "raw", packageName)
        if (resId==null) {
            return "Id is null"
        }
        val inputStream = resources.openRawResource(resId)

        if (inputStream != null) {
            val tresc = inputStream.bufferedReader().use { it.readText() }
            return tresc
        }
        else {
            return "File not exist"
        }
    }

    // POBRANIE ZAWARTOŚCI LEKCJI Z BAZY DANYCH
    fun getSciezkaFromDB(id : Int) : String {
        val dbAccess : DatabaseAccess = DatabaseAccess.getInstance(applicationContext)
        dbAccess.openDB()
        val sciezka : String = dbAccess.getVideoPath(id)
        dbAccess.closeDB()
        return sciezka
    }
}