package com.gasparaiciukas.owntrainer.ui.home

import androidx.lifecycle.ViewModel
import com.gasparaiciukas.owntrainer.utils.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository
) : ViewModel() {

}
