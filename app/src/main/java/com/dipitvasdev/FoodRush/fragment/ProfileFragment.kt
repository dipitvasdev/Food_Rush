package com.dipitvasdev.FoodRush.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dipitvasdev.FoodRush.R

lateinit var textViewName: TextView
lateinit var textViewEmail: TextView
lateinit var textViewMobileNumber: TextView
lateinit var textViewAddress: TextView

class ProfileFragment(val contex: Context) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        textViewName = view.findViewById(R.id.textViewName)
        textViewEmail = view.findViewById(R.id.textViewEmail)
        textViewMobileNumber = view.findViewById(R.id.textViewMobileNumber)
        textViewAddress = view.findViewById(R.id.textViewAddress)
        val sharedPreferencess = contex.getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )
        textViewName.text = sharedPreferencess.getString("user_name", "")
        textViewEmail.text = sharedPreferencess.getString("email", "")
        textViewMobileNumber.text = "+91-" + sharedPreferencess.getString("mobile", "")
        textViewAddress.text = sharedPreferencess.getString("address", "")

        return view
    }


}

