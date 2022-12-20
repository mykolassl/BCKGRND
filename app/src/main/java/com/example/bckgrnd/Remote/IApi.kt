package com.example.bckgrnd.Remote

import com.beust.klaxon.JsonArray
import com.example.bckgrnd.Model.tblLocation
import com.example.bckgrnd.Model.tblLocationResponse
import com.example.bckgrnd.Model.tblUser
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.http.*

interface IApi {
    @Headers("Connection: close")
    @POST("api/Register")
    fun registerUser(@Body user: tblUser): Observable<String>

    @Headers("Connection: close")
    @POST("api/Login")
    fun loginUser(@Body user: tblUser): Observable<tblUser>

    @Headers("Connection: close")
    @DELETE("api/Login/{id}")
    fun deleteUser(@Path("id") id: Int): Observable<String>


    @Headers("Connection: close")
    @POST("api/Location")
    fun addLocation(@Body location: tblLocation): Observable<String>

    @GET("api/Location/{requestString}")
    fun getPlaceInfo(@Path("requestString") requestString: String): Call<Array<tblLocationResponse>>

}