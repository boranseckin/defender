package com.example.boo

import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var textField: TextView
    private lateinit var button: Button
    private lateinit var ledButton: Button

    private lateinit var port: UsbSerialPort

    private var state = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textField = findViewById(R.id.textField)
        button = findViewById(R.id.button)
        ledButton = findViewById(R.id.ledButton)

        button.setOnClickListener(this)
        ledButton.setOnClickListener(this)

        val manager = getSystemService(USB_SERVICE) as UsbManager
        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
        if (availableDrivers.isEmpty()) {
            state = false
            return
        }
        state = true

        // Open a connection to the first available driver.
        val driver = availableDrivers[0]
        val connection = manager.openDevice(driver.device) ?: return

        port = driver.ports[0] // Most devices have just one port (port 0)
        port.open(connection)
        port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button -> {
                textField.text = if (state) "yes driver" else "no driver"
            }
            R.id.ledButton -> {
                port.write("led".toByteArray(), 500)
            }
        }
    }
}