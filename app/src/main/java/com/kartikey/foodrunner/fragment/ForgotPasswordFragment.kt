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
import androidx.core.app.ActivityCompat

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.kartikey.foodrunner.R
import com.kartikey.foodrunner.utils.ConnectionManager
import org.json.JSONException
import org.json.JSONObject


class ForgotPasswordFragment(val contextParam: Context, val mobile_number: String) : Fragment() {

    lateinit var etOTP: EditText
    lateinit var etNewPassword: EditText
    lateinit var etConfirmForgotPassword: EditText
    lateinit var forgotPasswordProgressDialog: RelativeLayout
    lateinit var btnSubmit: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forgot_password, container, false)

        etOTP = view.findViewById(R.id.etOTP)
        etNewPassword = view.findViewById(R.id.etNewPassword)
        etConfirmForgotPassword = view.findViewById(R.id.etConfirmForgotPassword)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        forgotPasswordProgressDialog = view.findViewById(R.id.forgotPasswordProgressDialog)

        btnSubmit.setOnClickListener(View.OnClickListener
        {
            if (etOTP.text.isBlank()) {
                etOTP.setError("OTP missing")
            } else {
                if (etNewPassword.text.isBlank()) {
                    etNewPassword.setError("Password Missing")
                } else {
                    if (etConfirmForgotPassword.text.isBlank()) {
                        etConfirmForgotPassword.setError("Confirm Password Missing")
                    } else {
                        if ((etNewPassword.text.toString()
                                .toInt() == etConfirmForgotPassword.text.toString().toInt())
                        ) {
                            if (ConnectionManager().checkConnectivity(activity as Context)) {
                                forgotPasswordProgressDialog.visibility = View.VISIBLE

                                try {
                                    val loginUser = JSONObject()

                                    loginUser.put("mobile_number", mobile_number)
                                    loginUser.put("password", etNewPassword.text.toString())
                                    loginUser.put("otp", etOTP.text.toString())

                                    val queue = Volley.newRequestQueue(activity as Context)
                                    val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                                    val jsonObjectRequest = object : JsonObjectRequest(
                                        Request.Method.POST,
                                        url,
                                        loginUser,
                                        Response.Listener
                                        {
                                            val responseJsonObjectData = it.getJSONObject("data")
                                            val success =
                                                responseJsonObjectData.getBoolean("success")

                                            if (success) {
                                                val serverMessage =
                                                    responseJsonObjectData.getString("successMessage")

                                                Toast.makeText(
                                                    contextParam,
                                                    serverMessage,
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                passwordChanged()
                                            } else {
                                                val responseMessageServer =
                                                    responseJsonObjectData.getString("errorMessage")
                                                Toast.makeText(
                                                    contextParam,
                                                    responseMessageServer.toString(),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            forgotPasswordProgressDialog.visibility = View.GONE
                                        },
                                        Response.ErrorListener
                                        {
                                            forgotPasswordProgressDialog.visibility = View.GONE
                                            Toast.makeText(
                                                contextParam,
                                                "mSome Error occurred!!!",
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
                            } else {
                                val alterDialog =
                                    androidx.appcompat.app.AlertDialog.Builder(activity as Context)
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
                        } else {
                            etConfirmForgotPassword.setError("Passwords don't match")
                        }
                    }
                }
            }

        })

        return view
    }

    fun passwordChanged() {
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(
            R.id.frameLayout,
            LoginFragment(contextParam)
        )
        transaction?.commit()
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