package com.jacoblip.andriod.armycontrol.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.ArmyActivity
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.views.soldiers.MainSoldiersFragment

class SoldiersOperationalListAdapter(var soldiers:List<Soldier>, var callbacks: MainSoldiersFragment.SoldierCallbacks, var callBacks2: MainSoldiersFragment.SoldierSelectedFromRV?,var passedActivities:List<ArmyActivity>):RecyclerView.Adapter<SoldierItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoldierItemViewHolder {
        when(viewType){
        }
        return SoldierItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.s_item_soilder, parent, false))
    }

    override fun getItemCount() = soldiers.size

    override fun onBindViewHolder(holder: SoldierItemViewHolder, position: Int) {
        var soldier = soldiers[position]
        var amountOfActivitiesCompleted = passedActivities.size
        var amoutOfActivitiesSoldierCompleted = 0
        for(i in soldier.Activates.indices){
            val activity = soldier.Activates[i]
            if(passedActivities.contains(activity))
                amoutOfActivitiesSoldierCompleted++
        }
        var per = amountOfActivitiesCompleted - amoutOfActivitiesSoldierCompleted
        var degree = 0
        if(per>2){
            degree = 1
        }
        if(per>4){
            degree = 2
        }
        if(per>6){
            degree = 3
        }
        holder.itemView.apply {

            var nameTV = findViewById<TextView>(R.id.soldierNameTV)
            var idNumberTV = findViewById<TextView>(R.id.IdNumberTV)
            var usageTV = findViewById<TextView>(R.id.phoneNumberTV)

            nameTV.text = soldier.name
            idNumberTV.text = soldier.idNumber
            usageTV.text = soldier.phoneNumber

            var soldierContainer:ConstraintLayout = findViewById(R.id.soldierContainer)
            when(degree){
                0->soldierContainer.setBackgroundColor(resources.getColor(R.color.green))
                1->soldierContainer.setBackgroundColor(resources.getColor(R.color.yellow))
                2->soldierContainer.setBackgroundColor(resources.getColor(R.color.orange))
                3->soldierContainer.setBackgroundColor(resources.getColor(R.color.red))
            }
            setOnClickListener {
                callbacks.onSoldierSelectedSelected(soldier, callbacks, callBacks2)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        when(position){

        }
        return 0
    }


}