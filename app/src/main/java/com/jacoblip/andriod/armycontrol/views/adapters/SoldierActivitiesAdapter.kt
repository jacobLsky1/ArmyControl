package com.jacoblip.andriod.armycontrol.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.ArmyActivity
import com.jacoblip.andriod.armycontrol.views.activities.MainActivitiesFragment

class SoldierActivitiesAdapter(var armyActivities:List<ArmyActivity>): RecyclerView.Adapter<ArmyDateBySoldiersItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArmyDateBySoldiersItemViewHolder {
        when(viewType){
        }
        Log.i("Adapter","CreateViewHolder")
        return ArmyDateBySoldiersItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.s_item_soldier_activity, parent, false))
    }

    override fun getItemCount():Int {
        return armyActivities.size
    }

    override fun onBindViewHolder(holder: ArmyDateBySoldiersItemViewHolder, position: Int) {
        val armyActivity = armyActivities[position]
        holder.itemView.apply {
           var nameTV = findViewById<TextView>(R.id.itemSoldierActivityNameTV)
           var dateTV = findViewById<TextView>(R.id.itemSoldierActivityDateTV)
           var passedIV = findViewById<ImageView>(R.id.itemSoldierActivityCompletedIV)

            nameTV.text = "${armyActivity.type} - ${armyActivity.name}"
            dateTV.text = armyActivity.date
            if(armyActivity.completed){
                passedIV.setImageResource(R.drawable.ic_baseline_check_24)
            }else{
                passedIV.setImageResource(R.drawable.ic_baseline_close_24)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {

        when(position){

        }
        return 0
    }


}