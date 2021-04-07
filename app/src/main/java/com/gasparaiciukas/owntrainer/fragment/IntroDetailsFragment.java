package com.gasparaiciukas.owntrainer.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.fragment.app.Fragment;

import com.gasparaiciukas.owntrainer.R;
import com.gasparaiciukas.owntrainer.database.User;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;

public class IntroDetailsFragment extends Fragment {

    // Variables
    private String sex = "Male";
    private int age = 25;
    private int height = 180;
    private double weight = 80;
    private String lifestyle = "Lightly active";

    // Text views
    private AutoCompleteTextView sexMenu;
    private TextInputEditText tAge;
    private TextInputEditText tHeight;
    private TextInputEditText tWeight;
    private AutoCompleteTextView lifestyleMenu;

    // Database
    private Realm realm;

    public static IntroDetailsFragment newInstance() {
        return new IntroDetailsFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_intro_details, container, false);
        initUi(rootView);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        writeUserToDatabase();
    }

    private void initUi(View rootView) {
        // Get views
        sexMenu = rootView.findViewById(R.id.intro_details_sex_menu);
        tAge = rootView.findViewById(R.id.intro_details_age_text);
        tHeight = rootView.findViewById(R.id.intro_details_height_text);
        tWeight = rootView.findViewById(R.id.intro_details_weight_text);
        lifestyleMenu = rootView.findViewById(R.id.intro_details_lifestyle_menu);

        // Sex menu input listener
        List<String> sexList = new ArrayList<>(Arrays.asList("Male", "Female"));
        ArrayAdapter sexAdapter = new ArrayAdapter(requireContext(), R.layout.details_list_item, sexList);
        sexMenu.setAdapter(sexAdapter);
        sexMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sex = sexMenu.getText().toString();
            }
        });

        // Age field text listener
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

        // Height field text listener
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

        // Weight field text listener
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

        // Lifestyle menu input listener
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
        ArrayAdapter lifestyleAdapter = new ArrayAdapter(requireContext(), R.layout.details_list_item, lifestyleList);
        lifestyleMenu.setAdapter(lifestyleAdapter);
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
}