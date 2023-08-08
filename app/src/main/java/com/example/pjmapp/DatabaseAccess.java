package com.example.pjmapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;

// Instancja tej klasy jest uzywana do dostępu do bazy danych:
public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;
    Cursor c = null;

    // konstruktor - prywatny żeby nie tworzyć obiektów tej klasy z zewnątrz
    private DatabaseAccess(Context context) {
        this.openHelper = new com.example.pjmapp.DatabaseOpenHelper(context);
    }

    // zwrócenie instancji klasy
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    // otwarcie bazy
    public void openDB() {
        this.database = openHelper.getWritableDatabase();
    }

    // zamykanie bazy
    public void closeDB() {
        if(database!=null) {
            this.database.close();
        }
    }

    //////////////////////////////////// RESULTS /////////////////////////////////////////////////////
    public String getTestResult(int id){
      c = database.rawQuery("select wynik from Testy_wyniki where test_id = '" + id + "'", new String[]{});
      StringBuffer buffer = new StringBuffer();
      while (c.moveToNext()) {
          String topicId = c.getString(0);
          buffer.append(""+ topicId);
      }
      return buffer.toString();

  }

    public String getTestIsPasses(){
        c = database.rawQuery("select czy_zdane from Testy_wyniki", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            String t = c.getString(0);
            buffer.append(""+ t + ";");
        }
        return buffer.toString();

    }
    public void editTestResult(int id, int res, int zdane){
        String sql = "UPDATE Testy_wyniki SET wynik = " + res
                + ", czy_zdane = " + zdane
                + " WHERE test_id = " + id;

        //System.out.println(sql);
        database.execSQL(sql);
    }

    public String getQuizResult(int id){
        c = database.rawQuery("select wynik from Quizy_wyniki where quiz_id = '" + id + "'", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer.append(""+ topicId);
        }
        return buffer.toString();
    }
    public void editQuizResult(int id, int res){
        String sql = "UPDATE Quizy_wyniki SET wynik = " + res
                + " WHERE quiz_id = " + id;
        //System.out.println(sql);
        database.execSQL(sql);
    }

    public void editOsiag(){
        String sql = "UPDATE Osiagniecia SET czy_zal = 1 WHERE osiag_id = 1";
        database.execSQL(sql);
    }

    public void editOsiag2(){
        String sql = "UPDATE Osiagniecia SET czy_zal = 1 WHERE osiag_id = 2";
        database.execSQL(sql);
    }

    public void editOsiag3(){
        String sql = "UPDATE Osiagniecia SET czy_zal = 1 WHERE osiag_id = 3";
        database.execSQL(sql);
    }

    //////////////////////////////////// LASSONS /////////////////////////////////////////////////////
    // ZAPYTANIA DO BAZY ZAWIERAJĄCEJ MATERIAŁY:
    // zapytanie o wszystkie lekcje i testy jakie są dostępne - kolejność
    public String getElements(){
        c = database.rawQuery("select elem_nazwa from Kolejnosc", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            String Name = c.getString(0);
            buffer.append(""+ Name + ";");
        }
        return buffer.toString();
    }

    // zapytanie zwracające czy dany element jest testem
    public Boolean isTest(int id){
        boolean result = false;
        c = database.rawQuery("select czy_test from Kolejnosc  where elem_id = '" + id + "'", new String[]{});
        while (c.moveToNext()) {
            int czyTest = c.getInt(0);
            if (czyTest == 1) {
                result = true;
            }
        }
        return result;
    }

    // dajemy id tematu i zwraca wszystkie sciezki do tematow do filmow/omówień
    public String getVideoPath(int id){
        c = database.rawQuery("select sciezka from Lekcje where temat_id = '" + id + "'", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer.append(""+ topicId);
        }
        return buffer.toString();
    }

    //////////////////////////////////// TESTS /////////////////////////////////////////////////////
    // OBSŁUGA TESTÓW
    // znajdź w Testy test o kliknietym id, zwróc jego id zadań -> Zadania jako ciag tekstowy, nalezy rozbić i przekonwertować na int
    public String getTest(int id)
    {
        c = database.rawQuery("select zad1_id from Testy where test_id = '" + id + "'", new String[]{});
        StringBuffer buffer1 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer1.append(""+ topicId + ";");
        }
        c = database.rawQuery("select zad2_id from Testy where test_id = '" + id + "'", new String[]{});
        StringBuffer buffer2 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer2.append(""+ topicId + ";");
        }
        c = database.rawQuery("select zad3_id from Testy where test_id = '" + id + "'", new String[]{});
        StringBuffer buffer3 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer3.append(""+ topicId + ";");
        }
        return buffer1.toString() + buffer2.toString() + buffer3.toString();
    }

    // znajdź w Testy_zadania zadanie o danym id, wczytaj jego polecenie, scieżki do odp A-D i poprawną odpowiedź
    public String getTask(int id)
    {
        c = database.rawQuery("select zad_polecenie from Testy_zadania where zad_id = '" + id + "'", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer.append(""+ topicId);
        }
        return buffer.toString();
    }

    public String getPathToAnswers(int id)
    {
        c = database.rawQuery("select odpA_sciezka from Testy_zadania where zad_id = '" + id + "'", new String[]{});
        StringBuffer buffer1 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer1.append(""+ topicId + ";");
        }
        c = database.rawQuery("select odpB_sciezka from Testy_zadania where zad_id = '" + id + "'", new String[]{});
        StringBuffer buffer2 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer2.append(""+ topicId + ";");
        }
        c = database.rawQuery("select odpC_sciezka from Testy_zadania where zad_id = '" + id + "'", new String[]{});
        StringBuffer buffer3 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer3.append(""+ topicId + ";");
        }
        c = database.rawQuery("select odpD_sciezka from Testy_zadania where zad_id = '" + id + "'", new String[]{});
        StringBuffer buffer4 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer4.append(""+ topicId + ";");
        }

        return buffer1.toString() + buffer2.toString() + buffer3.toString() + buffer4.toString();
    }

    public String getRightAnswer(int id)
    {
        c = database.rawQuery("select odp_poprawna from Testy_zadania where zad_id = '" + id + "'", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer.append(""+ topicId);
        }
        return buffer.toString();
    }

    //////////////////////////////////// QUIZ /////////////////////////////////////////////////////
    // OBSŁUGA QUIZÓW
    // pobierz wszystkie dostępne quizy
    public String getAllQuizes(){
        c = database.rawQuery("select quiz_nazwa from Quizy", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            String Name = c.getString(0);
            buffer.append(""+ Name + ";");
        }
        return buffer.toString();
    }

    // pobierz wszystkie id zadań dla danego quizu
    public String getAllTaskIdForQuiz(int id)
    {
        c = database.rawQuery("select zad1_id from Quizy where quiz_id = '" + id + "'", new String[]{});
        StringBuffer buffer1 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer1.append(""+ topicId + ";");
        }
        c = database.rawQuery("select zad2_id from Quizy where quiz_id = '" + id + "'", new String[]{});
        StringBuffer buffer2 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer2.append(""+ topicId + ";");
        }
        c = database.rawQuery("select zad3_id from Quizy where quiz_id = '" + id + "'", new String[]{});
        StringBuffer buffer3 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer3.append(""+ topicId + ";");
        }
        c = database.rawQuery("select zad4_id from Quizy where quiz_id = '" + id + "'", new String[]{});
        StringBuffer buffer4 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer4.append(""+ topicId + ";");
        }
        c = database.rawQuery("select zad5_id from Quizy where quiz_id = '" + id + "'", new String[]{});
        StringBuffer buffer5 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer5.append(""+ topicId + ";");
        }
        c = database.rawQuery("select zad6_id from Quizy where quiz_id = '" + id + "'", new String[]{});
        StringBuffer buffer6 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer6.append(""+ topicId + ";");
        }
        c = database.rawQuery("select zad7_id from Quizy where quiz_id = '" + id + "'", new String[]{});
        StringBuffer buffer7 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer7.append(""+ topicId + ";");
        }
        c = database.rawQuery("select zad8_id from Quizy where quiz_id = '" + id + "'", new String[]{});
        StringBuffer buffer8 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer8.append(""+ topicId + ";");
        }
        c = database.rawQuery("select zad9_id from Quizy where quiz_id = '" + id + "'", new String[]{});
        StringBuffer buffer9 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer9.append(""+ topicId + ";");
        }
        c = database.rawQuery("select zad10_id from Quizy where quiz_id = '" + id + "'", new String[]{});
        StringBuffer buffer10 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer10.append(""+ topicId + ";");
        }

        return buffer1.toString() + buffer2.toString() + buffer3.toString() + buffer4.toString() +
                buffer5.toString() + buffer6.toString() + buffer7.toString() + buffer8.toString() +
                buffer9.toString() + buffer10.toString();
    }

    // pobierz wszystkie typy zadań dla danego quizu
    public String getAllTaskTypesForQuiz(int id)
    {
        c = database.rawQuery("select zad1_typ from Quizy where quiz_id = '" + id + "'", new String[]{});
        StringBuffer buffer1 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer1.append(""+ topicId + ";");
        }
        c = database.rawQuery("select zad2_typ from Quizy where quiz_id = '" + id + "'", new String[]{});
        StringBuffer buffer2 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer2.append(""+ topicId + ";");
        }
        c = database.rawQuery("select zad3_typ from Quizy where quiz_id = '" + id + "'", new String[]{});
        StringBuffer buffer3 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer3.append(""+ topicId + ";");
        }
        c = database.rawQuery("select zad4_typ from Quizy where quiz_id = '" + id + "'", new String[]{});
        StringBuffer buffer4 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer4.append(""+ topicId + ";");
        }
        c = database.rawQuery("select zad5_typ from Quizy where quiz_id = '" + id + "'", new String[]{});
        StringBuffer buffer5 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer5.append(""+ topicId + ";");
        }
        c = database.rawQuery("select zad6_typ from Quizy where quiz_id = '" + id + "'", new String[]{});
        StringBuffer buffer6 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer6.append(""+ topicId + ";");
        }
        c = database.rawQuery("select zad7_typ from Quizy where quiz_id = '" + id + "'", new String[]{});
        StringBuffer buffer7 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer7.append(""+ topicId + ";");
        }
        c = database.rawQuery("select zad8_typ from Quizy where quiz_id = '" + id + "'", new String[]{});
        StringBuffer buffer8 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer8.append(""+ topicId + ";");
        }
        c = database.rawQuery("select zad9_typ from Quizy where quiz_id = '" + id + "'", new String[]{});
        StringBuffer buffer9 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer9.append(""+ topicId + ";");
        }
        c = database.rawQuery("select zad10_typ from Quizy where quiz_id = '" + id + "'", new String[]{});
        StringBuffer buffer10 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer10.append(""+ topicId + ";");
        }

        return buffer1.toString() + buffer2.toString() + buffer3.toString() + buffer4.toString() +
                buffer5.toString() + buffer6.toString() + buffer7.toString() + buffer8.toString() +
                buffer9.toString() + buffer10.toString();
    }


    // ZADANIA Z QUIZOW
    // pobierz zawartosc dla zadania typu 1
    public String getTaskForType1(int id)
    {
        c = database.rawQuery("select zad_polecenie from Quizy_zadania_p1 where zad_id = '" + id + "'", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer.append(""+ topicId);
        }
        return buffer.toString();
    }

    public String getPathToAnswersForType1(int id)
    {
        c = database.rawQuery("select odpA_sciezka from Quizy_zadania_p1 where zad_id = '" + id + "'", new String[]{});
        StringBuffer buffer1 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer1.append(""+ topicId + ";");
        }
        c = database.rawQuery("select odpB_sciezka from Quizy_zadania_p1 where zad_id = '" + id + "'", new String[]{});
        StringBuffer buffer2 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer2.append(""+ topicId + ";");
        }
        c = database.rawQuery("select odpC_sciezka from Quizy_zadania_p1 where zad_id = '" + id + "'", new String[]{});
        StringBuffer buffer3 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer3.append(""+ topicId + ";");
        }
        c = database.rawQuery("select odpD_sciezka from Quizy_zadania_p1 where zad_id = '" + id + "'", new String[]{});
        StringBuffer buffer4 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer4.append(""+ topicId + ";");
        }

        return buffer1.toString() + buffer2.toString() + buffer3.toString() + buffer4.toString();
    }

    public String getRightAnswerForType1(int id)
    {
        c = database.rawQuery("select odp_poprawna from Quizy_zadania_p1 where zad_id = '" + id + "'", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer.append(""+ topicId);
        }
        return buffer.toString();
    }

    // pobierz zawartosc dla zadania typu 2
    public String getTaskForType2(int id)
    {
        c = database.rawQuery("select zad_polecenie from Quizy_zadania_p2 where zad_id = '" + id + "'", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer.append(""+ topicId);
        }
        return buffer.toString();
    }

    public String getVideoPathForType2(int id)
    {
        c = database.rawQuery("select wideo_sciezka from Quizy_zadania_p2 where zad_id = '" + id + "'", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer.append(""+ topicId);
        }
        return buffer.toString();
    }

    public String getAnswersForType2(int id)
    {
        c = database.rawQuery("select odpA_tresc from Quizy_zadania_p2 where zad_id = '" + id + "'", new String[]{});
        StringBuffer buffer1 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer1.append(""+ topicId + ";");
        }
        c = database.rawQuery("select odpB_tresc from Quizy_zadania_p2 where zad_id = '" + id + "'", new String[]{});
        StringBuffer buffer2 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer2.append(""+ topicId + ";");
        }
        c = database.rawQuery("select odpC_tresc from Quizy_zadania_p2 where zad_id = '" + id + "'", new String[]{});
        StringBuffer buffer3 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer3.append(""+ topicId + ";");
        }
        c = database.rawQuery("select odpD_tresc from Quizy_zadania_p2 where zad_id = '" + id + "'", new String[]{});
        StringBuffer buffer4 = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer4.append(""+ topicId + ";");
        }

        return buffer1.toString() + buffer2.toString() + buffer3.toString() + buffer4.toString();
    }

    public String getRightAnswerForType2(int id)
    {
        c = database.rawQuery("select odp_poprawna from Quizy_zadania_p2 where zad_id = '" + id + "'", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer.append(""+ topicId);
        }
        return buffer.toString();
    }


    // pobierz zawartosc dla zadania typu 3
    public String getTaskForType3(int id)
    {
        c = database.rawQuery("select zad_polecenie from Quizy_zadania_p3 where zad_id = '" + id + "'", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer.append(""+ topicId);
        }
        return buffer.toString();
    }

    public String getRightSignForType3(int id)
    {
        c = database.rawQuery("select znak_porownawczy from Quizy_zadania_p3 where zad_id = '" + id + "'", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer.append(""+ topicId);
        }

        return buffer.toString();
    }


    //////////////////////////////////// POSTEPY /////////////////////////////////////////////////////
    public String getAllTests(){
        c = database.rawQuery("select test_nazwa from Testy", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            String Name = c.getString(0);
            buffer.append(""+ Name + ";");
        }
        return buffer.toString();
    }

    public String getTestResults()
    {
        c = database.rawQuery("select wynik from Testy_wyniki", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer.append(""+ topicId+ ";");
        }
        return buffer.toString();
    }

    public String getAllQuiz(){
        c = database.rawQuery("select quiz_nazwa from Quizy", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            String Name = c.getString(0);
            buffer.append(""+ Name + ";");
        }
        return buffer.toString();
    }

    public String getQuizResults()
    {
        c = database.rawQuery("select wynik from Quizy_wyniki", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer.append(""+ topicId+ ";");
        }
        return buffer.toString();
    }

    //osiagniecia:
    public String getAllOsi()
    {
        c = database.rawQuery("select osiag_nazwa from Osiagniecia", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer.append(""+ topicId+ ";");
        }
        return buffer.toString();
    }

    public String getAllOsiRes()
    {
        c = database.rawQuery("select czy_zal from Osiagniecia", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            String topicId = c.getString(0);
            buffer.append(""+ topicId+ ";");
        }
        return buffer.toString();
    }

    public String checkOsiag1()
    {
        c = database.rawQuery("select czy_zal from Osiagniecia where osiag_id = 1", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            String o = c.getString(0);
            buffer.append(""+ o);
        }
        return buffer.toString();
    }


}

