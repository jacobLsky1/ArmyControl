package com.jacoblip.andriod.armycontrol.views.adapters

import android.content.Context
import android.graphics.Color
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

class AddSoldierToDayAdapter(var context: Context, var allSolders:List<Soldier>, var checkEveryOne:Boolean, var addToActivity:Boolean,var existingSoldiers:List<String>?):BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var soldiersChecked = mutableListOf<Int>()
    override fun getCount():Int{
        if(checkEveryOne){
            for(i in 0 until allSolders.size){
                soldiersChecked.add(i)
            }
        }
        return allSolders.size
    }

    override fun getItem(p0: Int): Any {
       return allSolders[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, p1: View?, parent: ViewGroup?): View {
        var soldier = getItem(position) as Soldier
        val view = inflater.inflate(R.layout.a_item_soldier_for_day, parent, false)
        view.apply {
            val nameCB = findViewById<CheckBox>(R.id.nameCheckBox)
            val stationMap = findViewById<TextView>(R.id.stationMapTV)
            val idNumber = findViewById<TextView>(R.id.idNumberTV)
            val pakal = findViewById<TextView>(R.id.listOfPakalimTV)

            nameCB.apply {
                text = soldier.name
                setTextColor(Color.WHITE)
            }

            stationMap.apply {
                text = com.jacoblip.andriod.armycontrol.utilities.Util.getPositionByCode(soldier.positionMap)
                setTextColor(Color.WHITE)
            }
            idNumber.apply {
                text = com.jacoblip.andriod.armycontrol.utilities.Util.getArmyJobByCode(soldier.armyJobMap)
                setTextColor(Color.WHITE)
            }
            pakal.apply {
                text = soldier.pakal.toString()
                setTextColor(Color.WHITE)
            }

            if(existingSoldiers==null)
            nameCB.isChecked = soldiersChecked.contains(position)
            else{
                for(mySoldier in existingSoldiers!!){
                    if(soldier.idNumber==mySoldier){
                        nameCB.isChecked = true
                    }
                }
            }
            nameCB.setOnCheckedChangeListener{it,isChecked->
                if(isChecked){
                    soldiersChecked.add(position)
                    AddingSoldierHelper.soldiersToAdd.add(soldier.idNumber)
                }else{
                    soldiersChecked.remove(position)
                    AddingSoldierHelper.soldiersToAdd.remove(soldier.idNumber)
                }
            }
        }

        return view
    }
}