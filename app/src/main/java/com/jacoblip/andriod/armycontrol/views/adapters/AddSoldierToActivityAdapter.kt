package com.jacoblip.andriod.armycontrol.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.ArmyActivity
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.views.activities.MainActivitiesFragment

class AddSoldierToActivityAdapter(var activitySoldiers:List<Soldier>):RecyclerView.Adapter<ArmyDateBySoldiersItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArmyDateBySoldiersItemViewHolder {
        when(viewType){
        }
        Log.i("Adapter","CreateViewHolder")
        return ArmyDateBySoldiersItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.a_item_soldier_attending_activity, parent, false))
    }

    override fun getItemCount():Int {
       return activitySoldiers.size
    }

    override fun onBindViewHolder(holder: ArmyDateBySoldiersItemViewHolder, position: Int) {
        val soldier = activitySoldiers[position]
        holder.itemView.apply {
            var activityType = findViewById<TextView>(R.id.addSoldierActivityNameTV)
            var activityName = findViewById<TextView>(R.id.addSoldierActivityPositionTV)

            activityType.text = soldier.name
            activityName.text = Util.getPositionByCode(soldier.positionMap)
        }
    }

    override fun getItemViewType(position: Int): Int {

        when(position){

        }
        return 0
    }


}