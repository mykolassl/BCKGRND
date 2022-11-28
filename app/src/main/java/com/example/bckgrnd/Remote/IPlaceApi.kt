package com.example.bckgrnd.Remote

import com.example.bckgrnd.Model.placeInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IPlaceApi {
    @GET("{xid}?")
    fun getInfo(@Path("xid") xid: String, @Query("apikey") apikey: String): Call<placeInfo>
}