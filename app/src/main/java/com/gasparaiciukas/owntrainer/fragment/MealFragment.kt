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
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.activity.CreateMealItemActivity
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.databinding.FragmentMealBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import io.realm.Realm

class MealFragment : Fragment() {
    private var _binding: FragmentMealBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MealAdapter
    private lateinit var realm: Realm
    private lateinit var supportFragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager = requireActivity().supportFragmentManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentMealBinding.inflate(inflater, container, false)
        initUi()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        adapter.reload()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close() // close database
    }

    private fun initUi() {
        // Set up FAB
        binding.fab.setOnClickListener {
            val intent = Intent(requireContext(), CreateMealItemActivity::class.java)
            requireContext().startActivity(intent)
        }

        // Tabs (foods or meals)
        binding.layoutTab.getTabAt(1)?.select() // select current tab
        binding.layoutTab.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.layout_frame_fragment, FoodFragment.newInstance())
                    transaction.commit()
                } else if (tab.position == 1) {
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.layout_frame_fragment, newInstance())
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

    private fun initRecyclerView() {
        // Get meals from database
        realm = Realm.getDefaultInstance()
        val meals: List<Meal> = realm.where(Meal::class.java).findAll()

        // Set up recycler view
        val layoutManager = LinearLayoutManager(context)
        adapter = MealAdapter(meals)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
    }

    companion object {
        fun newInstance(): MealFragment {
            return MealFragment()
        }
    }
}