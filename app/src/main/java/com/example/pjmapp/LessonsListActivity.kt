package com.example.pjmapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class LessonsListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_lekcji)
        supportActionBar?.setTitle("Wybierz lekcję")

        // obsługa spinera do wyboru poziomu: A1, A2, B1, B2 - domyślnie poziom A1, kolejne odblokowywane gdy zaliczone wszystkie testyz poziomu niżej
        var spinnerPoziom = findViewById<Spinner>(R.id.spinner_poziom)
        //val elementy  = arrayOf<String>("A1", "A2", "B1", "B2" )
        val elementy  = arrayOf<String>("A1")
        var adapterSpinner : ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, elementy)
        spinnerPoziom.adapter = adapterSpinner
        spinnerPoziom.setSelection(0)

        // TODO: obsługa poziomów A1, A2, B1, B2
        //wybór poziomu - wybór jakie lekcje zostaną odczytane

       // spinnerPoziom.onItemSelectedListener = AdapterView.OnItemSelectedListener()

        // lista lekcji
        var listView = findViewById<ListView>(R.id.list_view_lekcje)
        val listaLekcji = getLekcjeFromDB()
        val poziom = getPoziomFromDB() // odczytuje ile testów zostało zaliczonych i ile lekcji można odtworzyć
        var dispListSize = 0
        dispListSize = if (poziom == 0) {
            4
        } else if (poziom == 1) {
            8
        } else if (poziom == 2) {
            11
        } else {
            listaLekcji.size
        }

        val listItems = arrayOfNulls<String>(dispListSize)
        // listview:
        for (i in 0 until listItems.size) {
            val lekcja = listaLekcji[i]
            listItems[i] = lekcja.toString()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1 , listItems)
        listView.adapter = adapter
        val context = this
        listView.setOnItemClickListener { parent, view, position, id ->
            val wybranaLekcja : String = listItems[position]!!
            val wybranaId  :  Int = position!! + 1
            // pobranie treści dla klikniętego elementu
            var czyTest = getIsTestFromDB(wybranaId)

            if (czyTest) {
                // intent - wysłanie id elementu, nazwy
                val lekcjaIntent = Intent(this, TestActivity::class.java)
                lekcjaIntent.putExtra("Id", wybranaId) // id klikniętego elementu listy
                lekcjaIntent.putExtra("Name", wybranaLekcja) // nazwa wybranego elementu
                startActivity(lekcjaIntent)
            }
            else {
                // intent - wysłanie id elementu, nazwy
                val lekcjaIntent = Intent(this, LessonActivity::class.java)
                lekcjaIntent.putExtra("Id", wybranaId) // id klikniętego elementu listy
                lekcjaIntent.putExtra("Name", wybranaLekcja) // nazwa wybranego elementu
                startActivity(lekcjaIntent)
            }
        }
    }

    fun getLekcjeFromDB(): List<String> { //MutableList<String>?
        // Wczytanie tematów lekcji i testów do widoku
        val dbAccessTest : DatabaseAccess = DatabaseAccess.getInstance(applicationContext)
        dbAccessTest.openDB()
        // Lista wszystkich tematów rozdzielonych ";" - wczytanie do zmiennej
        val zawartoscLekcjeLista : String = dbAccessTest.getElements()
        val rozdzieloneTematy : List<String> = (zawartoscLekcjeLista.dropLast(1)).split(";")
        // Zamknięcie bazy
        dbAccessTest.closeDB()
        return rozdzieloneTematy //listaLekce
    }

    fun getIsTestFromDB(id : Int) :  Boolean { //Result// Triple<List<String>, List<String>, List<String>>
        // dla danego elementu pobierz id, nazwę i czy test
        var czyTest : Boolean
        var dbAccess : DatabaseAccess = DatabaseAccess.getInstance(applicationContext)
        dbAccess.openDB()
        // najpierw sprawdź czy to co klikniete to było test czy lekcja
        czyTest = dbAccess.isTest(id)
        dbAccess.closeDB()
        // zwróc co trzeba
        return  czyTest // Result(isTest, poleceniaList, odpowiedziSciezkaList, odpPoprawnaList) //Triple(poleceniaList, odpowiedziSciezkaList, odpPoprawnaList)
    }

    fun getPoziomFromDB(): Int {
        var dbAccessTest : DatabaseAccess = DatabaseAccess.getInstance(applicationContext)
        dbAccessTest.openDB()
        // Lista wszystkich tematów rozdzielonych ";" - wczytanie do zmiennej
        var testy_zdane : String = dbAccessTest.getTestIsPasses()
        var rozdzielonetesty : List<String> = (testy_zdane.dropLast(1)).split(";")
        // Zamknięcie bazy
        dbAccessTest.closeDB()
        var poziom = 0
        for (element in rozdzielonetesty) {
            poziom += (element).toInt();
        }
        return poziom
    }
}


