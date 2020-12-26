package com.dipitvasdev.FoodRush.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.dipitvasdev.FoodRush.R

class SpashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Handler().postDelayed({
            val start = Intent(this@SpashActivity, LoginPageActivity::class.java)
            startActivity(start)
            finishAffinity()
        }, 2000)
        title = ""
    }


}