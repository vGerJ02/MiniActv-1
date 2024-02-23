package com.example.testaccelerometrekotlin


import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.testaccelerometrekotlin.ui.SensorViewModel
import kotlin.math.abs


class TestAccelerometreActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var color: Boolean = false
    private var maxLight: Float = 0F
    private var oldLightLevel: Float = 0F
    private var lastUpdate: Long = 0


    private val sensorViewModel: SensorViewModel by viewModels()


    /** Called when the activity is first created.  */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UserInterface()
        }


        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer != null) {
            sensorViewModel.changeAccelerometreInfo(getString(R.string.shake))
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        } else {
            sensorViewModel.changeAccelerometreInfo(getString(R.string.no_accel))
        }

        val light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        if (light != null) {

            sensorManager.registerListener(
                this,
                light,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        // register this class as a listener for the accelerometer sensor
        lastUpdate = System.currentTimeMillis()

    }

    @Preview
    @Composable
    fun UserInterface() {
        Column(
            modifier = Modifier.fillMaxSize()

        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth() // Fill the entire width
                    .weight(2f)
                    .background(color = sensorViewModel.boxColor.observeAsState(Color.Green).value)
            ) {
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth() // Fill the entire width
                    .weight(2f)
                    .background(color = Color.White)
            ) {
                Text(sensorViewModel.accelerometreInfo.observeAsState("").value)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth() // Fill the entire width
                    .weight(1f)
                    .background(color = Color.Yellow)
            ) {
                Text(
                    sensorViewModel.lightInfo.observeAsState("").value,
                    modifier = Modifier.verticalScroll(rememberScrollState())
                )

            }
        }
    }


    override fun onPause() {
        super.onPause()
        // This will unregister all listeners registered on `this`
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER ->
                getAccelerometer(event)

            Sensor.TYPE_LIGHT ->
                getLight(event)
        }
    }

    private fun getAccelerometer(event: SensorEvent) {
        val values = event.values
        // Movement
        val x = values[0]
        val y = values[1]
        val z = values[2]
        val accelerationSquareRoot = (x * x + y * y + z * z
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH))
        val actualTime = System.currentTimeMillis()
        if (accelerationSquareRoot >= 200) {
            if (actualTime - lastUpdate < 1000) {
                return
            }
            lastUpdate = actualTime

//            Toast.makeText(this, R.string.shuffed, Toast.LENGTH_SHORT).show()
            if (color) {
                sensorViewModel.setBoxColor(Color.Green)
            } else {
                sensorViewModel.setBoxColor(Color.Red)
            }
            color = !color
        }
    }

    private fun getLight(event: SensorEvent) {
        val actualTime = System.currentTimeMillis()
        if (actualTime - lastUpdate < 1000) return

        maxLight = event.sensor.maximumRange
        val lightLevel = event.values[0]
        val lowThresholdLight = maxLight / 3
        val highThresholdLight = maxLight * 2 / 3

        if (abs(lightLevel - oldLightLevel) >= 200) {
            oldLightLevel = lightLevel


            val newText = buildString {
                append(getString(R.string.new_light_value))
                append(" $lightLevel\n")

                when {
                    lightLevel < lowThresholdLight -> append(getString(R.string.light_intensity_low))
                    lightLevel > highThresholdLight -> append(getString(R.string.light_intensity_high))
                    else -> append(getString(R.string.light_intensity_medium))
                }

                append(" Intensity")
            }
            sensorViewModel.changeLightInfo(newText)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }


}
