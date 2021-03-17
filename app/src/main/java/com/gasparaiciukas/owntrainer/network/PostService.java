package com.gasparaiciukas.owntrainer.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PostService {

    String API_ROUTE = "nutrients?app_id=0de8a357&app_key=30a0c47f24999903266d0171d50b7aa6";
    @Headers({
            "Content-Type: application/json"
    })
    @POST(API_ROUTE)
    Call<PostResponse> sendPosts(@Body PostQuery postQuery);
}
