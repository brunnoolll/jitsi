package com.example.jitsitelemedicina.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import kotlin.collections.HashMap

interface ApiService {

    @POST("send")
    fun sendRemoteMessage(
        @HeaderMap headers: HashMap<String, String>,
        @Body remote: String?
    ): Call<String?>?
}