package com.gasparaiciukas.owntrainer.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gasparaiciukas.owntrainer.adapter.FoodAdapter;
import com.gasparaiciukas.owntrainer.R;
import com.gasparaiciukas.owntrainer.network.GetResponse;
import com.gasparaiciukas.owntrainer.network.GetService;
import com.gasparaiciukas.owntrainer.network.Hint;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.gasparaiciukas.owntrainer.adapter.FoodAdapter.foods;

public class FoodFragment extends Fragment {
    public static FoodFragment newInstance() {
        return new FoodFragment();
    }

    // API
    private GetResponse getResponse;
    private GetService getService;

    // UI
    private TextInputLayout foodInputLayout;
    private TextInputEditText foodInputText;
    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private LinearLayout foodItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get request
        Retrofit retrofitGet = new Retrofit.Builder()
                .baseUrl(GetService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        getService = retrofitGet.create(GetService.class);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_food, container, false);
        // Set up food search input field
        foodInputLayout = rootView.findViewById(R.id.foodInputLayout);
        foodInputText = rootView.findViewById(R.id.food_input_text);

        // Send get request on end icon clicked
        foodInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(foodInputText.getText())) {
                    Toast.makeText(v.getContext(), foodInputText.getText().toString(), Toast.LENGTH_SHORT).show();
                    sendGet(foodInputText.getText().toString());
                }
                else {
                    Toast.makeText(v.getContext(), "Search query is empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Also send get request on keyboard search button clicked
        foodInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!TextUtils.isEmpty(foodInputText.getText())) {
                        Toast.makeText(v.getContext(), foodInputText.getText().toString(), Toast.LENGTH_SHORT).show();
                        sendGet(foodInputText.getText().toString());
                    }
                    else {
                        Toast.makeText(v.getContext(), "Search query is empty!", Toast.LENGTH_SHORT).show();
                    }
                    handled = true;
                }
                return handled;
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set up recycler view
        recyclerView = view.findViewById(R.id.food_recycler_view);
        foodItem = view.findViewById(R.id.food_row);
        adapter = new FoodAdapter();
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void sendGet(String query) {
        foods.clear();

        //TODO: hide API keys before pushing
        String appId = "0de8a357";
        String appKey = "30a0c47f24999903266d0171d50b7aa6";
        Call<GetResponse> call = getService.getResponse(appId, appKey, query);
        call.enqueue(new Callback<GetResponse>() {
            @Override
            public void onResponse(@NotNull Call<GetResponse> call, @NotNull Response<GetResponse> response) {
                getResponse = response.body();
                assert getResponse != null;
                List<Hint> hints = getResponse.getHints();
                Log.d("testGet", "Sent: " + getResponse.getText());
                for (int i = 0; i < hints.size(); i++) {
                    foods.add(hints.get(i).getFood());
                }
                Log.d("testGet", String.valueOf(foods.size()));
                adapter.reload();
            }

            @Override
            public void onFailure(@NotNull Call<GetResponse> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Search request failed!", Toast.LENGTH_LONG).show();
                Log.d("testGet", t.toString());
            }
        });
    }
}