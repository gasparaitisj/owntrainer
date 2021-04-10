package com.gasparaiciukas.owntrainer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.gasparaiciukas.owntrainer.R;
import com.gasparaiciukas.owntrainer.adapter.MealAdapter;
import com.gasparaiciukas.owntrainer.database.Meal;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import io.realm.Realm;

public class AddMealToDiaryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MealAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private String primaryKey;
    private Realm realm;
    private TextInputEditText portionSizeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal_to_diary);

        primaryKey = getIntent().getStringExtra("primaryKey");

        // Get meals from database
        realm = Realm.getDefaultInstance();
        List<Meal> meals = realm.where(Meal.class).findAll();

        // Set up recycler view
        recyclerView = findViewById(R.id.add_meal_to_diary_recycler_view);
        adapter = new MealAdapter(3, meals, primaryKey);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.reload();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}