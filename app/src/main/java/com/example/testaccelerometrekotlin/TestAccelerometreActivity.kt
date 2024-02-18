package com.example.testaccelerometrekotlin


import android.app.Activity
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import com.example.testaccelerometrekotlin.databinding.MainBinding
import kotlin.math.abs


class TestAccelerometreActivity : Activity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var color: Boolean = false
    private var maxLight: Float = 0F
    private var oldLightLevel: Float = 0F
    private var lastUpdate: Long = 0
    private lateinit var view1: TextView
    private lateinit var view2: TextView
    private lateinit var view3: TextView

    private lateinit var binding: MainBinding

    /** Called when the activity is first created.  */

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        view1 = binding.textView
        view2 = binding.textView2
        view3 = binding.textView3

        view1.setBackgroundColor(Color.GREEN)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer != null) {
            view2.text = this.getString(R.string.shake)
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        val light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        if (light != null) {
            view3.setBackgroundColor(Color.YELLOW)
            sensorManager.registerListener(
                this,
                light,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        // register this class as a listener for the accelerometer sensor
        lastUpdate = System.currentTimeMillis()
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
                view1.setBackgroundColor(Color.GREEN)
            } else {
                view1.setBackgroundColor(Color.RED)
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

            view3.text = buildString {
                append(getString(R.string.new_light_value))
                append(" $lightLevel\n")

                when {
                    lightLevel < lowThresholdLight -> append(getString(R.string.light_intensity_low))
                    lightLevel > highThresholdLight -> append(getString(R.string.light_intensity_high))
                    else -> append(getString(R.string.light_intensity_medium))
                }

                append(" Intensity")
            }

        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onPause() {
        // unregister listener
        super.onPause()
        // This will unregister all listeners registered on `this`
        sensorManager.unregisterListener(this)
    }
}
