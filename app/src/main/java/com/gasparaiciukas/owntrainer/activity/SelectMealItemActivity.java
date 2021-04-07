package com.gasparaiciukas.owntrainer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.gasparaiciukas.owntrainer.R;
import com.gasparaiciukas.owntrainer.adapter.MealAdapter;
import com.gasparaiciukas.owntrainer.database.Meal;

import java.util.List;

import io.realm.Realm;

public class SelectMealItemActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MealAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private int position;
    private int quantity;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_meal_item);

        // Get selected food position
        position = getIntent().getIntExtra("position", 0);
        quantity = getIntent().getIntExtra("quantity", 0);

        // Get meals from database
        realm = Realm.getDefaultInstance();
        List<Meal> meals = realm.where(Meal.class).findAll();

        // Set up recycler view
        recyclerView = findViewById(R.id.select_meal_item_recycler_view);
        adapter = new MealAdapter(true, position, meals, quantity);
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