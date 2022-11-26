package com.example.bckgrnd.Remote

import android.util.Log
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetroFitClient {
    private var instance: Retrofit? = null

    fun getInstance(): Retrofit{
        Log.i("MESSAGE", "Instance")

        if(instance == null) {
            instance = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:7040/")
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
        }
        return instance!!
    }
}