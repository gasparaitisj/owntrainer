package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.database.User
import com.google.android.material.textfield.TextInputEditText
import io.realm.Realm
import java.util.*

class IntroDetailsFragment : Fragment() {
    private var sex = "Male"
    private var age = 25
    private var height = 180
    private var weight = 80.0
    private var lifestyle = "Lightly active"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_intro_details, container, false)
        initUi(rootView)
        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
        writeUserToDatabase()
    }

    private fun initUi(rootView: View) {
        val sexMenu = rootView.findViewById<AutoCompleteTextView>(R.id.intro_details_sex_menu)
        val tAge = rootView.findViewById<TextInputEditText>(R.id.intro_details_age_text)
        val tHeight = rootView.findViewById<TextInputEditText>(R.id.intro_details_height_text)
        val tWeight = rootView.findViewById<TextInputEditText>(R.id.intro_details_weight_text)
        val lifestyleMenu = rootView.findViewById<AutoCompleteTextView>(R.id.intro_details_lifestyle_menu)

        // Sex menu input listener
        val sexList: List<String> = ArrayList(listOf("Male", "Female"))
        val sexAdapter: ArrayAdapter<*> = ArrayAdapter<Any>(requireContext(), R.layout.details_list_item, sexList)
        sexMenu.setAdapter(sexAdapter)
        sexMenu.onItemClickListener = OnItemClickListener { _, _, _, _ -> sex = sexMenu.text.toString() }

        // Age field text listener
        tAge.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // do nothing
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty()) age = s.toString().toInt()
            }
        })

        // Height field text listener
        tHeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // do nothing
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty()) height = s.toString().toInt()
            }
        })

        // Weight field text listener
        tWeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // do nothing
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty()) weight = s.toString().toDouble()
            }
        })

        // Lifestyle menu input listener
        lifestyleMenu.onItemClickListener = OnItemClickListener { _, _, _, _ -> lifestyle = lifestyleMenu.text.toString() }
        val lifestyleList: List<String> = ArrayList(listOf(
                "Sedentary",
                "Lightly active",
                "Moderately active",
                "Very active",
                "Extra active"))
        val lifestyleAdapter: ArrayAdapter<*> = ArrayAdapter<Any>(requireContext(), R.layout.details_list_item, lifestyleList)
        lifestyleMenu.setAdapter(lifestyleAdapter)
    }

    private fun writeUserToDatabase() {
        val realmInstance = Realm.getDefaultInstance()
        val user = User()

        user.userId = "user"
        user.ageInYears = age
        user.sex = sex
        user.heightInCm = height
        user.weightInKg = weight
        user.lifestyle = lifestyle
        user.stepLengthInCm = user.calculateStepLengthInCm(height.toDouble(), sex)
        user.bmr = user.calculateBmr(weight, height.toDouble(), age, sex)
        user.kcalBurnedPerStep = user.calculateKcalBurnedPerStep(
                weight,
                height.toDouble(),
                user.avgWalkingSpeedInKmH)
        user.dailyKcalIntake = user.calculateDailyKcalIntake(user.bmr, lifestyle)
        user.dailyCarbsIntakeInG = user.calculateDailyCarbsIntake(user.dailyKcalIntake)
        user.dailyFatIntakeInG = user.calculateDailyFatIntake(user.dailyKcalIntake)
        user.dailyProteinIntakeInG = user.calculateDailyProteinIntakeInG(weight)

        realmInstance.executeTransaction { realm -> realm.insertOrUpdate(user) }
        realmInstance.close()
    }

    companion object {
        fun newInstance(): IntroDetailsFragment {
            return IntroDetailsFragment()
        }
    }
}