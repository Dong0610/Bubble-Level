package dong.duan.bubblelevel

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import dong.duan.bubblelevel.databinding.ActivityMainBinding

import dong.duan.bubblelevel.widget.CricleView
import dong.duan.bubblelevel.widget.RectangleVeritcalView
import dong.duan.bubblelevel.widget.RectangleView

class MainActivity : AppCompatActivity(), SensorEventListener {

    lateinit var binding: ActivityMainBinding
    private lateinit var cricleViewDemo: CricleView
    private lateinit var rectangleView: RectangleView

    private lateinit var rectangleView2: RectangleVeritcalView

    private var sensorManager: SensorManager? = null

    private var mSensor: Sensor? = null
    private var aSensor: Sensor? = null
    private var rSensor: Sensor? = null
    private var gSensor: Sensor? = null

    private val rotationMatrixR = FloatArray(9)
    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)
    private var orientation = FloatArray(3)

    private var accelerometerPrevTime: Long = 0
    private val alpha = 0.96f
    private val updateInterval = 10 //mills

    private lateinit var txtX: TextView
    private lateinit var txtY: TextView
    var withCricle=0
    var rectangleWith= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cricleViewDemo= findViewById(R.id.acm_view)
        rectangleView= findViewById(R.id.rectangles)
        rectangleView2= findViewById(R.id.rectangles2)
        txtX= findViewById(R.id.tv_x)
        txtY= findViewById(R.id.tv_y)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        mSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        aSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        rSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        gSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GRAVITY)

        Toast.makeText(applicationContext, "$withCricle -- $rectangleWith", Toast.LENGTH_SHORT).show()

    }

    override fun onSensorChanged(event: SensorEvent?) {

        synchronized(this) {
            val time = System.currentTimeMillis()

            event?.let { mEvent ->
                if (mEvent.sensor.type == Sensor.TYPE_GRAVITY) {
                    if (gSensor != null) {
                        gravity[0] = mEvent.values[0]
                        gravity[1] = mEvent.values[1]
                        gravity[2] = mEvent.values[2]
                        updateOrientation(time)
                    }
                }


                if (mEvent.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    if (mEvent.sensor.type == Sensor.TYPE_GRAVITY) {
                        if (gSensor == null) {
                            gravity[0] = this.alpha * gravity[0] + (1 - this.alpha) * mEvent.values[0]
                            gravity[1] = this.alpha * gravity[1] + (1 - this.alpha) * mEvent.values[1]
                            gravity[2] = this.alpha * gravity[2] + (1 - this.alpha) * mEvent.values[2]
                            updateOrientation(time)
                        }
                    }

                }

                if (mEvent.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {

                    geomagnetic[0] = this.alpha * geomagnetic[0] + (1 - this.alpha) * mEvent.values[0]
                    geomagnetic[1] = this.alpha * geomagnetic[1] + (1 - this.alpha) * mEvent.values[1]
                    geomagnetic[2] = this.alpha * geomagnetic[2] + (1 - this.alpha) * mEvent.values[2]


                }
            }

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun startSensors() {
        mSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        aSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        rSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        gSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GRAVITY)
        if (mSensor == null || aSensor == null) {
            notSupported()
            return
        } else {
            sensorManager?.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL)
            sensorManager?.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_NORMAL)
            sensorManager?.registerListener(this, rSensor, SensorManager.SENSOR_DELAY_NORMAL)
            sensorManager?.registerListener(this, gSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

    }

    private fun updateOrientation(time: Long) {
        if (SensorManager.getRotationMatrix(rotationMatrixR, null, gravity, geomagnetic)) {
            orientation = SensorManager.getOrientation(rotationMatrixR, orientation)
            if (time - accelerometerPrevTime > updateInterval) {
                Math.toDegrees(orientation[0].toDouble()).toFloat()  //azimuth
                Math.toDegrees(orientation[1].toDouble()).toFloat()  //pitch
                Math.toDegrees(orientation[2].toDouble()).toFloat() //roll
                accelerometerPrevTime = time



                var tisoW=0f
//                if(withCricle>rectangleWith){
//                    tisoW=  (withCricle/rectangleWith).toFloat()
//                }
//                else{
//                    tisoW= (rectangleWith/withCricle).toFloat()
//                }


                cricleViewDemo.updateOrientation(
                    Math.toDegrees(orientation[1].toDouble()).toFloat(),
                    Math.toDegrees(orientation[2].toDouble()).toFloat()
                )
                rectangleView.updateOrientation(
                    Math.toDegrees(orientation[1].toDouble()).toFloat(),
                    Math.toDegrees(orientation[2].toDouble()).toFloat()
                )

                rectangleView2.updateOrientation(
                    Math.toDegrees(orientation[1].toDouble()).toFloat(),
                    Math.toDegrees(orientation[2].toDouble()).toFloat()
                )

                txtX.setText("X: "+ Math.toDegrees(orientation[1].toDouble()).toString().replace("-","").substring(0,5))
                txtY.setText("Y: "+Math.toDegrees(orientation[2].toDouble()).toString().replace("-","").substring(0,5))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startSensors()
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    private fun notSupported() {
        Toast.makeText(this, "Sensors Not Supported in this device", Toast.LENGTH_SHORT).show()
    }

}