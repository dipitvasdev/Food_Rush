package com.dipitvasdev.FoodRush.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.dipitvasdev.FoodRush.R
import com.dipitvasdev.FoodRush.adapter.CartAdapter
import com.dipitvasdev.FoodRush.model.Cart
import com.dipitvasdev.FoodRush.utils.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {
    lateinit var toolbar4: androidx.appcompat.widget.Toolbar
    lateinit var textViewOrderingFrom: TextView
    lateinit var buttonPlaceOrder: Button
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var cartAdapter: CartAdapter
    lateinit var resId: String
    lateinit var restaurantName: String
    lateinit var linearLayout: LinearLayout
    lateinit var cart_Progress: RelativeLayout
    lateinit var selectedItemsId: ArrayList<String>
    var Amount = 0
    var cartListItems = arrayListOf<Cart>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        val sha = this.getSharedPreferences("amount", Context.MODE_PRIVATE)
        buttonPlaceOrder = findViewById(R.id.btnPlaceOrder)
        textViewOrderingFrom = findViewById(R.id.OrderingFrom)
        linearLayout = findViewById(R.id.linearLayout)
        toolbar4 = findViewById(R.id.toolBar)
        cart_Progress = findViewById(R.id.cart_Progress)
        resId = intent.getStringExtra("restaurantId")
        restaurantName = intent.getStringExtra("restaurantName")
        selectedItemsId = intent.getStringArrayListExtra("selectedItemsId")
        Amount = sha.getInt("am", 0)
        buttonPlaceOrder.text = "Place Order(Total:Rs. " + Amount + ")"
        layoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.recyclerViewCart)
        textViewOrderingFrom.text = restaurantName
        setSupportActionBar(toolbar4)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        buttonPlaceOrder.setOnClickListener {
            val shared = this.getSharedPreferences(
                getString(R.string.shared_preferences),
                Context.MODE_PRIVATE
            )
            if (ConnectionManager().checkConnectivity(this@CartActivity)) {
                cart_Progress.visibility = View.VISIBLE
                try {
                    val foodJson = JSONArray()
                    for (foodItem in selectedItemsId) {
                        val singleItemObject = JSONObject()
                        singleItemObject.put("food_item_id", foodItem)
                        foodJson.put(singleItemObject)
                    }
                    val jsonP = JSONObject()
                    jsonP.put("user_id", shared.getString("user_id", "0"))
                    jsonP.put("restaurant_id", resId.toString())
                    jsonP.put("total_cost", Amount)
                    jsonP.put("food", foodJson)

                    val queue = Volley.newRequestQueue(this@CartActivity)

                    val url = "http://13.235.250.119/v2/place_order/fetch_result/"
                    val jsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        jsonP,
                        Response.Listener {
                            println("Response1 is " + it)
                            val responseJsonObjectData = it.getJSONObject("data")
                            val success = responseJsonObjectData.getBoolean("success")
                            if (success) {

                                Toast.makeText(
                                    this@CartActivity,
                                    "Order Placed",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this@CartActivity, OrderConfirmed::class.java)
                                startActivity(intent)
                                finishAffinity()
                            }


                        },
                        Response.ErrorListener {
                            cart_Progress.visibility = View.INVISIBLE

                            println("errrrror" + it)
                            Toast.makeText(
                                this@CartActivity,
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
                        this@CartActivity,
                        "Some Unexpected error occured!!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {

                val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this@CartActivity)
                alterDialog.setTitle("No Internet")
                alterDialog.setMessage("Internet Connection can't be establish!")
                alterDialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                    startActivity(settingsIntent)
                }

                alterDialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@CartActivity)
                }
                alterDialog.setCancelable(false)

                alterDialog.create()
                alterDialog.show()

            }

        }


    }

    fun fetch() {
        if (ConnectionManager().checkConnectivity(this@CartActivity)) {
            cart_Progress.visibility = View.VISIBLE
            try {

                val queue = Volley.newRequestQueue(this@CartActivity)

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
                            cartListItems.clear()
                            Amount = 0
                            for (i in 0 until data.length()) {
                                val cartItemJsonObject = data.getJSONObject(i)
                                if (selectedItemsId.contains(cartItemJsonObject.getString("id"))) {
                                    val menu = Cart(
                                        cartItemJsonObject.getString("id"),
                                        cartItemJsonObject.getString("name"),
                                        cartItemJsonObject.getString("cost_for_one"),
                                        cartItemJsonObject.getString("restaurant_id")
                                    )
                                    Amount += cartItemJsonObject.getString("cost_for_one")
                                        .toInt()
                                    cartListItems.add(menu)
                                }
                                cartAdapter = CartAdapter(
                                    this,
                                    cartListItems
                                )
                                recyclerView.adapter = cartAdapter
                                recyclerView.layoutManager = layoutManager
                            }

                        }
                        cart_Progress.visibility = View.INVISIBLE
                    },
                    Response.ErrorListener {
                        cart_Progress.visibility = View.INVISIBLE

                        println("errrrror" + it)
                        Toast.makeText(
                            this@CartActivity,
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
                    this@CartActivity,
                    "Some Unexpected error occured!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this@CartActivity)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@CartActivity)
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)

        alterDialog.setTitle("Warning!")
        alterDialog.setMessage("Going Back will Clear the Cart!!")
        alterDialog.setPositiveButton("Proceed") { text, listener ->
            val id = item.itemId
            when (id) {
                android.R.id.home -> {
                    super.onBackPressed()
                }
            }
        }

        alterDialog.setNegativeButton("Stay Here") { text, listener ->

        }
        alterDialog.create()
        alterDialog.show()
        return super.onOptionsItemSelected(item)

    }

    override fun onResume() {
        fetch()
        super.onResume()
    }

}