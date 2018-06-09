package com.godlontonconsulting.entranze.service;

import okhttp3.MultipartBody;
import retrofit2.http.DELETE;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;

import com.godlontonconsulting.entranze.pojos.ActivateUserDtoResponse;
import com.godlontonconsulting.entranze.pojos.AddEntranzeResponse;
import com.godlontonconsulting.entranze.pojos.AddEvents;
import com.godlontonconsulting.entranze.pojos.AssignGateKeepers;
import com.godlontonconsulting.entranze.pojos.AssignGateKeepersResponseDTO;
import com.godlontonconsulting.entranze.pojos.AuthGateToken;
import com.godlontonconsulting.entranze.pojos.AuthorizeTokenResponse;
import com.godlontonconsulting.entranze.pojos.CustomersDTO;
import com.godlontonconsulting.entranze.pojos.DeviceUser;
import com.godlontonconsulting.entranze.pojos.Entranze;
import com.godlontonconsulting.entranze.pojos.EntranzePurchases;
import com.godlontonconsulting.entranze.pojos.Event;
import com.godlontonconsulting.entranze.pojos.Fav;
import com.godlontonconsulting.entranze.pojos.FollowDTO;
import com.godlontonconsulting.entranze.pojos.FollowUser;
import com.godlontonconsulting.entranze.pojos.GateTokenResponse;
import com.godlontonconsulting.entranze.pojos.GetContacts;
import com.godlontonconsulting.entranze.pojos.GetFollowers;
import com.godlontonconsulting.entranze.pojos.ImageUploadResponse;
import com.godlontonconsulting.entranze.pojos.InstallDtoResponse;
import com.godlontonconsulting.entranze.pojos.InstallUser;
import com.godlontonconsulting.entranze.pojos.Me;
import com.godlontonconsulting.entranze.pojos.NewRefreshAccessToken;
import com.godlontonconsulting.entranze.pojos.PurchaseResponse;
import com.godlontonconsulting.entranze.pojos.TicketResponse;
import com.godlontonconsulting.entranze.pojos.ViewGateKeepers;
import okhttp3.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Energy on 2017/05/02.
 */

public interface MyApiServiceKat {

    @Headers({
            "Accept:application/json",
            "Content-type: application/json"
    })
    @POST("api/entranzes")
    Call<AddEntranzeResponse> addEventDetails(@Body Entranze event);

    @Headers({
            "Accept:application/json",
            "Content-type: application/json"
    })
    @PUT("api/entranzes/{id}")
    Call<AddEntranzeResponse> updateEventDetails(@Path("id") String id, @Body AddEvents event);

    @Headers({
            "Accept:application/json",
            "Accept: */*"
    })

    @GET("api/entranzes/{id}")
    Call<Event> getEventId();

    @GET("api/entranzes")
    Call<Event> getEventList();


    @Headers({
            "Accept:application/json",
            "Accept: */*"
    })
    @POST("api/me/following")
    Call<FollowDTO> followUser(@Body FollowUser mobile);


    @Headers({
            "Accept:application/json",
            "Accept: */*"
    })
    @POST("api/me/devices")
    Call<FollowDTO> sendDevice(@Body DeviceUser user);



    @Headers({
            "Accept:application/json",
            "Accept: */*"
    })
    @DELETE("api/me/following/{msisdn}")
    Call<FollowDTO> unfollowUser(@Path("msisdn") long msisdn);

    @Headers({
            "Accept:application/json",
            "Accept: */*"
    })
    @GET("api/customers/{msisdn}")
    Call<CustomersDTO>getFollower(@Path("msisdn") long msisdn);

    @Headers({
            "Accept:application/json",
            "Content-type: multipart/form-data"
    })
    @Multipart
    @POST("api/entranzes/{id}/avatar.file")
    Call<ImageUploadResponse> uploadEventImage(@Path("id") String id, @Part MultipartBody.Part image);
    ///Call<ImageUploadResponse> uploadEventImage(@Path("id") String id, @Part MultipartBody.Part image);

    @Headers({
            "Accept:application/json",
            "Content-type: multipart/form-data"
    })
    @Multipart
    @POST("accounts/avatar.file ")
    Call<ImageUploadResponse> uploadUserImage(@Path("id") String id, @Part MultipartBody.Part image);
    ///Call<ImageUploadResponse> uploadEventImage(@Path("id") String id, @Part MultipartBody.Part image);

    @Headers({
            "Accept:application/json",
            "Accept: */*"
    })
    @GET("api/me/followers")
    Call<GetFollowers> getFollowers();

    @Headers({
            "Accept:application/json",
            "Accept: */*"
    })
    @GET("api/me/following")
    Call<GetFollowers> getFollowing();


    @Headers({
            "Accept:application/json",
            "Accept: */*"
    })
    @POST("api/me/addressbook")
    Call<GetContacts> postContacts(@Body ArrayList contacts);

    @Headers({
            "Accept:application/json",
            "Accept: */*"
    })
    @GET("api/entranzes/{id}")
    Call<TicketResponse> getEvent(@Path("id") String id);

    @Headers({
            "Accept:application/json",
            "Accept: */*"
    })
    @POST("api/entranzes/favourites")
    Call<FollowDTO> postFav(@Body Fav fav);

    @Headers({
            "Accept:application/json",
            "Accept: */*"
    })
    @HTTP(method = "DELETE", path = "api/entranzes/favourites", hasBody = true)
    Call<FollowDTO> unpostFav(@Body Fav favs);


    @Headers({
            "Accept:application/json",
            "Accept: */*"
    })
    @GET("api/entranzes/favourites")
    Call<Event> getFavs();

    @Headers({
            "Accept:application/json",
            "Accept: */*"
    })
    @GET("api/entranzes/{id}/gatekeepers")
    Call<List<ViewGateKeepers>> getGateKeepers(@Path("id") String id);

    @Headers({
            "Accept:application/json",
            "Accept: */*"
    })
    @PUT("api/entranzes/{id}/gatekeepers")
    Call<AssignGateKeepersResponseDTO> assignGateKeeper(@Path("id") String id, @Body List<AssignGateKeepers> gatekeepers);


    @Headers({
            "Accept:application/json",
            "Accept: */*"
    })
    @PUT("api/purchases/{id}/transfer/{msisdn}")
    Call<AssignGateKeepersResponseDTO> transferTicket(@Path("id") String id, @Path("msisdn") String msisdn);


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
    Call<ActivateUserDtoResponse> activateUser(@Query("username") String username, @Query("password") String password, @Query("grant-type") String grantType);



    @Headers({
            "Accept:application/json",
            "Content-type: application/json"
    })
    @GET("api/purchases")
    Call<EntranzePurchases> getPurchases();


    @Headers({
            "Accept:application/json",
            "Content-type: application/json"
    })
    @POST("api/entranzes/{event_id}/purchases")
    Call<PurchaseResponse> makePayment(@Path("event_id") String event_id);


    @Headers({
            "Accept:application/json",
            "Content-type: application/json"
    })
    @GET("api/purchases/{id}/gatetoken")
    Call<GateTokenResponse> qrCodeFromToken(@Path("id") String id);


    @Headers({
            "Accept:application/json",
            "Content-type: application/json"
    })
    @PUT("api/gates/{id}/authorize")
    Call<AuthorizeTokenResponse> scanTicket(@Path("id") String id, @Body AuthGateToken token);
}