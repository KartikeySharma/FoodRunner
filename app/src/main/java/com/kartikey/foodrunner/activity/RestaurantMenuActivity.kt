package com.kartikey.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.kartikey.foodrunner.R
import com.kartikey.foodrunner.adapter.RestaurantMenuAdapter
import com.kartikey.foodrunner.model.RestaurantMenu
import com.kartikey.foodrunner.utils.ConnectionManager
import org.json.JSONException
import kotlin.collections.HashMap

class RestaurantMenuActivity : AppCompatActivity() {

    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var menuAdapter: RestaurantMenuAdapter
    lateinit var restaurantId: String
    lateinit var restaurantName: String
    lateinit var proceedToCartLayout: RelativeLayout
    lateinit var btnProceedToCart: Button
    lateinit var restaurantMenuProgressDialog: RelativeLayout

    var restaurantMenuList = arrayListOf<RestaurantMenu>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)

        proceedToCartLayout = findViewById(R.id.rlProceedToCart)
        btnProceedToCart = findViewById(R.id.btnProceedToCart)
        restaurantMenuProgressDialog =
            findViewById(R.id.restaurantMenuProgressDialog)

        toolbar = findViewById(R.id.toolBar)

        restaurantId = intent.getStringExtra("restaurantId").toString()
        restaurantName = intent.getStringExtra("restaurantName").toString()

        setToolBar()
        layoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.recyclerViewRestaurantMenu)

    }

    fun fetchData() {
        if (ConnectionManager().checkConnectivity(this)) {
            restaurantMenuProgressDialog.visibility = View.VISIBLE
            try {

                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/restaurants/fetch_result/${restaurantId}"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    Response.Listener
                    {
                        val responseJsonObjectData = it.getJSONObject("data")
                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {
                            restaurantMenuList.clear()

                            val data = responseJsonObjectData.getJSONArray("data")

                            for (i in 0 until data.length()) {
                                val bookJsonObject = data.getJSONObject(i)
                                val menuObject = RestaurantMenu(
                                    bookJsonObject.getString("id"),
                                    bookJsonObject.getString("name"),
                                    bookJsonObject.getString("cost_for_one")

                                )
                                restaurantMenuList.add(menuObject)

                                menuAdapter = RestaurantMenuAdapter(
                                    this,
                                    restaurantId,
                                    restaurantName,
                                    proceedToCartLayout,//pass the relative layout which has the button to enable it later
                                    btnProceedToCart,
                                    restaurantMenuList
                                )

                                recyclerView.adapter = menuAdapter
                                recyclerView.layoutManager = layoutManager
                            }
                        }
                        restaurantMenuProgressDialog.visibility = View.GONE
                    },
                    Response.ErrorListener
                    {
                        Toast.makeText(
                            this,
                            "Some Error occurred!",
                            Toast.LENGTH_SHORT
                        ).show()

                        restaurantMenuProgressDialog.visibility = View.GONE
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "13714ab03e5a4d"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)

            } catch (e: JSONException) {
                Toast.makeText(
                    this,
                    "Some Unexpected error occurred!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be established!")
            alterDialog.setPositiveButton("Open Settings")
            { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }
            alterDialog.setNegativeButton("Exit")
            { _, _ ->
                finishAffinity()
            }
            alterDialog.setCancelable(false)
            alterDialog.create()
            alterDialog.show()
        }

    }

    fun setToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = restaurantName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_back_arrow)
    }

    override fun onBackPressed() {

        if (menuAdapter.getSelectedItemCount() > 0) {
            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("Alert!")
            alterDialog.setMessage("Going back will remove everything from cart")
            alterDialog.setPositiveButton("Okay")
            { _, _ ->
                super.onBackPressed()
            }
            alterDialog.setNegativeButton("No")
            { _, _ ->
                //do nothing
            }
            alterDialog.show()
        } else {
            super.onBackPressed()
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                if (menuAdapter.getSelectedItemCount() > 0) {

                    val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
                    alterDialog.setTitle("Alert!")
                    alterDialog.setMessage("Going back will remove everything from cart")
                    alterDialog.setPositiveButton("Okay")
                    { _, _ ->
                        super.onBackPressed()
                    }
                    alterDialog.setNegativeButton("No")
                    { _, _ ->

                    }
                    alterDialog.show()
                } else {
                    super.onBackPressed()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {

        if (ConnectionManager().checkConnectivity(this)) {
            if (restaurantMenuList.isEmpty())
                fetchData()
        } else {
            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be established!")
            alterDialog.setPositiveButton("Open Settings")
            { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                //chose to make an intent to ACTION_SETTINGS instead of WIRELESS_ACTION_SETTINGS as it
                //is more convenient for someone who wants to choose between opening mobile data or WIFI
                startActivity(settingsIntent)
            }
            alterDialog.setNegativeButton("Exit")
            { _, _ ->
                finishAffinity()
            }
            alterDialog.setCancelable(false)
            alterDialog.create()
            alterDialog.show()
        }
        super.onResume()

    }
}