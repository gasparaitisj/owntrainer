package com.gasparaiciukas.owntrainer.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gasparaiciukas.owntrainer.R;
import com.gasparaiciukas.owntrainer.activity.CreateMealItemActivity;
import com.gasparaiciukas.owntrainer.adapter.MealAdapter;
import com.gasparaiciukas.owntrainer.database.Meal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import io.realm.Realm;

public class MealFragment extends Fragment {

    // UI
    private TabLayout mealTabLayout;
    private RecyclerView recyclerView;
    private MealAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton fab;

    // Database
    private Realm realm;

    // Fragment
    private FragmentManager fragmentManager;

    public static MealFragment newInstance() {
        return new MealFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = requireActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_meal, container, false);
        initUi(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.reload();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close(); // close database
    }

    private void initUi(View rootView) {
        // Set up FAB
        fab = rootView.findViewById(R.id.meal_fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), CreateMealItemActivity.class);
                requireContext().startActivity(intent);
            }
        });

        // Tabs (foods or meals)
        mealTabLayout = rootView.findViewById(R.id.meal_tab_layout);
        mealTabLayout.getTabAt(1).select(); // select current tab
        mealTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.main_fragment_frame_layout, FoodFragment.newInstance());
                    transaction.commit();
                }
                else if (tab.getPosition() == 1) {
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.main_fragment_frame_layout, MealFragment.newInstance());
                    transaction.commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // do nothing
            }
        });
    }

    private void initRecyclerView(View view) {
        // Get meals from database
        realm = Realm.getDefaultInstance();
        List<Meal> meals = realm.where(Meal.class).findAll();

        // Set up recycler view
        recyclerView = view.findViewById(R.id.meal_recycler_view);
        adapter = new MealAdapter(meals);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

}