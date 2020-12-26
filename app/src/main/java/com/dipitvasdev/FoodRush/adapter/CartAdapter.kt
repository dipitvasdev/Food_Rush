package com.dipitvasdev.FoodRush.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dipitvasdev.FoodRush.R
import com.dipitvasdev.FoodRush.model.Cart

class CartAdapter(val context: Context, var itemCart: ArrayList<Cart>) :
    RecyclerView.Adapter<CartAdapter.ViewHolderCart>() {
    class ViewHolderCart(view: View) : RecyclerView.ViewHolder(view) {
        val OrderItem: TextView = view.findViewById(R.id.OrderItem)
        val OrderItemPrice: TextView = view.findViewById(R.id.OrderItemPrice)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCart {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_cart, parent, false)
        return ViewHolderCart(view)
    }

    override fun getItemCount(): Int {
        return itemCart.size
    }

    override fun onBindViewHolder(holder: ViewHolderCart, position: Int) {
        val cart = itemCart[position]
        holder.OrderItem.text = cart.itemName
        holder.OrderItemPrice.text = cart.itemPrice
    }
}