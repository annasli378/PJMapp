package com.example.pjmapp

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

//data class do zwracania wielu rezultatów
data class ProgressResult(
    val Testy : List<String>,
    val TestyRes : List<String>,
    val Quizy : List<String>,
    val QuizyRes : List<String>,
    val Osiag : List<String>,
    val OsiagRes : List<String>
)

class ProgressActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postepy)
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Postępy")

        // do layoutu ustawienia:
        val btnTest1 = findViewById<ImageButton>(R.id.imageButtonTest1)
        val btnTest2 = findViewById<ImageButton>(R.id.imageButtonTest2)
        val btnTest3 = findViewById<ImageButton>(R.id.imageButtonTest3)
        val btnQuiz1 = findViewById<ImageButton>(R.id.imageButtonQuiz1)
        val btnQuiz2 = findViewById<ImageButton>(R.id.imageButtonQuiz2)
        val btnQuiz3 = findViewById<ImageButton>(R.id.imageButtonQuiz3)
        val btnOsiag1 = findViewById<ImageButton>(R.id.imageButton_pierwszaLekcja)
        val btnOsiag2 = findViewById<ImageButton>(R.id.imageButton_1TestZdany)
        val btnOsiag3 = findViewById<ImageButton>(R.id.imageButton_quizNa100)

        // pobierz ile testów i quizow jest dostępnych
        val (testy, testyWyniki, quizy, quizyWyniki,osiag, osiagZal) = getContentFromDB()

        // dla każdej ikony: Ustaw nazwę i przycisku i kolor
        sprawdzTesty(btnTest1, testyWyniki[0].toInt(), testy[0])
        sprawdzTesty(btnTest2, testyWyniki[1].toInt(), testy[1])
        sprawdzTesty(btnTest3, testyWyniki[2].toInt(), testy[2])
        sprawdzQuziy(btnQuiz1, quizyWyniki[0].toInt(), quizy[0])
        sprawdzQuziy(btnQuiz2, quizyWyniki[1].toInt(), quizy[1])
        sprawdzQuziy(btnQuiz3, quizyWyniki[2].toInt(), quizy[2])

        val res: ArrayList<Int> = ArrayList()
        res.add(quizyWyniki[0].toInt())
        res.add(quizyWyniki[1].toInt())
        res.add(quizyWyniki[2].toInt())

        val pathToBtnOsiag1 = sprawdzOsiagniecia( osiagZal[0].toInt(), "eye")
        val pathToBtnOsiag2 = sprawdzTestMax(testyWyniki[0].toInt(), "checkbox1")
        val pathToBtnOsiag3 = sprawdzQuizMax(getMaxQuizResult(res), "star2" )

        btnOsiag1.setImageResource(resources.getIdentifier(pathToBtnOsiag1, "drawable", this.packageName))
        btnOsiag2.setImageResource(resources.getIdentifier(pathToBtnOsiag2, "drawable", this.packageName))
        btnOsiag3.setImageResource(resources.getIdentifier(pathToBtnOsiag3, "drawable", this.packageName))

        ustawOsiagniecia(btnOsiag1,  osiag[0])
        ustawOsiagniecia(btnOsiag2,  "Pierwszy test zaliczony na 100%")
        ustawOsiagniecia(btnOsiag3,  "Quiz zaliczony na 100%")
    }

    private fun getMaxQuizResult(results: ArrayList<Int>): Int {
        return results.maxOrNull() ?: 0
    }

    private fun getContentFromDB() : ProgressResult
    {
        val dbAccess : DatabaseAccess = DatabaseAccess.getInstance(applicationContext)
        dbAccess.openDB()
        // pobierz listę zadań i wyników dla testów
        val testyLista : String = dbAccess.getAllTests()
        val testyListaRozdzielone : List<String> = (testyLista.dropLast(1)).split(";")
        val testyWynikiLista : String = dbAccess.getTestResults()
        val testyWynikiListaRozdzielone : List<String> = (testyWynikiLista.dropLast(1)).split(";")
        // pobierz listę zadań i wyników dla quizów
        val quizyLista : String = dbAccess.getAllQuiz()
        val quizyListaRozdzielone : List<String> = (quizyLista.dropLast(1)).split(";")
        val quizyWynikiLista : String = dbAccess.getQuizResults()
        val quizyWynikiListaRozdzielone : List<String> = (quizyWynikiLista.dropLast(1)).split(";")
        // pobierz listę zadań i wyników dla postępów
        val postepyLista : String = dbAccess.getAllOsi()
        val postepyListaRozdzielone : List<String> = (postepyLista.dropLast(1)).split(";")
        val postepyWynikiLista : String = dbAccess.getAllOsiRes()
        val postepyWynikiListaRozdzielone : List<String> = (postepyWynikiLista.dropLast(1)).split(";")
        dbAccess.closeDB()
        return ProgressResult(testyListaRozdzielone, testyWynikiListaRozdzielone,
            quizyListaRozdzielone, quizyWynikiListaRozdzielone,
            postepyListaRozdzielone, postepyWynikiListaRozdzielone)
    }

    fun sprawdzTesty( btn : ImageButton, wynik : Int, nazwa : String) {
        if(wynik >= 2) {
            btn.setImageResource(R.drawable.flag_y)
        }
        else {
            btn.setImageResource(R.drawable.flag)
        }
        btn.setOnClickListener {
            Toast.makeText(this, "" + nazwa, Toast.LENGTH_SHORT).show()
        }
    }

    fun sprawdzQuziy( btn : ImageButton, wynik : Int, nazwa : String) {
        if(wynik == 0 ) {
            btn.setImageResource(R.drawable.star)
        }
        else {
            btn.setImageResource(R.drawable.star_y)
        }
        btn.setOnClickListener {
            Toast.makeText(this, "Quiz: " + nazwa, Toast.LENGTH_SHORT).show()
        }
    }

    fun sprawdzOsiagniecia( czyZal : Int, nazwa : String): String {
        var path = nazwa
        if(czyZal == 1) {
            path += "_y"
        }
        return path
    }

    fun sprawdzTestMax( wynik: Int, nazwa : String): String {
        var path = nazwa
        if(wynik == 3) {
            path += "_y"
        }
        return path
    }

    fun sprawdzQuizMax( wynik: Int, nazwa : String): String {
        var path = nazwa
        if(wynik == 10) {
            path += "_y"
        }
        return path
    }

    fun ustawOsiagniecia( btn : ImageButton,  nazwa : String) {
        btn.setOnClickListener {
            Toast.makeText(this, "" + nazwa, Toast.LENGTH_SHORT).show()
        }
    }
}


