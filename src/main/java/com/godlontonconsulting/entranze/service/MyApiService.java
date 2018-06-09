package com.godlontonconsulting.entranze.service;

import com.godlontonconsulting.entranze.pojos.ActivateUserDtoResponse;
import com.godlontonconsulting.entranze.pojos.AddEntranzeResponse;
import com.godlontonconsulting.entranze.pojos.AddEvents;
import com.godlontonconsulting.entranze.pojos.AssignGateKeepers;
import com.godlontonconsulting.entranze.pojos.AssignGateKeepersResponseDTO;
import com.godlontonconsulting.entranze.pojos.AuthGateToken;
import com.godlontonconsulting.entranze.pojos.AuthorizeTokenResponse;
import com.godlontonconsulting.entranze.pojos.DeviceUser;
import com.godlontonconsulting.entranze.pojos.Entranze;
import com.godlontonconsulting.entranze.pojos.EntranzePurchases;
import com.godlontonconsulting.entranze.pojos.Event;
import com.godlontonconsulting.entranze.pojos.FollowDTO;
import com.godlontonconsulting.entranze.pojos.GateTokenResponse;
import com.godlontonconsulting.entranze.pojos.ImageUploadResponse;
import com.godlontonconsulting.entranze.pojos.InstallDtoResponse;
import com.godlontonconsulting.entranze.pojos.InstallUser;
import com.godlontonconsulting.entranze.pojos.Me;
import com.godlontonconsulting.entranze.pojos.NewRefreshAccessToken;
import com.godlontonconsulting.entranze.pojos.PurchaseResponse;
import com.godlontonconsulting.entranze.pojos.ViewGateKeepers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Energy on 2017/05/02.
 */

public interface MyApiService {

    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build();

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://entranze.bluespine.co.za/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build();

    @Headers({
            "Accept:application/json",
            "Content-type: application/json"
    })
    @POST("api/entranzes")
    Call<AddEntranzeResponse> addEventDetails(@Header("Authorization") String authorization, @Body Entranze event);

    @Headers({
            "Accept:application/json",
            "Content-type: application/json"
    })
    @PUT("api/entranzes/{id}")
    Call<AddEntranzeResponse> updateEventDetails(@Path("id") String id,@Header("Authorization") String authorization, @Body AddEvents event);

    @Headers({
            "Accept:application/json",
            "Accept: */*"
    })
    @GET("api/entranzes")
    Call<Event> getEventList(@Header("Authorization") String authorization);

    @Headers({
            "Accept:application/json",
            "Accept: */*"
    })
    @GET("api/entranzes/{id}/gatekeepers")
    Call<List<ViewGateKeepers>> getGateKeepers(@Path("id") String id, @Header("Authorization") String authorization);

    @Headers({
            "Accept:application/json",
            "Accept: */*"
    })
    @PUT("api/entranzes/{id}/gatekeepers")
    Call<AssignGateKeepersResponseDTO> assignGateKeeper(@Path("id") String id, @Header("Authorization") String authorization,@Body List<AssignGateKeepers> gatekeepers);


    @Headers({
            "Accept:application/json",
            "Accept: */*"
    })
    @PUT("api/purchases/{id}/transfer/{msisdn}")
    Call<AssignGateKeepersResponseDTO> transferTicket(@Path("id") String id,@Path("msisdn") String msisdn, @Header("Authorization") String authorization);

    @Headers({
            "Accept:application/json",
            "Accept: */*"
    })
    @PUT("api/entranzes/{id}/invitations")
    Call<AssignGateKeepersResponseDTO> inviteFriend(@Path("id") String id, @Header("Authorization") String authorization,@Body List<AssignGateKeepers> gatekeepers);


    @Headers({
            "Accept:application/json",
            "Accept: */*"
    })
    @POST("api/me/devices")
    Call<FollowDTO> sendDevice(@Body DeviceUser user, @Header("Authorization") String authorization);

    @Headers({
            "Accept:application/json",
            "Accept: */*"
    })
    @GET("api/me")
    Call<Me> getMeAccount(@Header("Authorization") String authorization);

    @Headers({
            "Accept:application/json",
            "Content-type: application/json",
            "Accept-Language:en_ZA"
    })
    @POST("ext/accounts/install")
    Call<InstallDtoResponse> registerUser(@Body InstallUser user);


    @Headers({
            "Accept:application/json",
            "Content-type: application/json"
    })
    @POST("ext/accounts/refresh_token")
    Call<NewRefreshAccessToken> getNewAccessToken(
            @Query("grant_type") String grantType,
            @Query("refresh_token") String refreshToken
    );


    @Headers({
            "Accept:application/json",
            "Content-type: application/json",
    })
    @POST("ext/accounts/activate")
    Call<ActivateUserDtoResponse> activateUser(@Query("username") String username, @Query("password") String password,  @Query("grant-type") String grantType);


    @Headers({
            "Accept:application/json",
            "Content-type: multipart/form-data"
    })
    @Multipart
    @POST("api/entranzes/{id}/avatar.file")
    Call<ImageUploadResponse> uploadEventImage(@Header("Authorization") String authorization,@Path("id") String image_id, @Part MultipartBody.Part image);

    @Headers({
            "Accept:application/json",
            "Content-type: application/json"
    })
    @GET("api/entranzes/{image_id}/avatar")
    Call<ResponseBody> downloadImage(@Path("image_id") String image_id, @Header("Authorization") String authorization);


    @Headers({
            "Accept:application/json",
            "Content-type: application/json"
    })
    @GET("api/purchases")
    Call<EntranzePurchases> getPurchases(@Header("Authorization") String authorization);


    @Headers({
            "Accept:application/json",
            "Content-type: application/json"
    })
    @POST("api/entranzes/{event_id}/purchases")
    Call<PurchaseResponse> makePayment(@Path("event_id") String event_id, @Header("Authorization") String authorization);


    @Headers({
            "Accept:application/json",
            "Content-type: application/json"
    })
    @GET("api/purchases/{id}/gatetoken")
    Call<GateTokenResponse> qrCodeFromToken(@Path("id") String id, @Header("Authorization") String authorization);


    @Headers({
            "Accept:application/json",
            "Content-type: application/json"
    })
    @PUT("api/gates/{id}/authorize")
    Call<AuthorizeTokenResponse> scanTicket(@Header("Authorization") String authorization,@Path("id") String id, @Body AuthGateToken token);
}
