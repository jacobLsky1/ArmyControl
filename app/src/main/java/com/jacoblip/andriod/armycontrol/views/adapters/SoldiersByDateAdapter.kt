package com.jacoblip.andriod.armycontrol.views.adapters

import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.ArmyDay
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.views.activities.MainActivitiesFragment
import java.time.LocalDate

class SoldiersByDateAdapter(var armyDays:List<ArmyDay>,var mySoldiers:List<Soldier>,var dayCallBacks:MainActivitiesFragment.OnDayLongPressCallBacks?):RecyclerView.Adapter<ArmyDateBySoldiersItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArmyDateBySoldiersItemViewHolder {
        when(viewType){
            0->{
                return ArmyDateBySoldiersItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.blank, parent, false))
            }
            1->{
                return ArmyDateBySoldiersItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.s_item_amount_of_soldiers_by_date, parent, false))
            }
        }
        return ArmyDateBySoldiersItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.s_item_amount_of_soldiers_by_date, parent, false))
    }

    override fun getItemCount() = armyDays.size+2

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ArmyDateBySoldiersItemViewHolder, position: Int) {
        if(position != 0 && position != armyDays.size+1) {
            val armyDay = armyDays[position - 1]
            holder.itemView.apply {
                var x = 0
                for (i in mySoldiers.indices) {
                    val soldier = mySoldiers[i]
                    for (j in armyDay.listOfSoldiers.indices) {
                        val soldierInDay = armyDay.listOfSoldiers[j]
                        if (soldierInDay == soldier.idNumber)
                            x++
                    }
                }
                this.setBackgroundColor(resources.getColor(R.color.darkGray))
                var dateTV = findViewById<TextView>(R.id.armyDayDateTV)
                var dayTV = findViewById<TextView>(R.id.armyDaryDayOfWeek)
                var container = findViewById<ConstraintLayout>(R.id.dateContainer)
                var amountOfSoldiersTV = findViewById<TextView>(R.id.amountOfSoldiersPerDayTV)
                var localDate = LocalDate.parse(armyDay.date)
                dayTV.text = Util.getDayOfWeek(localDate.dayOfWeek.toString())
                dateTV.text = armyDay.date
                amountOfSoldiersTV.text = "${x}/${mySoldiers.size}"

                var today = LocalDate.now()
                if(localDate.isBefore(today)){
                    container.setBackgroundColor(resources.getColor(R.color.sand))
                }else{
                    if(localDate.isAfter(today)){
                        container.setBackgroundColor(resources.getColor(R.color.sand))
                    }else{
                        container.setBackgroundColor(resources.getColor(R.color.dayToday))
                    }
                }


                var clicked = false
                setOnClickListener {
                    if (!clicked) {
                        Util.currentDate.postValue(armyDay)
                        clicked = true
                    } else {
                        Util.currentDate.postValue(null)
                        clicked = false
                    }
                }

                if(dayCallBacks!=null&&Util.userCommandPath=="1"){
                    setOnLongClickListener {
                        dayCallBacks!!.onDayLongPress(armyDay)
                        true
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(position==0||position==armyDays.size+1)
            return 0

        return 1
    }


}