package com.kartikey.foodrunner.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.kartikey.foodrunner.R
import com.kartikey.foodrunner.activity.DashboardActivity
import com.kartikey.foodrunner.utils.ConnectionManager
import org.json.JSONException
import org.json.JSONObject


class RegisterFragment(val contextParam: Context) : Fragment() {

    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etDeliveryAddress: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnRegister: Button
    lateinit var registerProgressDialog: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_register, container, false)

        etName = view.findViewById(R.id.etName)
        etEmail = view.findViewById(R.id.etEmail)
        etMobileNumber = view.findViewById(R.id.etMobileNumber)
        etDeliveryAddress = view.findViewById(R.id.etDeliveryAddress)
        etPassword = view.findViewById(R.id.etPassword)
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword)
        btnRegister = view.findViewById(R.id.btnSubmit)
        registerProgressDialog = view.findViewById(R.id.registerProgressdialog)

        btnRegister.setOnClickListener {
            registerUserFun()
        }
        return view
    }

    fun userSuccessfullyRegistered() {
        openDashBoard()
    }

    fun openDashBoard() {
        val intent = Intent(activity as Context, DashboardActivity::class.java)
        startActivity(intent)
        activity?.finish();
    }

    fun registerUserFun() {
        val sharedPreferences = contextParam.getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )

        sharedPreferences.edit().putBoolean("user_logged_in", false).apply()

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            if (checkForErrors()) {
                registerProgressDialog.visibility = View.VISIBLE
                try {
                    val registerUser = JSONObject()
                    registerUser.put("name", etName.text)
                    registerUser.put("mobile_number", etMobileNumber.text)
                    registerUser.put("password", etPassword.text)
                    registerUser.put("address", etDeliveryAddress.text)
                    registerUser.put("email", etEmail.text)

                    val queue = Volley.newRequestQueue(activity as Context)
                    val url = "http://13.235.250.119/v2/register/fetch_result"

                    val jsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        registerUser,
                        Response.Listener
                        {
                            val responseJsonObjectData = it.getJSONObject("data")
                            val success = responseJsonObjectData.getBoolean("success")

                            if (success) {

                                val data = responseJsonObjectData.getJSONObject("data")
                                sharedPreferences.edit()
                                    .putBoolean("user_logged_in", true).apply()
                                sharedPreferences.edit()
                                    .putString("user_id", data.getString("user_id")).apply()
                                sharedPreferences.edit().putString("name", data.getString("name"))
                                    .apply()
                                sharedPreferences.edit().putString("email", data.getString("email"))
                                    .apply()
                                sharedPreferences.edit()
                                    .putString("mobile_number", data.getString("mobile_number"))
                                    .apply()
                                sharedPreferences.edit()
                                    .putString("address", data.getString("address")).apply()

                                Toast.makeText(
                                    contextParam,
                                    "Registered successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                userSuccessfullyRegistered()

                            } else {
                                val responseMessageServer =
                                    responseJsonObjectData.getString("errorMessage")
                                Toast.makeText(
                                    contextParam,
                                    responseMessageServer.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            registerProgressDialog.visibility = View.GONE
                        },
                        Response.ErrorListener
                        {
                            registerProgressDialog.visibility = View.GONE
                            Toast.makeText(
                                contextParam,
                                "Some Error occurred!",
                                Toast.LENGTH_SHORT
                            ).show()

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
                        contextParam,
                        "Some unexpected error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(activity as Context)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be established!")
            alterDialog.setPositiveButton("Open Settings")
            { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }
            alterDialog.setNegativeButton("Exit")
            { _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            alterDialog.create()
            alterDialog.show()
        }
    }

    fun checkForErrors(): Boolean {
        //errorPassCount determines if there are any errors or not
        var errorPassCount = 0
        if (etName.text.isBlank()) {
            etName.error = "Field Missing!"
        } else {
            errorPassCount++
        }
        if (etMobileNumber.text.isBlank()) {
            etMobileNumber.error = "Field Missing!"
        } else {
            errorPassCount++
        }
        if (etEmail.text.isBlank()) {
            etEmail.error = "Field Missing!"
        } else {
            errorPassCount++
        }
        if (etDeliveryAddress.text.isBlank()) {
            etDeliveryAddress.setError("Field Missing!")
        } else {
            errorPassCount++
        }
        if (etConfirmPassword.text.isBlank()) {
            etConfirmPassword.setError("Field Missing!")
        } else {
            errorPassCount++
        }
        if (etPassword.text.isBlank()) {
            etPassword.setError("Field Missing!")
        } else {
            errorPassCount++
        }
        if (etPassword.text.isNotBlank() && etConfirmPassword.text.isNotBlank()) {
            if (etPassword.text.toString().toInt() == etConfirmPassword.text.toString().toInt()) {
                errorPassCount++
            } else {
                etConfirmPassword.setError("Confirmed Password doesn't match")
            }
        }
        return errorPassCount == 7
    }

    override fun onResume() {
        if (!ConnectionManager().checkConnectivity(activity as Context)) {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(activity as Context)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be established!")
            alterDialog.setPositiveButton("Open Settings")
            { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }
            alterDialog.setNegativeButton("Exit")
            { _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            alterDialog.setCancelable(false)
            alterDialog.create()
            alterDialog.show()
        }
        super.onResume()
    }
}