package com.kartikey.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kartikey.foodrunner.R
import com.kartikey.foodrunner.model.CartItems


class CartAdapter(val context: Context, val cartItems: ArrayList<CartItems>) :
    RecyclerView.Adapter<CartAdapter.ViewHolderCart>() {

    class ViewHolderCart(view: View) : RecyclerView.ViewHolder(view) {
        val txtOrderItem: TextView = view.findViewById(R.id.txtCartItemName)
        val txtOrderItemPrice: TextView = view.findViewById(R.id.txtCartPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCart {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_recycler_view_single_row, parent, false)
        return ViewHolderCart(view)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    override fun onBindViewHolder(holder: ViewHolderCart, position: Int) {
        val cartItemObject = cartItems[position]
        holder.txtOrderItem.text = cartItemObject.itemName
        holder.txtOrderItemPrice.text = "Rs. ${cartItemObject.itemPrice}"
    }

}