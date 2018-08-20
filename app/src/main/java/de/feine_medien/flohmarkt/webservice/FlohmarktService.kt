package de.feine_medien.flohmarkt.webservice

import com.google.gson.JsonObject

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface FlohmarktService {

    @GET("?q=s&c=all")
    fun getAllEvents(): Call<JsonObject>

    @GET("?q=cnfg")
    fun getConfig(): Call<Any>

    @GET("?q=s")
    fun getEventsByDynamicCall(@QueryMap map: Map<String, String>): Call<JsonObject>

    @POST("/posts/")
    fun createNewPost(@Body dataJson: JsonObject): Call<Void>
}
