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
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
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
            Column(
                modifier = Modifier
                    .fillMaxSize() // Fill the entire parent container
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth() // Fill the entire width
                        .weight(2f)
                        .background(color = Color.Green)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth() // Fill the entire width
                        .weight(2f)
                        .background(color = Color.White)
                ) {
                    Text(
                        text = sensorViewModel.accelerometreInfo.value ?: "",
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth() // Fill the entire width
                        .weight(1f)
                        .background(color = Color.Yellow)
                )
            }
        }

        val sensorObserver =
            Observer<String> { newValue ->  }
        sensorViewModel.accelerometreInfo.observe(this, sensorObserver)

        sensorViewModel.changeAccelerometreInfo("Not detected")

        if (savedInstanceState != null) {
            color = savedInstanceState.getBoolean(
                "savedColor",
                false
            ) // retrieve saved color or use default
        }
//        view1.setBackgroundColor(if (!color) Color.GREEN else Color.RED)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // register this class as a listener for the accelerometer sensor
        lastUpdate = System.currentTimeMillis()
    }

    override fun onResume() {
        super.onResume()

        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer != null) {
//            view2.text = this.getString(R.string.shake)
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        val light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        if (light != null) {
//            view3.setBackgroundColor(Color.YELLOW)
            sensorManager.registerListener(
                this,
                light,
                SensorManager.SENSOR_DELAY_NORMAL
            )
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
            sensorViewModel.changeAccelerometreInfo("new value")
//            Toast.makeText(this, R.string.shuffed, Toast.LENGTH_SHORT).show()
            if (color) {
//                view1.setBackgroundColor(Color.GREEN)
            } else {
//                view1.setBackgroundColor(Color.RED)
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

//            view3.text = buildString {
//                append(getString(R.string.new_light_value))
//                append(" $lightLevel\n")
//
//                when {
//                    lightLevel < lowThresholdLight -> append(getString(R.string.light_intensity_low))
//                    lightLevel > highThresholdLight -> append(getString(R.string.light_intensity_high))
//                    else -> append(getString(R.string.light_intensity_medium))
//                }
//
//                append(" Intensity")
//            }

        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("savedColor", color) // save current color
    }

}
