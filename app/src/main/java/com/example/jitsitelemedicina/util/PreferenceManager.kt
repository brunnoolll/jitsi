package com.example.jitsitelemedicina.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class PreferenceManager(context: Context) {

    private val appContext = context.applicationContext

    private val preference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)

    fun putBoolean(key: String, value: Boolean){
        val editor = preference.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String) : Boolean{
        return preference.getBoolean(key, false)
    }

    fun putString(key: String, value : String){
        val editor =  preference.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String): String{
        return preference.getString(key, null).toString()
    }

    fun clearPreferences(){
        val editor = preference.edit()
        editor.clear()
        editor.apply()
    }

}