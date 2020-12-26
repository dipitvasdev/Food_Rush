package com.dipitvasdev.FoodRush.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.dipitvasdev.FoodRush.R
import com.dipitvasdev.FoodRush.adapter.OrderHistoryAdapter
import com.dipitvasdev.FoodRush.model.OrderHistory
import com.dipitvasdev.FoodRush.utils.ConnectionManager
import org.json.JSONException

lateinit var layoutManager2: RecyclerView.LayoutManager
lateinit var orderAdapter: OrderHistoryAdapter
lateinit var recyclerViewAllOrders: RecyclerView
lateinit var toolbar5: androidx.appcompat.widget.Toolbar
lateinit var history_Progress: RelativeLayout
lateinit var no_orders: RelativeLayout

class OrderHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)
        recyclerViewAllOrders = findViewById(R.id.recyclerViewAllOrders)
        toolbar5 = findViewById(R.id.toolBar)
        history_Progress = findViewById(R.id.history)
        no_orders = findViewById(R.id.order_history_fragment_no_orders)
        layoutManager2 = LinearLayoutManager(this@OrderHistoryActivity)
        val orderedRestaurantList = ArrayList<OrderHistory>()
        val sharedPr = this.getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )
        val user_id = sharedPr.getString("user_id", "000")
        setSupportActionBar(toolbar5)
        supportActionBar?.title = "My Previous Orders"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        if (ConnectionManager().checkConnectivity(this@OrderHistoryActivity)) {
            history_Progress.visibility = View.VISIBLE
            try {


                val queue = Volley.newRequestQueue(this@OrderHistoryActivity)

                val url = "http://13.235.250.119/v2/orders/fetch_result/$user_id"

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
                            if (data.length() == 0) {

                                Toast.makeText(
                                    this,
                                    "No Orders Placed yet!!!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                no_orders.visibility = View.VISIBLE

                            } else {
                                no_orders.visibility = View.INVISIBLE



                                for (i in 0 until data.length()) {
                                    val restaurantItemJsonObject = data.getJSONObject(i)

                                    val eachRestaurantObject = OrderHistory(
                                        restaurantItemJsonObject.getString("order_id"),
                                        restaurantItemJsonObject.getString("restaurant_name"),
                                        restaurantItemJsonObject.getString("total_cost"),
                                        restaurantItemJsonObject.getString("order_placed_at")
                                            .substring(0, 10)
                                    )

                                    orderedRestaurantList.add(eachRestaurantObject)
                                }
                                println(orderedRestaurantList)
                                orderAdapter = OrderHistoryAdapter(
                                    this@OrderHistoryActivity,
                                    orderedRestaurantList
                                )

                                recyclerViewAllOrders.adapter = orderAdapter

                                recyclerViewAllOrders.layoutManager = layoutManager2
                            }


                        }
                        history_Progress.visibility = View.INVISIBLE
                    },
                    Response.ErrorListener {
                        history_Progress.visibility = View.INVISIBLE

                        println("errrrror" + it)
                        Toast.makeText(
                            this@OrderHistoryActivity,
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
                    this@OrderHistoryActivity,
                    "Some Unexpected error occured!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this@OrderHistoryActivity)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@OrderHistoryActivity)
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