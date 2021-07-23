package com.example.boo

import android.hardware.usb.UsbManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var textField: TextView
    private lateinit var refreshButton: Button
    private lateinit var ledButton: Button

    private lateinit var port: UsbSerialPort

    private var state = false

    private fun connect() {
        val manager = getSystemService(USB_SERVICE) as UsbManager
        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
        if (availableDrivers.isEmpty()) {
            state = false
            Toast.makeText(applicationContext,"No driver found!", Toast.LENGTH_SHORT).show()
            return
        }
        state = true

        // Open a connection to the first available driver.
        val driver = availableDrivers[0]
        val connection = manager.openDevice(driver.device) ?: return

        if (driver.ports.size < 1) {
            Toast.makeText(applicationContext,"No device found!", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            port = driver.ports[0] // Most devices have just one port (port 0)
            port.open(connection)
            port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
        } catch (e: Exception) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            state = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textField = findViewById(R.id.textField)
        refreshButton = findViewById(R.id.refreshButton)
        ledButton = findViewById(R.id.ledButton)

        refreshButton.setOnClickListener(this)
        ledButton.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        connect()
        updateUI()
    }

    private fun updateUI() {
        textField.text = if (state) "yes driver" else "no driver"
        ledButton.isEnabled = state
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.refreshButton -> {
                connect()
                updateUI()
            }
            R.id.ledButton -> {
                try {
                    port.write("led".toByteArray(), 500)
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}