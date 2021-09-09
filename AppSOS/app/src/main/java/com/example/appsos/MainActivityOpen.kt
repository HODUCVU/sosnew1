package com.example.appsos

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appsos.getDataSOS.LocationOfMap
import com.example.appsos.getDataSOS.ReadContact

class MainActivityOpen : AppCompatActivity() {
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var location : LocationOfMap
    private lateinit var readContact: ReadContact
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_open)
        location()
        readContact = ReadContact(this)
        readContact.readContact()
        countDownTimer =
            object : CountDownTimer(3000,1000){
                override fun onTick(millisUntilFinished: Long) {
                }
                override fun onFinish() {
                    intent()
                }
            }.start()
    }
    private fun intent() {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun location() {
        location = LocationOfMap(this)
        location.init()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && (grantResults[0]
                    == PackageManager.PERMISSION_GRANTED)
        ) {
            location.init()
            readContact.readContact()
        } else {
            Toast.makeText(applicationContext, "Quyền truy cập bị từ chôi", Toast.LENGTH_SHORT).show()
        }
    }
}