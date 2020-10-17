package com.example.androidnetworkdemo.retrofit

import retrofit2.Call
import retrofit2.http.GET

interface AppService {

    @GET("get/data")
    fun getAppData(): Call<List<App>>

}