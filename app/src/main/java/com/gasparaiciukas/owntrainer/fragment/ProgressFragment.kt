package com.gasparaiciukas.owntrainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gasparaiciukas.owntrainer.R

class ProgressFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_progress, container, false)
    }

    companion object {
        fun newInstance(): ProgressFragment {
            return ProgressFragment()
        }
    }
}