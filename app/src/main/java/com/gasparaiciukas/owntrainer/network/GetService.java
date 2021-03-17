package com.gasparaiciukas.owntrainer.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface GetService {
    String API_ROUTE = "parser?app_id=0de8a357&app_key=30a0c47f24999903266d0171d50b7aa6&ingr=gala apple";
    @Headers({
            "Content-Type: application/json"
    })
    @GET(API_ROUTE)
    Call<GetResponse> getResponse();
}
