package com.example.testaccelerometrekotlin.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SensorViewModel : ViewModel() {

    private val _accelerometreInfo = MutableStateFlow<String>("")
    val accelerometreInfo : StateFlow<String> = _accelerometreInfo.asStateFlow()

    private val _lightInfo = MutableStateFlow<String>("")
    val lightInfo : StateFlow<String> = _lightInfo.asStateFlow()

    private val _boxColor = MutableStateFlow<Color>(Color.Green)
    val boxColor : StateFlow<Color> = _boxColor.asStateFlow()

    fun changeLightInfo(newValue: String){
        _lightInfo.value = _lightInfo.value + "\n" + newValue
    }
    fun changeAccelerometreInfo(newValue: String){
        _accelerometreInfo.value = newValue
    }

    fun setBoxColor(newValue: Color){
        _boxColor.value = newValue
    }
}