package de.feine_medien.flohmarkt.webservice;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.feine_medien.flohmarkt.BuildConfig;
import de.feine_medien.flohmarkt.event.OnLoadAllMarketsSuccessfulEvent;
import de.feine_medien.flohmarkt.event.OnNoResultsFoundEvent;
import de.feine_medien.flohmarkt.event.OnNoZipOrCitySelectedEvent;
import de.feine_medien.flohmarkt.model.Market;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.net.HttpURLConnection.HTTP_NOT_MODIFIED;

public class Webservice {

    private FlohmarktService flohmarktService;

    public Webservice() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        OkHttpClient okHttpClient = builder.build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        flohmarktService = restAdapter.create(FlohmarktService.class);
    }

    public void loadEventsByDynamicCall(final Map<String, String> map) {
        flohmarktService.getEventsByDynamicCall(map).enqueue(new LoggingCallback<JsonObject>() {
            @Override
            void onSuccess(JsonObject responseBody, boolean isCached) {
                try {
                    JsonObject events = responseBody.getAsJsonObject("events");
                    List<Market> allMarkets = new ArrayList<>();

                    for (String key : events.keySet()) {
                        if (TextUtils.isDigitsOnly(key)) {
                            JsonObject subObject = events.getAsJsonObject(key);
                            String fleaMarketId = subObject.keySet().iterator().next();
                            JsonObject fleaMarket = subObject.getAsJsonObject(fleaMarketId);
                            fleaMarket.addProperty("id", fleaMarketId);
                            Gson gson = new Gson();
                            Market market = gson.fromJson(fleaMarket.toString(), Market.class);

                            allMarkets.add(market);
                        }
                    }
                    EventBus.getDefault().postSticky(new OnLoadAllMarketsSuccessfulEvent(allMarkets));

                } catch (Exception e) {
                    JsonObject error = responseBody.getAsJsonObject("error");
                    String errorMessage = error.get("message").getAsString();
                    switch (errorMessage) {
                        case "No Results":
                            EventBus.getDefault().postSticky(new OnNoResultsFoundEvent());
                            break;
                        case "No Zip or City":
                            EventBus.getDefault().postSticky(new OnNoZipOrCitySelectedEvent());
                            break;
                    }
                }
            }

            @Override
            void onFailure() {
                super.onFailure();
            }
        });
    }

    private abstract class LoggingCallback<T> implements retrofit2.Callback<T> {

        @Override
        public final void onResponse(@NonNull final Call<T> call, @NonNull final Response<T> response) {
            if (response.isSuccessful() && (response.body() != null)) {
                onSuccess(response.body(), response.code() == HTTP_NOT_MODIFIED);
                call.request().url();

            } else {
                logFailure(call, response);
                onFailure();
                call.request().url();
            }
        }

        @Override
        public final void onFailure(@NonNull final Call<T> call, @NonNull final Throwable t) {
            //Crashlytics.logException(t);
            onFailure();
        }

        private void logFailure(@NonNull final Call<T> call, @NonNull final Response<T> response) {
            //Crashlytics.logException(new NetworkErrorException(String.format(Locale.getDefault(),
            //"Call [%s] failed with response [%s]", call, response)));
        }

        abstract void onSuccess(final T responseBody, final boolean isCached);

        void onFailure() {
        }
    }
}
