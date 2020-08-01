package com.kartikey.foodrunner.adapter


import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.kartikey.foodrunner.R
import com.kartikey.foodrunner.activity.RestaurantMenuActivity
import com.kartikey.foodrunner.database.RestaurantDatabase
import com.kartikey.foodrunner.database.RestaurantEntity
import com.kartikey.foodrunner.model.Restaurant
import com.squareup.picasso.Picasso


class DashboardFragmentAdapter(val context: Context, var itemList: ArrayList<Restaurant>) :
    RecyclerView.Adapter<DashboardFragmentAdapter.ViewHolderDashboard>() {

    class ViewHolderDashboard(view: View) : RecyclerView.ViewHolder(view) {
        val imgRestaurant: ImageView = view.findViewById(R.id.imgRestaurant)
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtPricePerPerson: TextView = view.findViewById(R.id.txtPricePerPerson)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
        val txtFavourite: TextView = view.findViewById(R.id.txtFavourite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDashboard {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dashboard_recycler_view_single_row, parent, false)
        return ViewHolderDashboard(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolderDashboard, position: Int) {
        val restaurant = itemList[position]
        val restaurantEntity = RestaurantEntity(
            restaurant.restaurantId,
            restaurant.restaurantName
        )

        holder.txtFavourite.setOnClickListener {
            if (!DBAsynTask(context, restaurantEntity, 1).execute().get()) {
                val result = DBAsynTask(context, restaurantEntity, 2).execute().get()

                if (result) {
                    holder.txtFavourite.setTag("liked")//new value
                    holder.txtFavourite.background =
                        context.resources.getDrawable(R.drawable.ic_fav_fill)
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            } else {
                val result = DBAsynTask(context, restaurantEntity, 3).execute().get()

                if (result) {
                    holder.txtFavourite.setTag("unliked")
                    holder.txtFavourite.background =
                        context.resources.getDrawable(R.drawable.ic_fav_outline)
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }

        holder.llContent.setOnClickListener {

            val intent = Intent(context, RestaurantMenuActivity::class.java)
            intent.putExtra("restaurantId", holder.txtRestaurantName.getTag().toString())
            intent.putExtra("restaurantName", holder.txtRestaurantName.text.toString())
            context.startActivity(intent)

        }

        holder.txtRestaurantName.setTag(restaurant.restaurantId + "")
        holder.txtRestaurantName.text = restaurant.restaurantName
        holder.txtPricePerPerson.text = restaurant.costForOne + "/Person "
        holder.txtRating.text = restaurant.restaurantRating
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.ic_default_image_restaurant)
            .into(holder.imgRestaurant)

        val checkFav = DBAsynTask(context, restaurantEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav) {
            holder.txtFavourite.setTag("liked")
            holder.txtFavourite.background = context.resources.getDrawable(R.drawable.ic_fav_fill)
        } else {
            holder.txtFavourite.setTag("unliked")
            holder.txtFavourite.background =
                context.resources.getDrawable(R.drawable.ic_fav_outline)
        }

    }

    fun filterList(filteredList: ArrayList<Restaurant>) {
        itemList = filteredList
        notifyDataSetChanged()
    }

    class DBAsynTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {
            /*
            * Mode 1->check if restaurant is in favourites
            * Mode 2->Save the restaurant into DB as favourites
            * Mode 3-> Remove the favourite restaurant
            */
            when (mode) {
                1 -> {
                    val restaurant: RestaurantEntity? = db.restaurantDao()
                        .getRestaurantById(restaurantEntity.restaurantId)
                    db.close()
                    return restaurant != null
                }
                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                else -> {
                    return false
                }
            }
        }
    }
}