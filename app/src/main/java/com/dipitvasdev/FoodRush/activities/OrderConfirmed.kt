package com.dipitvasdev.FoodRush.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.dipitvasdev.FoodRush.R

lateinit var buttonOkay: Button


class OrderConfirmed : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmed)
        buttonOkay = findViewById(R.id.btnOk)

        buttonOkay.setOnClickListener(View.OnClickListener {

            val intent = Intent(this, HomeActivity::class.java)

            startActivity(intent)

            finishAffinity()
        })
    }
}