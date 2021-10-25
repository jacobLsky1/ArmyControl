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
    lateinit var newGroupButton:Button
    var isVerified = false

    interface Callbacks {
        fun onButtonSelected()
    }
    interface NewGroupCallBacks{
        fun onNewGroupButtonSelected()
    }


    private var callbacks: Callbacks? = null
    private var newCallbacks:NewGroupCallBacks? = null

    //the callback functions
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
        newCallbacks = context as NewGroupCallBacks
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
        newCallbacks = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.m_fragment_greetings_army_control,container,false)
        getStartedButton = view.findViewById(R.id.getStartedButton)
        newGroupButton = view.findViewById(R.id.newGroupButton)
        getStartedButton.setOnClickListener {
            callbacks?.onButtonSelected()
        }
        newGroupButton.setOnClickListener {
            newCallbacks?.onNewGroupButtonSelected()
        }
        return view
    }



    companion object{
        fun newInstance(context: Context,prefs: SharedPreferences): GreetingsFragment {
            return GreetingsFragment(context,prefs)
        }
    }
}