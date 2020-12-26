package com.dipitvasdev.FoodRush.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.dipitvasdev.FoodRush.R
import com.dipitvasdev.FoodRush.model.RestaurantMenu
import com.dipitvasdev.FoodRush.utils.ConnectionManager
import org.json.JSONException
import com.dipitvasdev.FoodRush.adapter.MenuAdapter

lateinit var toolbar3: Toolbar
lateinit var recyclerView: RecyclerView
lateinit var layoutManager: RecyclerView.LayoutManager
lateinit var menuAdapter: MenuAdapter
lateinit var resId: String
lateinit var resName: String
lateinit var proceedToCartLayout: RelativeLayout
lateinit var btnProceedToCart: Button
lateinit var activity_Progress: RelativeLayout
var restaurantMenuList = arrayListOf<RestaurantMenu>()

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        proceedToCartLayout = findViewById(R.id.relativeLayoutProceedToCart)
        btnProceedToCart = findViewById(R.id.buttonProceedToCart)
        activity_Progress = findViewById(R.id.menu_Progress)
        toolbar3 = findViewById(R.id.toolBar)
        resId = intent.getStringExtra("restaurantId")
        resName = intent.getStringExtra("restaurantName")
        setSupportActionBar(toolbar3)
        supportActionBar?.title = resName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        layoutManager = LinearLayoutManager(this@MenuActivity)
        recyclerView = findViewById(R.id.recyclerViewMenu)
        if (ConnectionManager().checkConnectivity(this@MenuActivity)) {
            activity_Progress.visibility = View.VISIBLE
            try {
                restaurantMenuList.clear()

                val queue = Volley.newRequestQueue(this@MenuActivity)

                val url = "http://13.235.250.119/v2/restaurants/fetch_result/$resId"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    Response.Listener {
                        println("Response1 is " + it)
                        val responseJsonObjectData = it.getJSONObject("data")
                        val success = responseJsonObjectData.getBoolean("success")
                        if (success) {
                            val data = responseJsonObjectData.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val restaurantJsonObject = data.getJSONObject(i)
                                val restaurantObject = RestaurantMenu(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("cost_for_one")

                                )
                                restaurantMenuList.add(restaurantObject)
                                menuAdapter = MenuAdapter(
                                    this@MenuActivity,
                                    resId,
                                    resName,
                                    proceedToCartLayout,
                                    btnProceedToCart,
                                    recyclerView,
                                    restaurantMenuList
                                )
                                recyclerView.adapter = menuAdapter
                                recyclerView.layoutManager = layoutManager
                            }

                        }
                        activity_Progress.visibility = View.INVISIBLE
                    },
                    Response.ErrorListener {
                        activity_Progress.visibility = View.INVISIBLE

                        println("errrrror" + it)
                        Toast.makeText(
                            this@MenuActivity,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "ed0e68368529be"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)
            } catch (e: JSONException) {
                Toast.makeText(
                    this@MenuActivity,
                    "Some Unexpected error occured!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this@MenuActivity)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@MenuActivity)
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()

        }

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