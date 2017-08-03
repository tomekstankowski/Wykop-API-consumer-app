package com.tomaszstankowski.wykopapi.service

import com.tomaszstankowski.wykopapi.model.Comment
import com.tomaszstankowski.wykopapi.model.Link
import com.tomaszstankowski.wykopapi.model.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * REST API service instantiated by Retrofit.
 * @param appkey is requested to communicate with Wykop API.
 * It can not be injected so another wrapper class is needed.
 */
interface WykopAPI {

    @GET("/link/index/{id}/appkey,{appkey}")
    fun getLink(@Path("id") linkId: Int, @Path("appkey") appkey: String): Call<Link>

    @GET("/links/promoted/appkey,{appkey},page,{page},sort,day")
    fun getPromotedLinks(@Path("appkey") appkey: String, @Path("page") page: Int): Call<List<Link>>

    @GET("/link/comments/{id}/appkey,{appkey}")
    fun getComments(@Path("appkey") appkey: String, @Path("id") linkId: Int): Call<List<Comment>>

    @GET("/profile/index/{username}/appkey,{appkey}")
    fun getUser(@Path("username") username: String, @Path("appkey") appkey: String): Call<User>
}