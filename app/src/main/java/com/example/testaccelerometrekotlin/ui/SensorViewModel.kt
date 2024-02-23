package com.example.testaccelerometrekotlin.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SensorViewModel : ViewModel() {
    val accelerometreInfo: MutableLiveData<String> by lazy {
        MutableLiveData<String>("Default")
    }

    fun changeAccelerometreInfo(new: String) {
        accelerometreInfo.value = new
    }
}