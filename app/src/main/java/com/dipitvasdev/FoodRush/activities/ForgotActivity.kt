package com.dipitvasdev.FoodRush.activities

import android.content.Context
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

lateinit var etMobile: EditText
lateinit var etMail: EditText
lateinit var nxtBtn: Button
lateinit var progress: RelativeLayout
lateinit var sharedPreference: SharedPreferences

class ForgotActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
        etMobile = findViewById(R.id.etMobile)
        etMail = findViewById(R.id.etEmail)
        progress = findViewById(R.id.forgot_Progress)
        nxtBtn = findViewById(R.id.btnLogIn)
        nxtBtn.setOnClickListener {

            if (etMobile.text.isBlank()) {
                etMobile.setError("Mobile Number Missing")
            } else {
                if (etMail.text.isBlank()) {
                    etMail.setError("Email Missing")
                } else {
                    if (ConnectionManager().checkConnectivity(this@ForgotActivity)) {
                        try {
                            sharedPreference = getSharedPreferences(
                                getString(R.string.shared2),
                                Context.MODE_PRIVATE
                            )
                            val em = etMobile.text.toString()
                            val ema = etMail.text.toString()
                            val jsonParam = JSONObject()
                            jsonParam.put("mobile_number", em)
                            jsonParam.put("email", ema)
                            val queue = Volley.newRequestQueue(this@ForgotActivity)
                            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
                            nxtBtn.visibility = View.INVISIBLE
                            progress.visibility = View.VISIBLE
                            val jsonRequest = object : JsonObjectRequest(
                                Request.Method.POST,
                                url,
                                jsonParam,
                                Response.Listener {
                                    val res = it.getJSONObject("data")
                                    val success: Boolean = res.getBoolean("success")
                                    if (success) {
                                        val ft = res.getBoolean("first_try")
                                        if (ft) {
                                            Toast.makeText(
                                                this@ForgotActivity, "Please Check your mail for OTP"
                                                , Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                this@ForgotActivity,
                                                "OTP already sent, kindly check your mail",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }


                                        val intent =
                                            Intent(this@ForgotActivity, OtpActivity::class.java)
                                        intent.putExtra("mobile", em)
                                        startActivity(intent)
                                        finishAffinity()

                                    } else {
                                        val responseMessageServer =
                                            res.getString("errorMessage")
                                        Toast.makeText(
                                            this@ForgotActivity,
                                            responseMessageServer.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }, Response.ErrorListener {
                                    progress.visibility = View.INVISIBLE
                                    Toast.makeText(
                                        this@ForgotActivity,
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
                                this@ForgotActivity,
                                "Some unexpected error occured!!!",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    } else {
                        val alterDialog =
                            androidx.appcompat.app.AlertDialog.Builder(this@ForgotActivity)
                        alterDialog.setTitle("No Internet")
                        alterDialog.setMessage("Internet Connection can't be establish!")
                        alterDialog.setPositiveButton("Open Settings") { text, listener ->
                            val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                            startActivity(settingsIntent)
                        }

                        alterDialog.setNegativeButton("Exit") { text, listener ->
                            ActivityCompat.finishAffinity(this@ForgotActivity)
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