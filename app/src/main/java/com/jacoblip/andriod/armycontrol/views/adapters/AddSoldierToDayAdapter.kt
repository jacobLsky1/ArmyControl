package com.jacoblip.andriod.armycontrol.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import io.realm.internal.Util
import kotlinx.coroutines.awaitAll

class AddSoldierToDayAdapter (var allSolders:List<Soldier>,var checkEveryOne:Boolean): RecyclerView.Adapter<SoldierItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoldierItemViewHolder {
        when(viewType){
        }
        Log.i("Adapter","CreateViewHolder")
        return SoldierItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.a_item_soldier_for_day, parent, false))
    }

    override fun getItemCount():Int {
        return allSolders.size
    }

    override fun onBindViewHolder(holder: SoldierItemViewHolder, position: Int) {
        val soldier = allSolders[position]
        holder.itemView.apply {
            val nameCB = findViewById<CheckBox>(R.id.nameCheckBox)
            val stationMap = findViewById<TextView>(R.id.stationMapTV)
            val idNumber = findViewById<TextView>(R.id.idNumberTV)
            val pakal = findViewById<TextView>(R.id.listOfPakalimTV)

            nameCB.text = soldier.name
            stationMap.text = com.jacoblip.andriod.armycontrol.utilities.Util.getPositionByCode(soldier.positionMap)
            idNumber.text = com.jacoblip.andriod.armycontrol.utilities.Util.getArmyJobByCode(soldier.armyJobMap)
            pakal.text = soldier.pakal.toString()

            nameCB.isChecked = checkEveryOne
            if(checkEveryOne){
                com.jacoblip.andriod.armycontrol.utilities.Util.soldiersToAdd.add(soldier)
            }

            nameCB.setOnCheckedChangeListener { checkBox, isChecked ->
                if (isChecked) {
                    com.jacoblip.andriod.armycontrol.utilities.Util.soldiersToAdd.add(soldier)
                } else {
                    if (com.jacoblip.andriod.armycontrol.utilities.Util.soldiersToAdd.contains(soldier)
                    )
                        com.jacoblip.andriod.armycontrol.utilities.Util.soldiersToAdd.remove(soldier)
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