package com.gasparaiciukas.owntrainer.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.gasparaiciukas.owntrainer.adapter.DatabaseFoodAdapter
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject

class MainFragmentFactory @Inject constructor(
    private val mealAdapter: MealAdapter,
    private val databaseFoodAdapter: DatabaseFoodAdapter
): FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className) {
            AddFoodToMealFragment::class.java.name -> AddFoodToMealFragment(mealAdapter)
            AddMealToDiaryFragment::class.java.name -> AddMealToDiaryFragment(mealAdapter)
            DiaryFragment::class.java.name -> DiaryFragment(mealAdapter)
            MealFragment::class.java.name -> MealFragment(mealAdapter)
            MealItemFragment::class.java.name -> MealItemFragment(databaseFoodAdapter)
            else -> super.instantiate(classLoader, className)
        }
    }
}
