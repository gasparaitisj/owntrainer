package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.DiaryEntry
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.databinding.FragmentAddMealToDiaryBinding
import io.realm.Realm

class AddMealToDiaryFragment : Fragment() {
    private var _binding: FragmentAddMealToDiaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MealAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var primaryKey: String
    private lateinit var realm: Realm
    private lateinit var meals: List<Meal>
    private val listener: (meal: Meal, position: Int) -> Unit = { meal: Meal, _: Int ->
        val realm = Realm.getDefaultInstance()
        val diaryEntry = realm.where(DiaryEntry::class.java)
            .equalTo("yearAndDayOfYear", primaryKey)
            .findFirst()
        if (diaryEntry != null) {
            val mealList = diaryEntry.meals
            realm.executeTransaction {
                mealList.add(meal)
                diaryEntry.meals = mealList
            }
        }
        realm.close()
        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddMealToDiaryBinding.inflate(inflater, container, false)

        primaryKey = requireArguments().getString("primaryKey").toString()

        realm = Realm.getDefaultInstance()
        meals = realm.where(Meal::class.java).findAll()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MealAdapter(meals, listener)
        layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = layoutManager
    }

    override fun onResume() {
        super.onResume()
        adapter.reload()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        realm.close()
        _binding = null
    }
}