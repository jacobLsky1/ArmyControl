package com.jacoblip.andriod.armycontrol.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.databinding.AAddSoldiersForDayBinding.inflate
import com.jacoblip.andriod.armycontrol.utilities.AddingSoldierHelper

class AddSoldierToDayAdapter(var context: Context, var allSolders:List<Soldier>, var checkEveryOne:Boolean, var addToActivity:Boolean):BaseAdapter() {
    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    override fun getCount() = allSolders.size

    override fun getItem(p0: Int): Any {
       return allSolders[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, parent: ViewGroup?): View {
        var soldier = getItem(p0) as Soldier
        val view = inflater.inflate(R.layout.a_item_soldier_for_day, parent, false)
        view.apply {
            val nameCB = findViewById<CheckBox>(R.id.nameCheckBox)
            val stationMap = findViewById<TextView>(R.id.stationMapTV)
            val idNumber = findViewById<TextView>(R.id.idNumberTV)
            val pakal = findViewById<TextView>(R.id.listOfPakalimTV)

            nameCB.text = soldier.name
            stationMap.text = com.jacoblip.andriod.armycontrol.utilities.Util.getPositionByCode(soldier.positionMap)
            idNumber.text = com.jacoblip.andriod.armycontrol.utilities.Util.getArmyJobByCode(soldier.armyJobMap)
            pakal.text = soldier.pakal.toString()

            nameCB.isChecked = checkEveryOne
            nameCB.setOnCheckedChangeListener{it,isChecked->
                if(isChecked){
                    AddingSoldierHelper.soldiersToAdd.add(soldier)
                }else{
                    AddingSoldierHelper.soldiersToAdd.remove(soldier)
                }
            }
        }

        return view
    }
}