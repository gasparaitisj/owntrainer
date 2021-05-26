package com.gasparaiciukas.owntrainer.fragment

import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.activity.CreateMealItemActivity
import com.gasparaiciukas.owntrainer.database.Meal
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import io.realm.Realm

class MealFragment : Fragment() {
    // UI
    private lateinit var adapter: MealAdapter

    // Database
    private lateinit var realm: Realm

    // Fragment
    private lateinit var supportFragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager = requireActivity().supportFragmentManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_meal, container, false)
        initUi(rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView(view)
    }

    override fun onResume() {
        super.onResume()
        adapter.reload()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close() // close database
    }

    private fun initUi(rootView: View) {
        // Set up FAB
        val fab = rootView.findViewById<FloatingActionButton>(R.id.meal_fab_add)
        fab.setOnClickListener {
            val intent = Intent(requireContext(), CreateMealItemActivity::class.java)
            requireContext().startActivity(intent)
        }

        // Tabs (foods or meals)
        val mealTabLayout = rootView.findViewById<TabLayout>(R.id.meal_tab_layout)
        mealTabLayout.getTabAt(1)?.select() // select current tab
        mealTabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.main_fragment_frame_layout, FoodFragment.newInstance())
                    transaction.commit()
                } else if (tab.position == 1) {
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.main_fragment_frame_layout, newInstance())
                    transaction.commit()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // do nothing
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // do nothing
            }
        })
    }

    private fun initRecyclerView(view: View) {
        // Get meals from database
        realm = Realm.getDefaultInstance()
        val meals: List<Meal> = realm.where(Meal::class.java).findAll()

        // Set up recycler view
        val recyclerView = view.findViewById<RecyclerView>(R.id.meal_recycler_view)
        val layoutManager = LinearLayoutManager(context)
        adapter = MealAdapter(meals)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    companion object {
        fun newInstance(): MealFragment {
            return MealFragment()
        }
    }
}