package de.feine_medien.flohmarkt.webservice;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import de.feine_medien.flohmarkt.BuildConfig;
import de.feine_medien.flohmarkt.event.OnLoadAllMarketsSuccessfulEvent;
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

    public void loadConfig() {
        flohmarktService.getConfig().enqueue(new LoggingCallback<Object>() {

            @Override
            void onSuccess(Object responseBody, boolean isCached) {
                //EventBus.getDefault().postSticky(new OnLoadAllMarketsSuccessfulEvent(responseBody));
            }

            @Override
            void onFailure() {
                super.onFailure();
            }
        });
    }

    public void loadAllEvents() {
        flohmarktService.getAllEvents().enqueue(new LoggingCallback<JsonObject>() {
            @Override
            void onSuccess(JsonObject responseBody, boolean isCached) {
                JsonObject events = responseBody.getAsJsonObject("events");
                List<Market> allMarkets = new ArrayList<>();

                for (String key : events.keySet()) {
                    if (TextUtils.isDigitsOnly(key)) {
                        JsonObject subObject = events.getAsJsonObject(key);
                        String fleahMarketId = subObject.keySet().iterator().next();
                        JsonObject fleahMarket = subObject.getAsJsonObject(fleahMarketId);
                        fleahMarket.addProperty("id", fleahMarketId);
                        Gson gson = new Gson();
                        Market market = gson.fromJson(fleahMarket.toString(), Market.class);

                        allMarkets.add(market);
                    }
                }
                EventBus.getDefault().postSticky(new OnLoadAllMarketsSuccessfulEvent(allMarkets));
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

            } else {
                logFailure(call, response);
                onFailure();
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
