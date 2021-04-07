package com.gasparaiciukas.owntrainer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;

import com.gasparaiciukas.owntrainer.R;
import com.gasparaiciukas.owntrainer.database.Meal;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import io.realm.Realm;

public class CreateMealItemActivity extends AppCompatActivity {

    // Views
    private MaterialToolbar toolbar;
    private TextInputEditText tTitle;
    private TextInputEditText tInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meal_item);
        initUi();
    }

    private void initUi() {
        // Get views
        toolbar = findViewById(R.id.create_meal_top_app_bar);
        tTitle = findViewById(R.id.create_meal_title_input_text);
        tInstructions = findViewById(R.id.create_meal_instructions_text);

        // Back button
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Save button
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.create_meal_top_app_bar_save) {
                    // Parse text edits
                    String title;
                    String instructions;
                    if (tTitle.getText() == null)
                        title = "No title";
                    else if (tTitle.getText().toString().trim().isEmpty())
                        title = "No title";
                    else
                        title = tTitle.getText().toString();
                    if (tInstructions.getText() == null)
                        instructions = "No instructions";
                    else if (tTitle.getText().toString().trim().isEmpty())
                        instructions = "No title";
                    else
                        instructions = tInstructions.getText().toString();

                    // Add meal to database
                    Meal meal = new Meal();
                    meal.setTitle(title);
                    meal.setInstructions(instructions);
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(@NotNull Realm realm) {
                            realm.insertOrUpdate(meal);
                        }
                    });
                    realm.close();

                    // Finish activity
                    finish();
                    return true;
                }
                return false;
            }
        });
    }
}