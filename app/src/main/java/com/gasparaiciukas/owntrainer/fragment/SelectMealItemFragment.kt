package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.database.Food
import com.gasparaiciukas.owntrainer.database.Meal
import com.gasparaiciukas.owntrainer.databinding.FragmentSelectMealItemBinding
import com.gasparaiciukas.owntrainer.network.FoodApi
import io.realm.Realm

class SelectMealItemFragment : Fragment() {
    private var _binding: FragmentSelectMealItemBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MealAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var position = 0
    private var quantity = 0.0
    private lateinit var meals: List<Meal>
    private lateinit var realm: Realm
    private lateinit var foodItem: FoodApi
    private val listener: (meal: Meal, position: Int) -> Unit = { meal: Meal, _: Int ->
        val foodList = meal.foodList
        val food = Food()
        food.title = foodItem.label
        food.caloriesPer100G = foodItem.nutrients.calories
        food.carbsPer100G = foodItem.nutrients.carbs
        food.fatPer100G = foodItem.nutrients.fat
        food.proteinPer100G = foodItem.nutrients.protein
        food.quantityInG = quantity
        food.calories = food.calculateCalories(food.caloriesPer100G, food.quantityInG)
        food.carbs = food.calculateCarbs(food.carbsPer100G, food.quantityInG)
        food.fat = food.calculateFat(food.fatPer100G, food.quantityInG)
        food.protein = food.calculateProtein(food.proteinPer100G, food.quantityInG)

        // Write to database
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            foodList.add(food)
            meal.foodList = foodList
        }
        realm.close()
        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectMealItemBinding.inflate(inflater, container, false)

        foodItem = requireArguments().getParcelable("foodItem")!!
        position = requireArguments().getInt("position", 0)
        quantity = requireArguments().getInt("quantity", 0).toDouble()

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
        _binding = null
    }
}