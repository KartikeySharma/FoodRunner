package com.kartikey.foodrunner.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.kartikey.foodrunner.fragment.LoginFragment
import com.kartikey.foodrunner.R

class LoginRegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

        val sharedPreferences = getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )

        if (sharedPreferences.getBoolean("user_logged_in", false)) {
            val intent = Intent(this@LoginRegisterActivity, DashboardActivity::class.java)
            startActivity(intent)
            finish();
        } else {
            openLoginFragment()
        }
    }

    fun openLoginFragment() {

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.frameLayout,
            LoginFragment(this)
        )
        transaction.commit()
        supportActionBar?.title = "DashboardActivity"

    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayout)
        when (currentFragment) {
            !is LoginFragment -> {
                openLoginFragment()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                openLoginFragment()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}