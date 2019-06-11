package com.example.barnaclebot.chantel.util.api;


import com.example.barnaclebot.chantel.model.ServerResponse;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

public interface BaseApiService {
    @GET("token")
    Call<ResponseBody> loginRequest(@Header("client_id") String client_id,
                                    @Header("client_secret") String client_secret,
                                    @Header("username") String username,
                                    @Header("password") String password);

    /*@Multipart
    @POST("document-extractor")
    Call<ResponseBody> uploadImage(@Header("access_token") String access_token,
                                     @Part MultipartBody.Part image);*/

    @Multipart
    @POST("document-extractor")
    Call<ServerResponse> upload(
            @Header("access_token") String access_token,
            @PartMap Map<String, RequestBody> map
            //@Part MultipartBody.Part filePart
    );


}
