package com.jacoblip.andriod.armycontrol.views.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.ArmyActivity
import com.jacoblip.andriod.armycontrol.data.sevices.ActivitiesViewModel

class AddOrEditNewActivityFragment(var armyActivity:ArmyActivity?):Fragment() {

    lateinit var viewModel: ActivitiesViewModel
    lateinit var activityTypeSpinner:Spinner
    lateinit var activityNameET:EditText
    lateinit var startTimeButton: Button
    lateinit var endTimeButton:Button
    lateinit var activityLocationET:EditText
    lateinit var addAllSoldiersButton:Button
    lateinit var addSomeSoldiersButton: Button
    lateinit var participantsTV:TextView
    lateinit var saveActivityButton:Button

    var dateOfActivity:String = ""
    var activityStartTime:String = ""
    var activityEndTime:String = ""

    var allAttending = false
    var someAttending = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.a_fragment_add_new_activity,container,false)
        viewModel = ViewModelProvider(requireActivity()).get(ActivitiesViewModel::class.java)

        setUpViews(view)
        setUpObservers()
        return view
    }

    private fun setUpViews(view: View){
        view.apply {
            activityTypeSpinner = findViewById(R.id.typeOfActivitySpinner)
            activityNameET = findViewById(R.id.activityNameET)
            startTimeButton = findViewById(R.id.activityStartTimeButton)
            endTimeButton = findViewById(R.id.activityEndTimeButton)
            activityLocationET = findViewById(R.id.activityLocationET)
            addAllSoldiersButton = findViewById(R.id.addAllSoldiersButton)
            addSomeSoldiersButton = findViewById(R.id.addSomeSoldiersButton)
            participantsTV = findViewById(R.id.whoIsAttendingTV)
            saveActivityButton = findViewById(R.id.saveActivityButton)
        }
    }

    private fun setUpObservers(){

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveActivityButton.setOnClickListener {
            var type = activityTypeSpinner.selectedItem.toString()
            var name = activityNameET.text.toString()
            var date = dateOfActivity
            var startTime = activityStartTime
            var endTime = activityEndTime
            var location = activityLocationET.text.toString()
            var listOfSoldiers = getSoldiersAttending(allAttending,someAttending)
            if(type.isEmpty()||name.isEmpty()||date.isEmpty()||startTime.isEmpty()||endTime.isEmpty()){
                alertUserError(type,name,date,startTime,endTime)
            }else {
                val newArmyActivity = ArmyActivity(type, name, date, startTime, endTime, location,listOfSoldiers)
                if(armyActivity==null){
                    // TODO: 8/18/2021 add activity
                }else{
                    // TODO: 8/18/2021 swap activity
                }
            }
        }
    }
    private fun alertUserError(t:String,n:String,d:String,s:String,e:String){
        // TODO: 8/18/2021 implement errors
        if(t.isEmpty()){

        }
        if(n.isEmpty()){

        }
        if(d.isEmpty()){

        }
        if(s.isEmpty()){

        }
        if(e.isEmpty()){

        }

    }

    private fun getSoldiersAttending(all:Boolean, some:Boolean):List<String>{
        if(all){
            // TODO: 8/18/2021 get names
        }
        if (some){

        }
        return emptyList()
    }


    companion object{
        fun newInstance(armyActivity: ArmyActivity?):AddOrEditNewActivityFragment{
            return AddOrEditNewActivityFragment(armyActivity)
        }
    }
}