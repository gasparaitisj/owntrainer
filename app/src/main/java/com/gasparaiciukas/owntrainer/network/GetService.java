package com.gasparaiciukas.owntrainer.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface GetService {
    String BASE_URL = "https://api.edamam.com/api/food-database/v2/";

    @Headers({
            "Content-Type: application/json"
    })
    @GET("parser")
    Call<GetResponse> getResponse(@Query("app_id") String appId, @Query("app_key") String appKey,
                                  @Query("ingr") String query);
}
