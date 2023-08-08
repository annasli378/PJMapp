package com.example.pjmapp

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.widget.*
import androidx.annotation.RequiresApi
import android.app.AlertDialog
import android.content.DialogInterface

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // odebranie dodatkowych informacji z putExtra:
        val testId : Int = intent.getIntExtra("Id", 1)
        val testNazwa: String? = intent.getStringExtra("Name")

        if (testNazwa.isNullOrEmpty()) {
            supportActionBar?.setTitle("Test")
        }
        else {
            supportActionBar?.setTitle(testNazwa)
        }

        // do layoutu ustawienia:
        val textNumerZadania = findViewById<TextView>(R.id.txtNumerZadania)
        val textPolecenie = findViewById<TextView>(R.id.txtPolecenie)
        val btnA = findViewById<RadioButton>(R.id.btnOdpA)
        val btnB = findViewById<RadioButton>(R.id.btnOdpB)
        val btnC = findViewById<RadioButton>(R.id.btnOdpC)
        val btnD = findViewById<RadioButton>(R.id.btnOdpD)
        val btnDalej = findViewById<Button>(R.id.btnNext)
        val scr_view = findViewById<ScrollView>(R.id.scrl_view_zad)
        val rd_group = findViewById<RadioGroup>(R.id.btngroupOdpowiedzi)

        // ustawienia cd
        val txtParams : LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        txtParams.setMargins(10,10,10,10)

        textNumerZadania.layoutParams = txtParams
        textNumerZadania.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30F)
        textPolecenie.layoutParams = txtParams
        textPolecenie.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)

        // pobranie zawartości testu z bazy danych id 3 zadań dla danego testu,
        // dla każdego zadania pobranie z bazy polecenia, sciezek do odpowiedzi i odpowiedzi poprawnej
        var (polecenia, odpowiedziABCD, odpowiedziPoprawne) = getZadaniaFromDB(testId)

        // funkcja ustawiająca elementu layoutu na podstawie indeksu
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        fun ustawieniaZadania(index : Int) {
            //btnDalej.isEnabled = false
            rd_group.clearCheck()
            textNumerZadania.text = "Zadanie " + (index + 1).toString()
            textPolecenie.text = polecenia[index].toString()
            var odpowiedziZad = odpowiedziABCD[index].toString()
            var odpowiedziRozdzielone : List<String> = (odpowiedziZad.dropLast(1)).split(";")
            var odpA_lokacja = odpowiedziRozdzielone[0]
            var odpAid = resources.getIdentifier(odpA_lokacja, "drawable", this.packageName)
            var odpB_lokacja = odpowiedziRozdzielone[1]
            var odpBid = resources.getIdentifier(odpB_lokacja, "drawable", this.packageName)
            var odpC_lokacja = odpowiedziRozdzielone[2]
            var odpCid = resources.getIdentifier(odpC_lokacja, "drawable", this.packageName)
            var odpD_lokacja = odpowiedziRozdzielone[3]
            var odpDid = resources.getIdentifier(odpD_lokacja, "drawable", this.packageName)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                btnA.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, odpAid, 0)
                btnB.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, odpBid, 0)
                btnC.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, odpCid, 0)
                btnD.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, odpDid, 0)
            }
            else {
                Toast.makeText(this, "Wymaga co najmniej wersji JELLY_BEAN_MR1", Toast.LENGTH_SHORT).show()
            }
        }

        fun sprawadzOdpowiedzi(i : Int) : Int {
             // i - iterator wskazujący zadanie
            var odpPoprawna = odpowiedziPoprawne[i].toString() // odczytaj poprawną opowiedź z zadania
            if (btnA.isChecked && odpPoprawna.equals("A")) {
                // jeśli zaznaczono odp A i miała zostać zaznaczona A
                return 1
            }
            else if (btnB.isChecked && odpPoprawna.equals("B")) {
                return 1
            }
            else if (btnC.isChecked && odpPoprawna.equals("C")) {
                return 1
            }
            else if (btnD.isChecked && odpPoprawna.equals("D")) {
                return 1
            }
            else {
                // na pewno nie zaznaczono prawidłowej odpowiedzi
                return 0
            }
        }

        var index = 0
        var wynik = 0

        ustawieniaZadania(index)

        btnDalej.setOnClickListener {
            var pkt = sprawadzOdpowiedzi(index)
            wynik += pkt
            var dialogBuilder  = AlertDialog.Builder(this)

            if (index == polecenia.size -1 ) {
                //  zapis wyniku do bazy
                zapiszWynikiDoBD(testId, wynik)
                //  jeśli test zdany - powiadom użytkownika - powiadomienie na tel?
                if (wynik >= 2) {
                    if (!sprawdzWynikiTestow()) {
                        // ustaw w bazie osiagniecie na zaliczone
                        zaliczOsiag()
                        powiadomienie2()

                    }
                    //Toast.makeText(this, "Test zaliczono :)", Toast.LENGTH_LONG)
                    dialogBuilder.setMessage("Gratulację - zaliczony!").setPositiveButton("Dalej", DialogInterface.OnClickListener{
                        dialog, id -> finish()
                    })
                }
                else {
                    //Toast.makeText(this, "Nie udało się zaliczyć testu", Toast.LENGTH_LONG)
                    dialogBuilder.setMessage("Spróbuj ponownie").setPositiveButton("Dalej", DialogInterface.OnClickListener{
                            dialog, id -> finish()
                    })
                }

                val alert = dialogBuilder.create()
                alert.setTitle("Ukończono test: ")
                alert.show()
            }
            else {
                // wczytaj kolejne zadanie, odśwież widok
                index += 1
                ustawieniaZadania(index)
                scr_view.fullScroll(ScrollView.FOCUS_UP)
            }
        }

        // TODO: blokada przycisku dalej jeżeli nic nie jest zaznaczone
    }

    // POBRANIE ID ZADAŃ Z BAZY
    fun getZadaniaFromDB(id: Int) : Triple<List<String>, List<String>, List<String>>
    {
        println(id)
        //zmienne
        var polecenia =  arrayOfNulls<String>(3) // bo 3 zadania na 1 test daje 3 polecenia
        var odpowiedziSciezka  = arrayOfNulls<String>(3) // 3 zadania, 3 ładńcuchy stringów z ściezkami -> należy je potem porozdzielać
        var odpPoprawna = arrayOfNulls<String>(3) // 3 poprawne odp

        var dbAccess : DatabaseAccess = DatabaseAccess.getInstance(applicationContext)
        dbAccess.openDB()
        // pobierz listę zadań dla danego testu
        var idZadanLista : String = dbAccess.getTest(id)
        var idZadanRozdzielone : List<String> = (idZadanLista.dropLast(1)).split(";")
        // znajdz zadania po id i wczytaj polecenia, sciezki do odpowiedzi, odp poprawną
        for (i in 0..2) {
            var zadId = (idZadanRozdzielone[i]).toInt() // konwersja na int:
            var zadPolecenie : String = dbAccess.getTask(zadId)
            var zadSciezkiDoOdpowiedzi : String = dbAccess.getPathToAnswers(zadId)
            var zadPoprawnaOdp : String = dbAccess.getRightAnswer(zadId)
            polecenia[i] = zadPolecenie
            odpowiedziSciezka[i] = zadSciezkiDoOdpowiedzi
            odpPoprawna[i] = zadPoprawnaOdp
        }
        // włóż do list
        var poleceniaList : List<String> = polecenia.toList() as List<String>
        var odpowiedziSciezkaList : List<String> = odpowiedziSciezka.toList() as List<String>
        var odpPoprawnaList : List<String> = odpPoprawna.toList() as List<String>
        dbAccess.closeDB()
        return Triple(poleceniaList, odpowiedziSciezkaList, odpPoprawnaList)
    }

    fun zapiszWynikiDoBD(id: Int, wynik: Int) {
        var zdane = 0;
        if (wynik >= 2) {
            zdane = 1;
        }
        var dbAccess : DatabaseAccess = DatabaseAccess.getInstance(applicationContext)
        dbAccess.openDB()
        var poprzedniWynik = dbAccess.getTestResult(id)
        if (poprzedniWynik.toInt() < wynik) {
            dbAccess.editTestResult(id, wynik, zdane)
        }
        dbAccess.closeDB()
    }

    fun sprawdzWynikiTestow() : Boolean{
        //pobierz wyniki quizów
        val dbAccess : DatabaseAccess = DatabaseAccess.getInstance(applicationContext)
        dbAccess.openDB()
        val quizy : String = dbAccess.getTestIsPasses()
        var rozdzielonequizy : List<String> = (quizy.dropLast(1)).split(";")
        dbAccess.closeDB()
        // sprawdz czy któryś 10 - jeśli tak true, jeśli nie false
        for (element in rozdzielonequizy) {
            if((element).toInt() == 1) {
                return true
            }
        }
        return false
    }

    fun zaliczOsiag() {
        val dbAccess : DatabaseAccess = DatabaseAccess.getInstance(applicationContext)
        dbAccess.openDB()
        dbAccess.editOsiag2()
        dbAccess.closeDB()
    }

    fun powiadomienie2() {

        //TODO: Powiadomienie

    }
}