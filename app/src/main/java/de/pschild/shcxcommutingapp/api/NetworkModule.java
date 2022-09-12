package de.pschild.shcxcommutingapp.api;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import de.pschild.shcxcommutingapp.BuildConfig;
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@InstallIn(SingletonComponent.class)
@Module
public class NetworkModule {

    private static final String API_BASE_URL = BuildConfig.endpoint;

    @Provides
    @Singleton
    public Retrofit provideRetrofit(){
        return new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(new OkHttpClient()
                .newBuilder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build()
            )
            .build();
    }

    @Singleton
    @Provides
    public CommuterApi getApiInterface(Retrofit retrofit){
        return retrofit.create(CommuterApi.class);
    }
}
