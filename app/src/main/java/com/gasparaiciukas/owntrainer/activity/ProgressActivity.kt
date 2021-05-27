package com.gasparaiciukas.owntrainer.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.databinding.ActivityProgressBinding

class ProgressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProgressBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_progress)
    }
}