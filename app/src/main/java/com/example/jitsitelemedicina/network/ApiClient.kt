package com.example.jitsitelemedicina.network

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create

class ApiClient(var context: Context) {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com/fcm/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

//    val retrofit = Retrofit.Builder()
//            .baseUrl("https://fcm.googleapis.com/fcm/")
//            .addConverterFactory(ScalarsConverterFactory.create())
//            .build()

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

}