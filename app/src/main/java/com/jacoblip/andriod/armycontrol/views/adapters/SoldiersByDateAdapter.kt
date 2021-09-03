package com.jacoblip.andriod.armycontrol.views.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.ArmyDay
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.utilities.Util

class SoldiersByDateAdapter(var armyDays:List<ArmyDay>,var mySoldiers:List<Soldier>):RecyclerView.Adapter<ArmyDateBySoldiersItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArmyDateBySoldiersItemViewHolder {
        when(viewType){
        }
        Log.i("Adapter","CreateViewHolder")
        return ArmyDateBySoldiersItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.s_item_amount_of_soldiers_by_date, parent, false))
    }

    override fun getItemCount() = armyDays.size

    override fun onBindViewHolder(holder: ArmyDateBySoldiersItemViewHolder, position: Int) {
        val armyDay = armyDays[position]
        holder.itemView.apply {
            var x = 0
            for(i in mySoldiers.indices){
                val soldier = mySoldiers[i]
                for(j in armyDay.amountOfSoldiers.indices){
                    val soldierInDay = armyDay.amountOfSoldiers[j]
                    if(soldierInDay.idNumber==soldier.idNumber)
                        x++
                }
            }
            this.setBackgroundColor(resources.getColor(R.color.darkGray))
            var dateTV = findViewById<TextView>(R.id.armyDayDateTV)
            var amountOfSoldiersTV = findViewById<TextView>(R.id.amountOfSoldiersPerDayTV)

            dateTV.text = armyDay.date
            amountOfSoldiersTV.text = "${x}/${mySoldiers.size}"

            var clicked = false
                setOnClickListener {
                    if(!clicked) {
                        Util.currentDate.postValue(armyDay)
                        clicked = true
                    }else{
                        Util.currentDate.postValue(null)
                        clicked = false
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