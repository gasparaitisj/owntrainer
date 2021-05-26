package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.adapter.FoodApiAdapter
import com.gasparaiciukas.owntrainer.network.FoodApi
import com.gasparaiciukas.owntrainer.network.GetResponse
import com.gasparaiciukas.owntrainer.network.GetService
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FoodFragment : Fragment() {
    private lateinit var getResponse: GetResponse
    private lateinit var getService: GetService
    private lateinit var adapter: FoodApiAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var supportFragmentManager: FragmentManager
    private var foodsApi: List<FoodApi> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get request
        val retrofitGet = Retrofit.Builder()
                .baseUrl(GetService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        getService = retrofitGet.create(GetService::class.java)

        // Get fragment manager from activity
        supportFragmentManager = requireActivity().supportFragmentManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_food, container, false)
        initUi(rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView(view)
    }

    private fun initRecyclerView(view: View) {
        // Set up recycler view
        recyclerView = view.findViewById(R.id.food_recycler_view)
        val layoutManager = LinearLayoutManager(context)
        adapter = FoodApiAdapter(foodsApi)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    private fun sendGet(query: String) {
        foodsApi = arrayListOf()
        val call = getService.getResponse(APP_ID, APP_KEY, query)
        call.enqueue(object : Callback<GetResponse?> {
            override fun onResponse(call: Call<GetResponse?>, response: Response<GetResponse?>) {
                getResponse = response.body()!!
                val hints = getResponse.hints
                Log.d("testGet", "Sent: " + getResponse.text)
                for (i in hints!!.indices) {
                    (foodsApi as ArrayList<FoodApi>).add(hints[i].foodApi!!)
                }
                Log.d("testGet", foodsApi[0].label)
                adapter.reload(foodsApi)
            }
            override fun onFailure(call: Call<GetResponse?>, t: Throwable) {
                Toast.makeText(context, "Search request failed!", Toast.LENGTH_LONG).show()
                Log.d("testGet", t.toString())
            }
        })
    }

    private fun initUi(rootView: View) {
        // Set up food search input field
        val foodInputLayout = rootView.findViewById<TextInputLayout>(R.id.food_input_layout)
        val foodInputText = rootView.findViewById<TextInputEditText>(R.id.food_input_text)
        val foodTabLayout = rootView.findViewById<TabLayout>(R.id.food_tab_layout)

        // Send get request on end icon clicked
        foodInputLayout.setEndIconOnClickListener { v ->
            if (!TextUtils.isEmpty(foodInputText.text)) {
                Toast.makeText(v.context, foodInputText.text.toString(), Toast.LENGTH_SHORT).show()
                sendGet(foodInputText.text.toString())
            } else {
                Toast.makeText(v.context, "Search query is empty!", Toast.LENGTH_SHORT).show()
            }
        }

        // Also send get request on keyboard search button clicked
        foodInputText.setOnEditorActionListener { v, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (!TextUtils.isEmpty(foodInputText.text)) {
                    Toast.makeText(v.context, foodInputText.text.toString(), Toast.LENGTH_SHORT).show()
                    sendGet(foodInputText.text.toString())
                } else {
                    Toast.makeText(v.context, "Search query is empty!", Toast.LENGTH_SHORT).show()
                }
                handled = true
            }
            handled
        }

        // Tabs (foods or meals)
        foodTabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.main_fragment_frame_layout, newInstance())
                    transaction.commit()
                } else if (tab.position == 1) {
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.main_fragment_frame_layout, MealFragment.newInstance())
                    transaction.commit()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    companion object {
        fun newInstance(): FoodFragment {
            return FoodFragment()
        }

        private const val APP_KEY = "30a0c47f24999903266d0171d50b7aa6"
        private const val APP_ID = "0de8a357"
    }
}