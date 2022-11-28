package com.example.bckgrnd.Remote

import com.example.bckgrnd.Model.tblUser
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface IApi {
    @POST("api/Register")
    fun registerUser(@Body user: tblUser): Observable<String>

    @POST("api/Login")
    fun loginUser(@Body user: tblUser): Observable<String>
}