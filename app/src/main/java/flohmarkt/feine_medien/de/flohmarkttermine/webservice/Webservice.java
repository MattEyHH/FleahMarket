package flohmarkt.feine_medien.de.flohmarkttermine.webservice;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;
import java.util.List;

import flohmarkt.feine_medien.de.flohmarkttermine.BuildConfig;
import flohmarkt.feine_medien.de.flohmarkttermine.Event.OnLoadAllEventsSuccessfulEvent;
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
                EventBus.getDefault().postSticky(new OnLoadAllEventsSuccessfulEvent(responseBody));
            }

            @Override
            void onFailure() {
                super.onFailure();
            }
        });
    }

    public void loadAllEvents() {
        flohmarktService.getAllEvents().enqueue(new LoggingCallback<Object>() {
            @Override
            void onSuccess(Object responseBody, boolean isCached) {
                EventBus.getDefault().postSticky(new OnLoadAllEventsSuccessfulEvent(responseBody));
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
