package com.example.pjmapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        if (savedInstanceState == null) {
            supportFragmentManager
               .beginTransaction()
               .replace(R.id.settings, SettingsFragment())
                .commit()
        }
       // supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Ustawienia")

    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            //var mPref1 = context?.getSharedPreferences("sync1",  Context.MODE_PRIVATE)
            val mPref2 = context?.getSharedPreferences("sync2",  Context.MODE_PRIVATE)

            //var sw1 = findPreference<SwitchPreferenceCompat>("sync1")
            //sw1?.setDefaultValue(mPref1)

            val sw2 = findPreference<SwitchPreferenceCompat>("sync2")
            sw2?.setDefaultValue(mPref2)

        }
    }



}