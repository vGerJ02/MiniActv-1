package com.example.testaccelerometrekotlin.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SensorViewModel : ViewModel() {

    val accelerometreInfo: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val lightInfo: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val boxColor: MutableLiveData<Color> by lazy {
        MutableLiveData<Color>(Color.Green)
    }

    fun changeLightInfo(newValue: String) {
        lightInfo.value = lightInfo.value + "\n" + newValue
    }

    fun changeAccelerometreInfo(newValue: String) {
        accelerometreInfo.value = newValue
    }

    fun setBoxColor(newValue: Color) {
        boxColor.value = newValue
    }
}