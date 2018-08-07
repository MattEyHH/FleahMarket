package de.feine_medien.flohmarkt.webservice;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface FlohmarktService {

    @GET("?q=s&c=all")
    Call<JsonObject> getAllEvents();

    @GET("?q=cnfg")
    Call<Object> getConfig();

    @POST("/posts/")
    Call<Void> createNewPost(@Body JsonObject dataJson);
}
