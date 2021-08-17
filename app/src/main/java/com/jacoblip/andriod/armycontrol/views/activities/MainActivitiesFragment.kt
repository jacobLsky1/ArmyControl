package com.jacoblip.andriod.armycontrol.views.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.sevices.ActivitiesViewModel

class MainActivitiesFragment(var commandPath:String):Fragment() {

    lateinit var activitiesViewModel:ActivitiesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main_activities,container,false)

        view.apply {

        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object{
        fun newInstance(commandPath: String): MainActivitiesFragment {
            return MainActivitiesFragment(commandPath)
        }
    }
}