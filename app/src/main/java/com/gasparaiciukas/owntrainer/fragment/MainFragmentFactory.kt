package com.gasparaiciukas.owntrainer.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.gasparaiciukas.owntrainer.adapter.DatabaseFoodAdapter
import com.gasparaiciukas.owntrainer.adapter.MealAdapter
import com.gasparaiciukas.owntrainer.adapter.NetworkFoodAdapter
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class MainFragmentFactory @Inject constructor() : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className) {
            AddFoodToMealFragment::class.java.name -> AddFoodToMealFragment()
            AddMealToDiaryFragment::class.java.name -> AddMealToDiaryFragment()
            DiaryFragment::class.java.name -> DiaryFragment()
            FoodFragment::class.java.name -> FoodFragment()
            MealFragment::class.java.name -> MealFragment()
            MealItemFragment::class.java.name -> MealItemFragment()
            else -> super.instantiate(classLoader, className)
        }
    }
}
