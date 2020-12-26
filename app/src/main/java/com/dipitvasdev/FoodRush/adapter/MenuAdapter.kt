package com.dipitvasdev.FoodRush.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.dipitvasdev.FoodRush.R
import com.dipitvasdev.FoodRush.activities.CartActivity
import com.dipitvasdev.FoodRush.model.RestaurantMenu

class MenuAdapter(
    val context: Context,
    val restaurantId: String,
    val restaurantName: String,
    val proceedToCartPassed: RelativeLayout,
    val buttonProceedToCart: Button,
    val recycler: RecyclerView,
    var itemList: ArrayList<RestaurantMenu>
) : RecyclerView.Adapter<MenuAdapter.ViewHolderRestaurantMenu>() {
    var itemSelectedCount: Int = 0
    var sum = 0
    lateinit var proceedToCart: RelativeLayout
    var itemsSelectedId = arrayListOf<String>()

    class ViewHolderRestaurantMenu(view: View) : RecyclerView.ViewHolder(view) {
        val textViewSerialNumber: TextView = view.findViewById(R.id.textViewNum)
        val textViewItemName: TextView = view.findViewById(R.id.textViewItemName)
        val textViewItemPrice: TextView = view.findViewById(R.id.textViewItemPrice)
        val buttonAddToCart: Button = view.findViewById(R.id.btnAdd)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRestaurantMenu {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_menu, parent, false)
        return MenuAdapter.ViewHolderRestaurantMenu(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: ViewHolderRestaurantMenu, position: Int) {
        val restaurantMenu = itemList[position]
        val sp = context.getSharedPreferences("amount", Context.MODE_PRIVATE)
        proceedToCart = proceedToCartPassed

        holder.buttonAddToCart.setOnClickListener {
            if (holder.buttonAddToCart.text.toString() == "Remove") {
                itemSelectedCount--
                itemsSelectedId.remove(holder.buttonAddToCart.getTag().toString())
                holder.buttonAddToCart.text = "Add"
                holder.buttonAddToCart.setBackgroundColor(Color.parseColor("#394989"))
                sum = sum - ((restaurantMenu.cost).toInt())
            } else {
                itemSelectedCount++
                itemsSelectedId.add(holder.buttonAddToCart.getTag().toString())
                holder.buttonAddToCart.text = "Remove"
                holder.buttonAddToCart.setBackgroundColor(Color.rgb(255, 87, 34))
                sum = sum + ((restaurantMenu.cost).toInt())
            }
            if (itemSelectedCount > 0) {
                val margins = (recycler.layoutParams as RelativeLayout.LayoutParams).apply {
                    leftMargin = 0
                    topMargin = 0
                    bottomMargin = 150
                    rightMargin = 0
                }
                recycler.layoutParams = margins
                proceedToCart.visibility = View.VISIBLE
            } else {
                proceedToCart.visibility = View.INVISIBLE
            }
        }

        buttonProceedToCart.setOnClickListener(View.OnClickListener {

            val intent = Intent(context, CartActivity::class.java)
            intent.putExtra("restaurantId", restaurantId.toString())
            intent.putExtra("restaurantName", restaurantName)
            intent.putExtra("selectedItemsId", itemsSelectedId)
            sp.edit().putInt("am", sum).apply()
            context.startActivity(intent)
        })
        holder.buttonAddToCart.setTag(restaurantMenu.id + "")
        holder.textViewSerialNumber.text = (position + 1).toString()
        holder.textViewItemName.text = restaurantMenu.name
        holder.textViewItemPrice.text = restaurantMenu.cost
    }

    fun getCount(): Int {
        return itemSelectedCount
    }

}
