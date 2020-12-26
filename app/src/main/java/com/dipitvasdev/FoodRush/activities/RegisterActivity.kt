package com.dipitvasdev.FoodRush.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.dipitvasdev.FoodRush.R
import com.dipitvasdev.FoodRush.utils.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

lateinit var editTextName: EditText
lateinit var editTextEmail: EditText
lateinit var editTextMobileNumber: EditText
lateinit var editTextDeliveryAddress: EditText
lateinit var editTextPassword: EditText
lateinit var editTextConfirmPassword: EditText
lateinit var buttonRegister: Button
lateinit var toolbar2: androidx.appcompat.widget.Toolbar
lateinit var register_Progress: RelativeLayout
lateinit var sharedPreferences: SharedPreferences

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        editTextName = findViewById(R.id.etName)
        editTextEmail = findViewById(R.id.etEmail)
        editTextMobileNumber = findViewById(R.id.etMobile)
        editTextDeliveryAddress = findViewById(R.id.etDelivery)
        editTextPassword = findViewById(R.id.etPass)
        editTextConfirmPassword = findViewById(R.id.etConfirmPassword)
        buttonRegister = findViewById(R.id.btnRegister)
        toolbar2 = findViewById(R.id.toolBar)
        register_Progress = findViewById(R.id.register_Progress)
        (this@RegisterActivity as AppCompatActivity).setSupportActionBar(toolbar2)
        (this@RegisterActivity as AppCompatActivity).supportActionBar?.title = "Register Yourself"
        (this@RegisterActivity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true)
        (this@RegisterActivity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
            true
        )
        (this@RegisterActivity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        sharedPreferences =
            getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)
        buttonRegister.setOnClickListener {
            if (check()) {
                if (ConnectionManager().checkConnectivity(this@RegisterActivity)) {
                    register_Progress.visibility = View.VISIBLE
                    buttonRegister.visibility = View.INVISIBLE
                    try {
                        val Namet = editTextName.text.toString()
                        val Mobile = editTextMobileNumber.text.toString()
                        val pass = editTextPassword.text.toString()
                        val a = editTextDeliveryAddress.text.toString()
                        val em = editTextEmail.text.toString()
                        val queue = Volley.newRequestQueue(this@RegisterActivity)
                        val url = "http://13.235.250.119/v2/register/fetch_result"
                        val jsonparams = JSONObject()
                        jsonparams.put("name", Namet)
                        jsonparams.put("mobile_number", Mobile)
                        jsonparams.put("password", pass)
                        jsonparams.put("address", a)
                        jsonparams.put("email", em)
                        val jsonRequest = object :
                            JsonObjectRequest(
                                Request.Method.POST, url, jsonparams,
                                Response.Listener {
                                    val res = it.getJSONObject("data")
                                    val success: Boolean = res.getBoolean("success")
                                    if (success) {
                                        val regJSONObject = res.getJSONObject("data")
                                        sharedPreferences.edit().putString(
                                            "user_id",
                                            regJSONObject.getString("user_id")
                                        ).apply()
                                        sharedPreferences.edit()
                                            .putString("user_name", regJSONObject.getString("name"))
                                            .apply()
                                        sharedPreferences.edit()
                                            .putString("email", regJSONObject.getString("email"))
                                            .apply()
                                        sharedPreferences.edit().putString(
                                            "mobile",
                                            regJSONObject.getString("mobile_number")
                                        ).apply()
                                        sharedPreferences.edit().putString(
                                            "address",
                                            regJSONObject.getString("address")
                                        ).apply()
                                        Toast.makeText(
                                            this@RegisterActivity,
                                            "Registered sucessfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val intent =
                                            Intent(this@RegisterActivity, HomeActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        val responseMessageServer =
                                            res.getString("errorMessage")
                                        Toast.makeText(
                                            this@RegisterActivity,
                                            responseMessageServer.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }


                                }, Response.ErrorListener {
                                    register_Progress.visibility = View.INVISIBLE
                                    println(it)
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Some Error occurred!!! + $it",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-Type"] = "application/json"
                                headers["token"] = "ed0e68368529be"
                                return headers
                            }
                        }
                        queue.add(jsonRequest)
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Some unexpected error occured!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val alterDialog =
                        androidx.appcompat.app.AlertDialog.Builder(this@RegisterActivity)
                    alterDialog.setTitle("No Internet")
                    alterDialog.setMessage("Internet Connection can't be establish!")
                    alterDialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                        startActivity(settingsIntent)
                    }

                    alterDialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@RegisterActivity)
                    }
                    alterDialog.setCancelable(false)

                    alterDialog.create()
                    alterDialog.show()

                }

            }

        }


    }

    fun check(): Boolean {
        var err = 0
        if (editTextName.text.isBlank()) {

            editTextName.setError("Field Missing!")
        } else {
            err++
        }
        if(editTextName.text.length<=3){
            editTextName.setError("Minimum 3 Characters")
        }else{
            err++
        }
        if(editTextMobileNumber.text.length!=10){
            editTextMobileNumber.setError("Mobile Number should be 10 digit")
        }else{
            err++
        }
        if(editTextPassword.text.length<6){
            editTextPassword.setError("Min 6 characters")
        }else{
            err++
        }
        if (editTextMobileNumber.text.isBlank()) {
            editTextMobileNumber.setError("Field Missing!")
        } else {
            err++
        }
        if(!(editTextEmail.text.contains("@")&&(editTextEmail.text.contains(".")))){
            editTextEmail.setError("Invalid Email")
        }else{
            err++
        }
        if (editTextEmail.text.isBlank()) {
            editTextEmail.setError("Field Missing!")
        } else {
            err++
        }

        if (editTextDeliveryAddress.text.isBlank()) {
            editTextDeliveryAddress.setError("Field Missing!")
        } else {
            err++
        }

        if (editTextConfirmPassword.text.isBlank()) {
            editTextConfirmPassword.setError("Field Missing!")
        } else {
            err++
        }

        if (editTextPassword.text.isBlank()) {
            editTextPassword.setError("Field Missing!")
        } else {
            err++
        }

        if (editTextPassword.text.isNotBlank() && editTextConfirmPassword.text.isNotBlank()) {
            if (editTextPassword.text.toString().toInt() == editTextConfirmPassword.text.toString()
                    .toInt()
            ) {
                err++
            } else {
                editTextConfirmPassword.setError("Password don't match")
            }
        }

        return err == 11

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                super.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}