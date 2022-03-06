package com.gasparaiciukas.owntrainer.viewmodel

import com.gasparaiciukas.owntrainer.repository.FakeDiaryRepository
import com.gasparaiciukas.owntrainer.repository.FakeUserRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DiaryViewModelTest {

    private lateinit var viewModel: DiaryViewModel

    @Before
    fun setup() {
        viewModel = DiaryViewModel(FakeUserRepository(), FakeDiaryRepository())
    }

//    @Test
//    fun `insert `

}