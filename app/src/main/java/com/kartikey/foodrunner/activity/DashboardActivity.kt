package com.kartikey.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.kartikey.foodrunner.R
import com.kartikey.foodrunner.fragment.*


class DashboardActivity : AppCompatActivity() {

    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout
    lateinit var txtCurrentUser: TextView
    lateinit var txtMobileNumber: TextView
    lateinit var sharedPreferences: SharedPreferences

    var previousMenuItemSelected: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        sharedPreferences = getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )

        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolBar)
        frameLayout = findViewById(R.id.frameLayout)
        navigationView = findViewById(R.id.navigationView)
        drawerLayout = findViewById(R.id.drawerLayout)
        val headerView = navigationView.getHeaderView(0)
        txtCurrentUser = headerView.findViewById(R.id.txtCurrentUser)
        txtMobileNumber = headerView.findViewById(R.id.txtMobileNumber)

        //Used to set the default dashboard to checked when the
        //app opens or the back btn is pressed to go to the previous fragment
        navigationView.menu.getItem(0).setCheckable(true)
        navigationView.menu.getItem(0).setChecked(true)

        //set tool bar
        setToolBar()

        //default user details
        txtCurrentUser.text = sharedPreferences.getString("name", "Kartikey Sharma")
        txtMobileNumber.text = "+91-" + sharedPreferences.getString("mobile_number", "9876543210")

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@DashboardActivity,
            drawerLayout,
            R.string.open_drawer,//hamburger icon to open
            R.string.close_drawer
        )//once opened it changes to back arrow

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        //to sync with the state of the navigation toggle with the state of the navigation drawer

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItemSelected != null && previousMenuItemSelected!!.itemId != R.id.logout) {
                previousMenuItemSelected?.isChecked = true
            }

            //the current fragment will be previous fragment when a user clicks a new fragment
            previousMenuItemSelected = it
            it.isCheckable = true
            it.isChecked = true

            //The closing of navigation drawer is delayed to make the transition smoother
            //Delayed by 0.2s
            val pendingRunnable = Runnable { drawerLayout.closeDrawer(GravityCompat.START) }
            Handler().postDelayed(
                pendingRunnable,
                100
            )

            when (it.itemId) {

                R.id.home -> {
                    openDashboard()
                    drawerLayout.closeDrawers()
                }
                R.id.profile -> {

                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            ProfileFragment(this)
                        )
                        .commit()

                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()
                    Toast.makeText(
                        this@DashboardActivity,
                        "My Profile",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                }
                R.id.favouriteRestaurants -> {

                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            FavouriteRestaurantFragment(this)
                        )
                        .commit()

                    supportActionBar?.title = "Favourite Restaurants"
                    drawerLayout.closeDrawers()

                }
                R.id.orderHistory -> {

                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout, OrderHistoryFragment()
                    ).commit()
                    supportActionBar?.title = "My Previous Orders"
                    drawerLayout.closeDrawers()

                    Toast.makeText(
                        this@DashboardActivity,
                        "Order History",
                        Toast.LENGTH_SHORT
                    ).show()

                }
                R.id.faqs -> {

                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            FaqsFragment()
                        )
                        .commit()

                    supportActionBar?.title = "Frequently Asked Questions"
                    drawerLayout.closeDrawers()

                    Toast.makeText(
                        this@DashboardActivity,
                        " FAQs ",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                R.id.logout -> {
                    drawerLayout.closeDrawers()

                    val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
                    alterDialog.setTitle("Confirmation")
                    alterDialog.setMessage("Are you sure you want to log out?")
                    alterDialog.setPositiveButton("Yes")
                    { _, _ ->
                        sharedPreferences.edit().putBoolean("user_logged_in", false).apply()
                        val intent =
                            Intent(this@DashboardActivity, LoginRegisterActivity::class.java)
                        startActivity(intent)
                        ActivityCompat.finishAffinity(this)
                        //app login user credentials are erased and it takes the user to the login screen
                    }

                    alterDialog.setNegativeButton("No")
                    { _, _ ->
                        openDashboard()
                    }
                    alterDialog.setCancelable(false)
                    alterDialog.create()
                    alterDialog.show()
                }
            }

            return@setNavigationItemSelectedListener true
        }
        openDashboard()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {

        val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayout)

        when (currentFragment) {
            !is DashboardFragment -> {
                navigationView.menu.getItem(0).setChecked(true)
                openDashboard()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    fun setToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "All Restaurants"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
    }

    fun openDashboard() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.frameLayout,
            DashboardFragment(this)
        )
        transaction.commit()

        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.home)
    }

    //to disable auto pop of soft keyboard on the search
    override fun onResume() {
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        super.onResume()
    }
}
