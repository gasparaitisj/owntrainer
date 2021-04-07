package com.gasparaiciukas.owntrainer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.gasparaiciukas.owntrainer.R;
import com.gasparaiciukas.owntrainer.database.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;

public class SettingsActivity extends AppCompatActivity {

    // Variables
    private String sex;
    private int age;
    private int height;
    private double weight;
    private String lifestyle;

    // Text views
    private AutoCompleteTextView sexMenu;
    private TextInputEditText tAge;
    private TextInputEditText tHeight;
    private TextInputEditText tWeight;
    private AutoCompleteTextView lifestyleMenu;

    // Database
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initUi();
        loadData();
    }

    private void initUi() {
        // Back button
        MaterialToolbar toolbar = findViewById(R.id.settings_top_app_bar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Options
        sexMenu = findViewById(R.id.settings_sex_menu);
        tAge = findViewById(R.id.settings_age_text);
        tHeight = findViewById(R.id.settings_height_text);
        tWeight = findViewById(R.id.settings_weight_text);
        lifestyleMenu = findViewById(R.id.settings_lifestyle_menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        writeUserToDatabase();
    }

    private void writeUserToDatabase() {
        // Write to database
        realm = Realm.getDefaultInstance();
        User user = new User();
        user.setUserId("user");
        user.setAgeInYears(age);
        user.setSex(sex);
        user.setHeightInCm(height);
        user.setWeightInKg(weight);
        user.setLifestyle(lifestyle);
        user.setStepLengthInCm(user.calculateStepLengthInCm(height, sex));
        user.setBmr(user.calculateBmr(weight, height, age, sex));
        user.setKcalBurnedPerStep(user.calculateKcalBurnedPerStep(weight, height, user.getAvgWalkingSpeedInKmH()));
        user.setDailyKcalIntake(user.calculateDailyKcalIntake(user.getBmr(), lifestyle));
        user.setDailyCarbsIntakeInG(user.calculateDailyCarbsIntake(user.getDailyKcalIntake()));
        user.setDailyFatIntakeInG(user.calculateDailyFatIntake(user.getDailyKcalIntake()));
        user.setDailyProteinIntakeInG(user.calculateDailyProteinIntakeInG(weight));

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NotNull Realm realm) {
                realm.insertOrUpdate(user);
            }
        });
        realm.close();
    }

    private void loadData() {
        realm = Realm.getDefaultInstance();
        User u = realm.where(User.class)
                .equalTo("userId", "user")
                .findFirst();
        sex = u.getSex();
        age = u.getAgeInYears();
        height = u.getHeightInCm();
        weight = u.getWeightInKg();
        lifestyle = u.getLifestyle();
        realm.close();

        // Insert current data into fields
        sexMenu.setText(sex);
        tAge.setText(String.valueOf(age));
        tHeight.setText(String.valueOf(height));
        tWeight.setText(String.valueOf(weight));
        lifestyleMenu.setText(lifestyle);

        // Set up listeners
        List<String> sexList = new ArrayList<>(Arrays.asList("Male", "Female"));
        ArrayAdapter sexAdapter = new ArrayAdapter(this, R.layout.details_list_item, sexList);
        sexMenu.setAdapter(sexAdapter);
        sexMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sex = sexMenu.getText().toString();
            }
        });
        tAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty())
                    age = Integer.parseInt(s.toString());
            }
        });
        tHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty())
                    height = Integer.parseInt(s.toString());
            }
        });
        tWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty())
                    weight = Double.parseDouble(s.toString());
            }
        });
        lifestyleMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lifestyle = lifestyleMenu.getText().toString();
            }
        });

        List<String> lifestyleList = new ArrayList<>(Arrays.asList(
                "Sedentary",
                "Lightly active",
                "Moderately active",
                "Very active",
                "Extra active"));
        ArrayAdapter lifestyleAdapter = new ArrayAdapter(this, R.layout.details_list_item, lifestyleList);
        lifestyleMenu.setAdapter(lifestyleAdapter);
    }

}