package com.kartikey.foodrunner.activity


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import com.kartikey.foodrunner.R

class OrderPlacedActivity : AppCompatActivity() {

    lateinit var btnOkay: Button
    lateinit var orderPlacedSuccessfully: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)
        orderPlacedSuccessfully = findViewById(R.id.orderPlacedSuccessfully)
        btnOkay = findViewById(R.id.btnOkay)

        btnOkay.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    override fun onBackPressed() {
        Toast.makeText(
            this@OrderPlacedActivity,
            "Press OK for Main Menu",
            Toast.LENGTH_SHORT
        )
            .show()
        //force user to press okay button to take him to dashboard screen
        //user can't use back button
    }
}