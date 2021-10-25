package com.jacoblip.andriod.armycontrol.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.ArmyActivity
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.views.activities.MainActivitiesFragment

class ArmyActivityAdapter(var armyActivities:List<ArmyActivity>,var personalSoldiers:List<Soldier>,var callbacks:MainActivitiesFragment.OnActivityPressedCallBacks?,var longPressCallBacks:MainActivitiesFragment.OnActivitySelectedFromRVCallBacks?):RecyclerView.Adapter<ArmyDateBySoldiersItemViewHolder>() {
    var listOfActivities:MutableList<ArmyActivity> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArmyDateBySoldiersItemViewHolder {
        when(viewType){
        }
        Log.i("Adapter","CreateViewHolder")
        return ArmyDateBySoldiersItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.s_item_army_activity, parent, false))
    }

    override fun getItemCount():Int {
       return armyActivities.size
    }

    override fun onBindViewHolder(holder: ArmyDateBySoldiersItemViewHolder, position: Int) {
        val armyActivity = armyActivities[position]
        var amountOfSoldiersAttending = 0
        for(i in personalSoldiers.indices){
            val soldier = personalSoldiers[i]
            if(armyActivity.attendees.contains(soldier.idNumber))
                amountOfSoldiersAttending++
        }
        holder.itemView.apply {
            var activityType = findViewById<TextView>(R.id.itemArmyActivityType)
            var activityName = findViewById<TextView>(R.id.itemArmyActivityName)
            var activityLocation = findViewById<TextView>(R.id.itemArmyActivityLocation)
            var activityDate = findViewById<TextView>(R.id.itemArmyActvityDate)
            var activityTime = findViewById<TextView>(R.id.itemArmyActivityStart)
            var activityAttendees = findViewById<TextView>(R.id.itemArmyActivityAttendees)
            var checkedImageView = findViewById<ImageView>(R.id.checkedActivityImageView)

            activityType.text = armyActivity.type
            activityName.text = armyActivity.name
            activityLocation.text = armyActivity.location
            activityDate.text = armyActivity.date
            activityTime.text = "${armyActivity.endTime} - ${armyActivity.startTime}"
            activityAttendees.text = "כמות חיילים : ${amountOfSoldiersAttending}"

            if(callbacks!=null&&longPressCallBacks!=null) {
                setOnClickListener {
                    if (Util.inSelectionMode.value!!) {
                        if (!listOfActivities.contains(armyActivity)) {
                            listOfActivities.add(armyActivity)
                            longPressCallBacks!!.onActivitySelectedFromRV(armyActivity, true)
                            checkedImageView.visibility = View.VISIBLE
                        } else {
                            listOfActivities.remove(armyActivity)
                            longPressCallBacks!!.onActivitySelectedFromRV(armyActivity, false)
                            checkedImageView.visibility = View.GONE
                        }
                    } else {
                        callbacks!!.onActivityPressed(armyActivity)
                    }
                }

                setOnLongClickListener {
                    if (!Util.inSelectionMode.value!! && !listOfActivities.contains(armyActivity)) {
                        Util.inSelectionMode.postValue(true)
                        longPressCallBacks!!.onActivitySelectedFromRV(armyActivity, true)
                        checkedImageView.visibility = View.VISIBLE
                        listOfActivities.add(armyActivity)
                    }
                    true
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {

        when(position){

        }
        return 0
    }


}