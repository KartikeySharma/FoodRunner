package com.kartikey.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kartikey.foodrunner.R
import com.kartikey.foodrunner.activity.CartActivity
import com.kartikey.foodrunner.model.RestaurantMenu

class RestaurantMenuAdapter
    (
    val context: Context,
    val restaurantId: String,
    val restaurantName: String,
    val proceedToCartPassed: RelativeLayout,
    val btnProceedToCart: Button,
    val restaurantMenu: ArrayList<RestaurantMenu>
) : RecyclerView.Adapter<RestaurantMenuAdapter.ViewHolderRestaurantMenu>() {

    var itemSelectedCount: Int = 0
    lateinit var proceedToCart: RelativeLayout

    var itemsSelectedId = arrayListOf<String>()

    class ViewHolderRestaurantMenu(view: View) : RecyclerView.ViewHolder(view) {
        val txtSerialNumber: TextView = view.findViewById(R.id.txtSerialName)
        val txtItemName: TextView = view.findViewById(R.id.txtItemName)
        val txtItemPrice: TextView = view.findViewById(R.id.txtItemPrice)
        val btnAddToCart: Button = view.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRestaurantMenu {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.restuarant_menu_recycler_view_single_row, parent, false)

        return ViewHolderRestaurantMenu(view)
    }

    override fun getItemCount(): Int {
        return restaurantMenu.size
    }

    fun getSelectedItemCount(): Int {
        return itemSelectedCount
    }

    override fun onBindViewHolder(holder: ViewHolderRestaurantMenu, position: Int) {
        val restaurantMenuItem = restaurantMenu[position]
        proceedToCart = proceedToCartPassed//button view passed from the RestaurantMenuActivity

        btnProceedToCart.setOnClickListener(View.OnClickListener
        {
            val intent = Intent(context, CartActivity::class.java)
            intent.putExtra(
                "restaurantId",
                restaurantId.toString()
            )

            intent.putExtra("restaurantName", restaurantName)
            intent.putExtra(
                "selectedItemsId",
                itemsSelectedId
            )
            context.startActivity(intent)

        })

        holder.btnAddToCart.setOnClickListener(View.OnClickListener
        {
            if (holder.btnAddToCart.text.toString().equals("Remove")) {
                itemSelectedCount--     //item unselected

                itemsSelectedId.remove(holder.btnAddToCart.getTag().toString())
                holder.btnAddToCart.text = context.getString(R.string.add)
                holder.btnAddToCart.setBackgroundResource(R.drawable.rounded_view_for_menu_red)

            } else {

                itemSelectedCount++     //item selected
                itemsSelectedId.add(holder.btnAddToCart.getTag().toString())

                holder.btnAddToCart.text = context.getString(R.string.remove)
                holder.btnAddToCart.setBackgroundResource(R.drawable.rounded_view_for_menu_chosen)
            }

            if (itemSelectedCount > 0) {
                proceedToCart.visibility = View.VISIBLE
            } else {
                proceedToCart.visibility = View.GONE
            }

        })

        holder.btnAddToCart.setTag(restaurantMenuItem.id + "")      //save the item id in textViewName Tag ,will be used to add to cart
        holder.txtSerialNumber.text = (position + 1).toString()     //position starts from 0
        holder.txtItemName.text = restaurantMenuItem.name
        holder.txtItemPrice.text = """Rs.${restaurantMenuItem.cost_for_one}"""
    }


}