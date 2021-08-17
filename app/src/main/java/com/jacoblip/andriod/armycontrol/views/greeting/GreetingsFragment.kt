package com.jacoblip.andriod.armycontrol.views.greeting

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.jacoblip.andriod.armycontrol.R

class GreetingsFragment(context: Context,var prefs:SharedPreferences):Fragment() {

    var mycontext = context
    lateinit var getStartedButton:Button
    var isVerified = false

    interface Callbacks {
        fun onButtonSelected()
    }


    private var callbacks: Callbacks? = null

    //the callback functions
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_greetings_army_control,container,false)
        getStartedButton = view.findViewById(R.id.getStartedButton)
        getStartedButton.setOnClickListener {
            callbacks?.onButtonSelected()
        }
        return view
    }



    companion object{
        fun newInstance(context: Context,prefs: SharedPreferences): GreetingsFragment {
            return GreetingsFragment(context,prefs)
        }
    }
}