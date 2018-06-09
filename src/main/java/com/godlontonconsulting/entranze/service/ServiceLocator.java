package com.godlontonconsulting.entranze.service;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

public enum ServiceLocator {
    INSTANCE;
    private static final String BASE_URL = "https://entranze.bluespine.co.za";
    private String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private MyApiServiceKat myApiService;

    OkHttpClient okHttpClient;


    ServiceLocator() {
        //Fetch token once from localStorage
        final String accessToken = null;
        refreshToken(accessToken);
    }

    public void refreshToken(final String accessToken) {
        if (accessToken!=null) {
            okHttpClient = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                    Request originalRequest = chain.request();
                    //String accessToken="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiYmx1ZXNwaW5lLWFjY291bnRzIiwiYmx1ZXNwaW5lLWVudHJhbnplIl0sInVzZXJfbmFtZSI6IjI3NzQ3NzM2MDY1Iiwic2NvcGUiOlsicmVhZCIsIndyaXRlIl0sIm9yZ2FuaXphdGlvbiI6IjI3NzQ3NzM2MDY1ZUJnSSIsImV4cCI6MTUwMzIxOTcwMywiYXV0aG9yaXRpZXMiOlsiVVNFUiJdLCJqdGkiOiIxMTNjY2RlZS1mZDQ0LTRmMGItYTg3ZC05Mzg4YWY4NDlhZGUiLCJjbGllbnRfaWQiOiJlbnRyYW56ZSJ9.Hdb4vn9sFMnCA0kPUemcBSWT4B11200OpLli4GDd6BY8iYeptLDeqQdo8cY4mpQxTXo2zMEi2c3YoY8THRab6bifz0vZD3wMGFisz4YBjLcW_K00sQMvqB9NWh1g6q9wBaEbG-UR3QH8nQCZiCbguqe_PW5ZQ4t0vu2lUp9MeoEwWDDkxWXpQTRtYWuxrda-_S4PLA4-Dy4DMduNEU-5NJrUKDl77solhtCmrK2KGV37v2soGFli0RhHA5zX-KFOKPNEx6A8wwVqvHmNZGe_9NICD2SbLGqOkOR7WgZ_8QbIEIlfK-PsXKHTVxI_U49OXCHcPLxBPh4W27cw2trrlQ";
                    Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                            "Bearer ".concat(accessToken));
                    Request newRequest = builder.build();
                    return chain.proceed(newRequest);
                }
            }).readTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .build();

            //You can use GSON
            Gson converterFactory = new GsonBuilder()
                    .setLenient()
                    .create();
            //Or you can use Jackson
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setDateFormat(new SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH));
//        JacksonConverterFactory converterFactory = JacksonConverterFactory.create(objectMapper);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(converterFactory))
                    .build();
            myApiService = retrofit.create(MyApiServiceKat.class);
        }
    }

    public MyApiServiceKat getMyApiService() {
        return myApiService;
    }

    public Picasso getPicasso(final Context context) {
        return new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();
    }
}
