package com.dipitvasdev.FoodRush.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
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

lateinit var OTP: EditText
lateinit var NewPassword: EditText
lateinit var ConfirmPasswordForgot: EditText
lateinit var forgot_password_Progress: RelativeLayout
lateinit var cnf: Button
lateinit var sharedPref: SharedPreferences
lateinit var x: String

class OtpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        OTP = findViewById(R.id.editTextOTP)
        NewPassword = findViewById(R.id.etNewPassword)
        ConfirmPasswordForgot = findViewById(R.id.etConfirmPasswordForgot)
        forgot_password_Progress = findViewById(R.id.forgot_Progress)
        cnf = findViewById(R.id.buttonSubmit)
        ////sharedPref =
        //getSharedPreferences(getString(R.string.shared2), Context.MODE_PRIVATE)
        if (intent != null) {
            x = intent.getStringExtra("mobile")
        }

        println("checkme x")
        cnf.setOnClickListener {

            if (OTP.text.isBlank()) {
                OTP.setError("OTP Missing")
            } else {
                if (NewPassword.text.isBlank()) {
                    NewPassword.setError("Password Missing")
                } else {
                    if (ConfirmPasswordForgot.text.isBlank()) {
                        ConfirmPasswordForgot.setError("Re Enter Password")
                    } else {
                        if ((NewPassword.text.toString()
                                    == ConfirmPasswordForgot.text.toString())
                        ) {
                            if (ConnectionManager().checkConnectivity(this@OtpActivity)) {
                                try {
                                    val otp = OTP.text.toString()
                                    val pass = NewPassword.text.toString()
                                    val jsonParam = JSONObject()

                                    jsonParam.put("mobile_number", x)
                                    jsonParam.put("password", pass)
                                    jsonParam.put("otp", otp)
                                    val queue = Volley.newRequestQueue(this@OtpActivity)
                                    val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                                    val jsonRequest = object : JsonObjectRequest(
                                        Request.Method.POST,
                                        url,
                                        jsonParam,
                                        Response.Listener {
                                            val res = it.getJSONObject("data")
                                            val success: Boolean = res.getBoolean("success")
                                            if (success) {
                                                val sm = res.getString("successMessage")
                                                Toast.makeText(
                                                    this@OtpActivity,
                                                    sm.toString(),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                cnf.visibility = View.INVISIBLE
                                                forgot_password_Progress.visibility = View.VISIBLE
                                                val intent =
                                                    Intent(
                                                        this@OtpActivity,
                                                        LoginPageActivity::class.java
                                                    )
                                                startActivity(intent)

                                            } else {
                                                val responseMessageServer =
                                                    res.getString("errorMessage")
                                                Toast.makeText(
                                                    this@OtpActivity,
                                                    responseMessageServer.toString(),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }, Response.ErrorListener {
                                            forgot_password_Progress.visibility = View.INVISIBLE
                                            Toast.makeText(
                                                this@OtpActivity,
                                                "Some Error occurred!!! + $it",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        }

                                    ) {
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
                                        this@OtpActivity,
                                        "Some unexpected error occured!!!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                            } else {
                                val alterDialog =
                                    androidx.appcompat.app.AlertDialog.Builder(this@OtpActivity)
                                alterDialog.setTitle("No Internet")
                                alterDialog.setMessage("Internet Connection can't be establish!")
                                alterDialog.setPositiveButton("Open Settings") { text, listener ->
                                    val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                                    startActivity(settingsIntent)
                                }

                                alterDialog.setNegativeButton("Exit") { text, listener ->
                                    ActivityCompat.finishAffinity(this@OtpActivity)
                                }
                                alterDialog.setCancelable(false)

                                alterDialog.create()
                                alterDialog.show()

                            }
                        }

                    }
                }
            }
        }

    }
}