package com.example.pjmapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.webkit.URLUtil
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlin.collections.ArrayList


// klasa pomocnicza
data class Result(val polecenie : String, val sciezkaVideo : String, val odpowiedziTresc: String, val poprawnaOdpowiedz : String)


class QuizTaskActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("WrongViewCast")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_zadanie2)

        lateinit var odpPoprawna : String
        var znakZwrocony : Int = -1; // <----- na początku -1 bo łatwo sprawdzić czy coś faktycznie zostało zwrócone

        // odebranie dodatkowych informacji z putExtra:
        val quizId : Int = intent.getIntExtra("Id", 1)
        val quizNazwa: String? = intent.getStringExtra("Name")
        val quizWynik : Int = intent.getIntExtra("Wynik", 0)
        Log.i("wynik poprzedni ", "" + quizWynik)

        supportActionBar?.setTitle("Quiz")

        // ustawienia do layoutu
        val quizListaIdZadan : ArrayList<String?> = intent.getStringArrayListExtra("listaIdZadan") as ArrayList<String?>
        val quizListaTypowZadan : ArrayList<String?> = intent.getStringArrayListExtra("listaTypyZadan") as ArrayList<String?>
        val svzawartosc = findViewById<ScrollView>(R.id.scrl_view_zad)
        val tytulZadaniaTextView = findViewById<TextView>(R.id.txtNumerZadania)
        val polecenieTextView = findViewById<TextView>(R.id.txtPolecenie)
        val videoView = findViewById<VideoView>(R.id.videoView_p2)
        val btnDalej = findViewById<Button>(R.id.button_dalej)
        val cameraBtn = findViewById<Button>(R.id.cameraBtn)
        val btn1A = findViewById<RadioButton>(R.id.radioButton1_A)
        val btn1B = findViewById<RadioButton>(R.id.radioButton1_B)
        val btn1C = findViewById<RadioButton>(R.id.radioButton1_C)
        val btn1D = findViewById<RadioButton>(R.id.radioButton1_D)
        val rd1group = findViewById<RadioGroup>(R.id.radioButton_p1)
        val btn2A = findViewById<RadioButton>(R.id.radioButton2_A)
        val btn2B = findViewById<RadioButton>(R.id.radioButton2_B)
        val btn2C = findViewById<RadioButton>(R.id.radioButton2_C)
        val btn2D = findViewById<RadioButton>(R.id.radioButton2_D)
        val rd2group = findViewById<RadioGroup>(R.id.radioButton_p2)
        val paramRDGroup1 = rd1group.layoutParams
        val paramRDGroup2 = rd2group.layoutParams
        val paramVView = videoView.layoutParams
        val paramImBtn = cameraBtn.layoutParams

        // ustawienia do layoutu cd
        val txtParams : LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        txtParams.setMargins(10,10,10,10)
        tytulZadaniaTextView.layoutParams = txtParams
        tytulZadaniaTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30F)
        polecenieTextView.layoutParams = txtParams
        polecenieTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)

        // POBRANIE ZAWARTOŚCI QUIZU:
        //TODO: do poprawy te layouty aby lepsze

        // wyszukaj w opowiedniej tabeli po id i pobierz elementy
        @SuppressLint("SetTextI18n")
        fun ustawZawartoscLayoutu(idx: Int) {
            tytulZadaniaTextView.text = "Zadanie " + (idx + 1).toString()
            // id zadania
            var idZadaniaDB = quizListaIdZadan[idx]!!.toInt()
            // sprawdź jaki typ
            var typZadania = quizListaTypowZadan[idx]!!.toInt()
            // w zależności od typu zadania:
            if (typZadania == 1) {
                // pobierz zawartość zadania z bazy : polecenie, ściezki do zdjęć, odp poprawną
                var (polecenie, odpowiedziABCD, odpowiedzPoprawna) = getZadaniaTypu1FromDB(idZadaniaDB)
                polecenieTextView.text = polecenie.toString()
                rd1group.clearCheck()
                // widoczne elementy: radiobtngrup, reszta niewidoczna
                rd1group.visibility = View.VISIBLE
                rd2group.visibility = View.INVISIBLE
                videoView.visibility = View.INVISIBLE
                cameraBtn.visibility = View.INVISIBLE
                paramRDGroup1.height = RadioGroup.LayoutParams.WRAP_CONTENT
                paramRDGroup2.height = 0
                paramImBtn.height = 0
                paramVView.height = 0
                // odpowiedzi do rdbtn
                var odpowiedziRozdzielone : List<String> = (odpowiedziABCD.dropLast(1)).split(";")
                var odpAlokacja = odpowiedziRozdzielone[0]
                var odpAid = resources.getIdentifier(odpAlokacja, "drawable", this.packageName)
                var odpBlokacja = odpowiedziRozdzielone[1]
                var odpBid = resources.getIdentifier(odpBlokacja, "drawable", this.packageName)
                var odpClokacja = odpowiedziRozdzielone[2]
                var odpCid = resources.getIdentifier(odpClokacja, "drawable", this.packageName)
                var odpDlokacja = odpowiedziRozdzielone[3]
                var odpDid = resources.getIdentifier(odpDlokacja, "drawable", this.packageName)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    btn1A.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, odpAid, 0)
                    btn1B.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, odpBid, 0)
                    btn1C.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, odpCid, 0)
                    btn1D.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, odpDid, 0)
                }
                else {
                    Toast.makeText(this, "Wymaga co najmniej wersji JELLY_BEAN_MR1", Toast.LENGTH_SHORT).show()
                }
                odpPoprawna = odpowiedzPoprawna
            }
            else if( typZadania == 2) {
                // pobierz zawartość zadania z bazy: polecenie, filmik, odpowiedzi abcd i poprawną
                var (polecenie, sciezkaVideo, odpowiedziTresc, odpowiedzPoprawna) = getZadaniaTypu2FromDB(idZadaniaDB)
                polecenieTextView.text = polecenie
                rd2group.clearCheck()
                // widoczne elementy: radiobtngrup, videowiev reszta niewidoczna
                rd1group.visibility = View.INVISIBLE
                rd2group.visibility = View.VISIBLE
                videoView.visibility = View.VISIBLE
                cameraBtn.visibility = View.INVISIBLE
                paramRDGroup1.height = 0
                paramRDGroup2.height = RadioGroup.LayoutParams.WRAP_CONTENT
                paramImBtn.height = 0
                paramVView.height = 700
                var videoName = sciezkaVideo
                var controller = MediaController(this)
                controller.setMediaPlayer(videoView)
                videoView.setMediaController(controller)
                initPlayer(videoName, videoView)
                var odpowiedziRozdzielone : List<String> = (odpowiedziTresc.dropLast(1)).split(";")
                var odpA = odpowiedziRozdzielone[0]
                var odpB = odpowiedziRozdzielone[1]
                var odpC = odpowiedziRozdzielone[2]
                var odpD = odpowiedziRozdzielone[3]

                btn2A.text = "A. " + odpA
                btn2B.text = "B. " + odpB
                btn2C.text = "C. " + odpC
                btn2D.text = "D. " + odpD

                odpPoprawna = odpowiedzPoprawna
            }
            else if (typZadania == 3){
                // pobierz zawartość zadania z bazy: polecenie, znak porównawczy
                var (polecenie, znakPorownawczy) = getZadaniaTypu3FromDB(idZadaniaDB)
                polecenieTextView.text = polecenie.toString()

                rd1group.visibility = View.INVISIBLE
                rd2group.visibility = View.VISIBLE
                videoView.visibility = View.VISIBLE
                cameraBtn.visibility = View.INVISIBLE

                paramRDGroup1.height = 0
                paramRDGroup2.height = RadioGroup.LayoutParams.WRAP_CONTENT
                paramImBtn.height = 0
                paramVView.height = 500

                // widoczne elementy: imagebtn, pomin, reszta niewidoczna
                rd1group.visibility = View.INVISIBLE
                rd2group.visibility = View.INVISIBLE
                videoView.visibility = View.INVISIBLE
                cameraBtn.visibility = View.VISIBLE

                paramRDGroup1.height = 0
                paramRDGroup2.height = 0
             //   paramBtnPomin.height = btnDalej.height
                paramImBtn.height = 500
                paramVView.height = 0

                odpPoprawna = znakPorownawczy
            }
        }

        fun sprawadzOdpowiedziTyp(idx: Int, odpPopr: String, znakZwrocony: Int) : Int {
            // sprawdź jaki typ
            var typZadania = quizListaTypowZadan[idx]!!.toInt()
            if ( typZadania == 3) {
                // zadanie 3 typu
                var sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                var sign = sharedPreferences.getInt("Znak", -5)

                // Log.i("ZNAK WYKRYTY:", "  $sign")
                // sprawdzanie wyniku
                return if (odpPopr.toInt() == sign) {
                    1
                } else {
                    0
                }
            }
            else if(typZadania == 2) {
                // zadanie typu 2
                if (btn2A.isChecked && odpPopr.equals("A")) {
                    // jeśli zaznaczono odp A i miała zostać zaznaczona A
                    return 1
                }
                else if (btn2B.isChecked && odpPopr.equals("B")) {
                    return 1
                }
                else if (btn2C.isChecked && odpPopr.equals("C")) {
                    return 1
                }
                else if (btn2D.isChecked && odpPopr.equals("D")) {
                    return 1
                }
                else {
                    // na pewno nie zaznaczono prawidłowej odpowiedzi
                    return 0
                }
            }
            else {
                // zadanie typu 1
                if (btn1A.isChecked && odpPopr.equals("A")) {
                    // jeśli zaznaczono odp A i miała zostać zaznaczona A
                    return 1
                }
                else if (btn1B.isChecked && odpPopr.equals("B")) {
                    return 1
                }
                else if (btn1C.isChecked && odpPopr.equals("C")) {
                    return 1
                }
                else if (btn1D.isChecked && odpPopr.equals("D")) {
                    return 1
                }
                else {
                    // na pewno nie zaznaczono prawidłowej odpowiedzi
                    return 0
                }
            }
            // i - iterator wskazujący zadanie
        }

        fun porownajWynik(pWynik: Int, nWynik: Int) : Boolean
        {
            return pWynik <= nWynik //true - nowy wynik nadpisze stary
        }

        // TUTAJ JUŻ WŁAŚCIWE PRZESUWANIE SIE PO ZADANIACH:
        var index = 0 // numer zadania: po uruchomieniu 1 zadanie z listy idzie
        var wynik = 0 // dodawane kolejne wartosci gdy sprawdzaone zadania są poprawne, zapisywany na koniec

        ustawZawartoscLayoutu(index)

        // LISTENERY OD PRZYCISKÓW
        btnDalej.setOnClickListener {
            val dialogBuilder  = AlertDialog.Builder(this)
            var pkt = sprawadzOdpowiedziTyp(index, odpPoprawna, znakZwrocony)
            wynik += pkt

            if (index == quizListaIdZadan.size -1 ) {
                // ostatnie zadanie -> należy podliczyć punkty i zapisać wynik, zamknąć test i wrócić do wyboru lekcji
                if (porownajWynik(quizWynik, wynik)) {
                    zapiszWynikiDoBD(quizId, wynik)
                }

                // Wyświetlanie wyniku użytkownikowi
                var msg = "" + wynik + "/10"
                dialogBuilder.setMessage(msg).setPositiveButton("Dalej", DialogInterface.OnClickListener{
                        dialog, id -> finish()
                })

                if (wynik == 10) {
                    // TODO: Sprawdź czy już uzyskano z quizu 10, jeśli tak ustaw w bazie czy_zal 1 i daj powiadomienie jeśli zezwolone
                    if (!sprawdzWynikiQuizowCzy10()) {
                        // ustaw w bazie osiagniecie na zaliczone
                        zaliczOsiag()
                        powiadomienie3()

                    }
                }
                var alert = dialogBuilder.create()
                alert.setTitle("Uzyskany wynik: ")
                alert.show()
            }
            else {
                // wczytaj kolejne zadanie, odśwież widok
                index += 1
                ustawZawartoscLayoutu(index)
                svzawartosc.fullScroll(ScrollView.FOCUS_UP)
            }
        }

        cameraBtn.setOnClickListener {
            // uruchom aktywność do przechwytywania łapki i pokazywania symbolu
            val intentCamera = Intent(this, ClasifyHandActivity::class.java)
            startActivity( intentCamera)
        }
    }

    // FUNKCJE
    // Funkcja do pobierania zawartosci z tabeli dla poziomu 1
    fun getZadaniaTypu1FromDB(id: Int) : Triple<String, String, String>
    {
        var dbAccess : DatabaseAccess = DatabaseAccess.getInstance(applicationContext)
        dbAccess.openDB()
        var zadPolecenie : String = dbAccess.getTaskForType1(id)
        var zadSciezkiDoOdpowiedzi : String = dbAccess.getPathToAnswersForType1(id)
        var zadPoprawnaOdp : String = dbAccess.getRightAnswerForType1(id)
        dbAccess.closeDB()
        return Triple(zadPolecenie, zadSciezkiDoOdpowiedzi, zadPoprawnaOdp)
    }

    // Funkcja do pobierania zawartosci z tabeli dla poziomu 2
    fun getZadaniaTypu2FromDB(id: Int) : Result
    {
        var dbAccess : DatabaseAccess = DatabaseAccess.getInstance(applicationContext)
        dbAccess.openDB()
        var zadPolecenie : String = dbAccess.getTaskForType2(id)
        var zadSciezka: String = dbAccess.getVideoPathForType2(id)
        var zadOdpowiedzi : String = dbAccess.getAnswersForType2(id)
        var zadPoprawnaOdp : String = dbAccess.getRightAnswerForType2(id)
        dbAccess.closeDB()
        return Result(zadPolecenie, zadSciezka, zadOdpowiedzi, zadPoprawnaOdp)
    }

    // Funkcja do pobierania zawartosci z tabeli dla poziomu 3
    fun getZadaniaTypu3FromDB(id: Int) : Pair<String, String>
    {
        var dbAccess : DatabaseAccess = DatabaseAccess.getInstance(applicationContext)
        dbAccess.openDB()
        var zadPolecenie : String = dbAccess.getTaskForType3(id)
        var zadZnakPorownawczy : String = dbAccess.getRightSignForType3(id)
        dbAccess.closeDB()
        return Pair(zadPolecenie, zadZnakPorownawczy)
    }

    // Wideo
    // ZWRÓCENIE URI DO FILMU NA PODSTAWIE NAZWY
    private fun getUri(name : String) : Uri {
        // sprawdzenie poprawności Uri
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
        var videoUri : Uri = getUri(videoName)
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

    fun zapiszWynikiDoBD(id: Int, wynik: Int) {
        val dbAccess : DatabaseAccess = DatabaseAccess.getInstance(applicationContext)
        dbAccess.openDB()
        dbAccess.editQuizResult(id, wynik)
        dbAccess.closeDB()
    }

    fun sprawdzWynikiQuizowCzy10() : Boolean{
        //pobierz wyniki quizów
        val dbAccess : DatabaseAccess = DatabaseAccess.getInstance(applicationContext)
        dbAccess.openDB()
        val quizy : String = dbAccess.getQuizResults()
        var rozdzielonequizy : List<String> = (quizy.dropLast(1)).split(";")
        dbAccess.closeDB()
        // sprawdz czy któryś 10 - jeśli tak true, jeśli nie false
        for (element in rozdzielonequizy) {
            if((element).toInt() == 10) {
                return true
            }
        }
        return false
    }

    fun zaliczOsiag() {
        val dbAccess : DatabaseAccess = DatabaseAccess.getInstance(applicationContext)
        dbAccess.openDB()
        dbAccess.editOsiag3()
        dbAccess.closeDB()
    }

    fun powiadomienie3() {

        //TODO: Powiadomienie
    }
}