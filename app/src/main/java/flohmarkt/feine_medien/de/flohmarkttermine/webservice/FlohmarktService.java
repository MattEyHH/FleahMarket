package flohmarkt.feine_medien.de.flohmarkttermine.webservice;

import com.google.gson.JsonObject;

import java.util.List;

import flohmarkt.feine_medien.de.flohmarkttermine.model.Market;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface FlohmarktService {

    @GET("?q=s&c=all")
    Call <Object> getAllEvents();

    @GET("?q=cnfg")
    Call <Object> getConfig();

    @POST("/posts/")
    Call<Void> createNewPost(@Body JsonObject dataJson);
}
