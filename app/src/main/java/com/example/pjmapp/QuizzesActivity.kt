package com.example.pjmapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class QuizzesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        supportActionBar?.setTitle("Quizy")

        val listView = findViewById<ListView>(R.id.list_view_quiz)
        val listaQuizow = getQuizyFromDB()
        val listaWynikow = getWynikiFromDB()
        val listItems = arrayOfNulls<String>(listaQuizow.size)
        for (i in 0 until listItems.size) {
            val q = listaQuizow[i]
            val w = listaWynikow[i]
            listItems[i] = "$q\t   $w/10"
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1 , listItems)
        listView.adapter = adapter
        val context = this

        listView.setOnItemClickListener { parent, view, position, id ->
            val wybranyQuiz : String = listItems[position]!!
            val wybranyId  :  Int = position!! + 1
            // pobranie zawartości
            val listaIdZadan = getZadaniaIdFromDB(wybranyId)
            val listaTypyZadan = getZadaniaTypyFromDB(wybranyId)
            val listaIdZadanasArray : ArrayList<String> = ArrayList(listaIdZadan)
            val listaTypyZadanasArray : ArrayList<String> = ArrayList(listaTypyZadan)
            // przejście do wybranego quizu
            val quizZadanieIntent = Intent(this, QuizTaskActivity::class.java)
            quizZadanieIntent.putExtra("Id", wybranyId) // id klikniętego elementu listy
            quizZadanieIntent.putExtra("Name", wybranyQuiz) // nazwa wybranego elementu
            quizZadanieIntent.putExtra("Wynik", listaWynikow[wybranyId])
            quizZadanieIntent.putStringArrayListExtra("listaIdZadan", listaIdZadanasArray)
            quizZadanieIntent.putStringArrayListExtra("listaTypyZadan", listaTypyZadanasArray)
            startActivity(quizZadanieIntent)
        }
    }

    // pobranie listy dostępnych quizów:
    fun getQuizyFromDB() : List<String>{
            // Wczytanie nazw quizow:
            val dbAccessTest : DatabaseAccess = DatabaseAccess.getInstance(applicationContext)
            dbAccessTest.openDB()
            // Lista wszystkich quizow rozdzielonych ";" - wczytanie do zmiennej
            val zawartoscQuziy : String = dbAccessTest.getAllQuizes()
            val rozdzieloneQuizy : List<String> = (zawartoscQuziy.dropLast(1)).split(";")
            // Zamknięcie bazy
            dbAccessTest.closeDB()
            return rozdzieloneQuizy
    }

    // pobranie listy id wszystkich zadań z danego quizu
    fun getZadaniaIdFromDB(id: Int) : List<String> {
        val dbAccessTest : DatabaseAccess = DatabaseAccess.getInstance(applicationContext)
        dbAccessTest.openDB()
        // Lista wszystkich quizow rozdzielonych ";" - wczytanie do zmiennej
        val listaIdZadan : String = dbAccessTest.getAllTaskIdForQuiz(id)
        val rozdzieloneIdZadan : List<String> = (listaIdZadan.dropLast(1)).split(";")
        // Zamknięcie bazy
        dbAccessTest.closeDB()
        return rozdzieloneIdZadan
    }

    fun getZadaniaTypyFromDB(id: Int) : List<String> {
        val dbAccessTest : DatabaseAccess = DatabaseAccess.getInstance(applicationContext)
        dbAccessTest.openDB()
        // Lista wszystkich quizow rozdzielonych ";" - wczytanie do zmiennej
        val listaTypyZadan : String = dbAccessTest.getAllTaskTypesForQuiz(id)
        val rozdzieloneTypyZadan : List<String> = (listaTypyZadan.dropLast(1)).split(";")
        // Zamknięcie bazy
        dbAccessTest.closeDB()
        return rozdzieloneTypyZadan
    }

    fun getWynikiFromDB() : List<String>{
        // Wczytanie nazw quizow:
        var dbAccessTest : DatabaseAccess = DatabaseAccess.getInstance(applicationContext)
        dbAccessTest.openDB()
        // Lista wszystkich quizow rozdzielonych ";" - wczytanie do zmiennej
        val zawartoscQuziy : String = dbAccessTest.getQuizResults()
        val rozdzieloneQuizy : List<String> = (zawartoscQuziy.dropLast(1)).split(";")
        // Zamknięcie bazy
        dbAccessTest.closeDB()
        return rozdzieloneQuizy
    }
}

