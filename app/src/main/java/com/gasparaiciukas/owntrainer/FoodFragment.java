package com.gasparaiciukas.owntrainer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.Fragment;

import com.gasparaiciukas.owntrainer.network.GetResponse;
import com.gasparaiciukas.owntrainer.network.GetService;
import com.gasparaiciukas.owntrainer.network.Ingredient;
import com.gasparaiciukas.owntrainer.network.Parsed;
import com.gasparaiciukas.owntrainer.network.PostQuery;
import com.gasparaiciukas.owntrainer.network.PostResponse;
import com.gasparaiciukas.owntrainer.network.PostService;
import com.gasparaiciukas.owntrainer.network.ResponseIngredient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FoodFragment extends Fragment {
    public static FoodFragment newInstance() {
        return new FoodFragment();
    }

    private PostService postService;
    private PostResponse postResponse;
    private GetResponse getResponse;
    private GetService getService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Post request
        Retrofit retrofitPost = new Retrofit.Builder()
                .baseUrl("https://api.edamam.com/api/food-database/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        postService = retrofitPost.create(PostService.class);
        sendPost();

        // Get request
        Retrofit retrofitGet = new Retrofit.Builder()
                .baseUrl("https://api.edamam.com/api/food-database/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        getService = retrofitGet.create(GetService.class);
        sendGet();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_food, container, false);
    }

    private void sendPost() {
        // Initialize test post query
        PostQuery postQuery = new PostQuery();
        List<Ingredient> ingredientList = new ArrayList<>();
        Ingredient ingredient = new Ingredient();
        ingredient.setQuantity(100);
        ingredient.setFoodId("food_bnbh4ycaqj9as0a9z7h9xb2wmgat");
        ingredient.setMeasureURI("http://www.edamam.com/ontologies/edamam.owl#Measure_gram");
        ingredientList.add(ingredient);
        postQuery.setIngredients(ingredientList);
        Call<PostResponse> call = postService.sendPosts(postQuery);

        // Initialize test post response
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                postResponse = response.body();
                List<ResponseIngredient> responseIngredientList = postResponse.getIngredients();
                for (int i = 0; i < responseIngredientList.size(); i++) {
                    List<Parsed> parsedList = responseIngredientList.get(i).getParsed();
                    for (int j = 0; j < parsedList.size(); j++) {
                        String foodName = parsedList.get(j).getFood();
                        int foodKcal = postResponse.getCalories();
                        Log.d("testPost", foodName + ", 100g (kCal: " + foodKcal + ")");
                    }
                }
                //Toast.makeText(getApplicationContext(), "Response success!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<PostResponse>call, Throwable t) {
                //Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
                Log.d("testPost", t.toString());
            }
        });
    }
    private void sendGet() {
        Call<GetResponse> call = getService.getResponse();
        call.enqueue(new Callback<GetResponse>() {
            @Override
            public void onResponse(Call<GetResponse> call, Response<GetResponse> response) {
                getResponse = response.body();
                Log.d("testGet", getResponse.getText());
            }

            @Override
            public void onFailure(Call<GetResponse> call, Throwable t) {
                // if error occurs in network transaction then we can get the error in this method.
                //Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                Log.d("testGet", t.toString());
            }
        });
    }
}