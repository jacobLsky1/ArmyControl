package com.jacoblip.andriod.armycontrol.views.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.ArmyActivity
import com.jacoblip.andriod.armycontrol.data.models.ArmyDay
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.views.soldiers.MainSoldiersFragment
import org.w3c.dom.Text
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class SoldiersOperationalListAdapter(var soldiers:List<Soldier>, var callbacks: MainSoldiersFragment.SoldierCallbacks, var callBacks2: MainSoldiersFragment.SoldierSelectedFromRV?,var armyDays:List<ArmyDay>):RecyclerView.Adapter<SoldierItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoldierItemViewHolder {
        when(viewType){
        }
        return SoldierItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.s_item_soilder, parent, false))
    }

    override fun getItemCount() = soldiers.size


    override fun onBindViewHolder(holder: SoldierItemViewHolder, position: Int) {
        var soldier = soldiers[position]
        var amountOfActivitiesCompleted = 0
        var amoutOfActivitiesSoldierCompleted = 0
        var today = LocalDate.now()
        today = today.plusDays(1)

        for(day in armyDays){
            val dayDate = LocalDate.parse(day.date)
        //    if(dayDate.isBefore(today)) {
                if (day.listOfSoldiers.contains(soldier.idNumber)) {
                    for (activity in day.activities) {
                        if (activity.completed) {
                            amountOfActivitiesCompleted++
                        }
                        if (activity.attendees.contains(soldier.idNumber) && activity.completed) {
                            amoutOfActivitiesSoldierCompleted++
                        }
                    }
                }
         //   }
        }

        var per = amountOfActivitiesCompleted - amoutOfActivitiesSoldierCompleted
        var degree = 0
        if(per>1){
            degree = 1
        }
        if(per>3){
            degree = 2
        }
        if(per>5){
            degree = 3
        }
        holder.itemView.apply {

            var nameTV = findViewById<TextView>(R.id.soldierNameTV)
            var idNumberTV = findViewById<TextView>(R.id.IdNumberTV)
            var phoneTV = findViewById<TextView>(R.id.phoneNumberTV)
            var activitiesTV = findViewById<TextView>(R.id.amountOfActivitiesPassedTV)
            var icon = findViewById<ImageView>(R.id.soldierIcon)

            nameTV.text = soldier.name
            idNumberTV.text = soldier.idNumber
            phoneTV.text = Util.getPositionByCode(soldier.positionMap)
            activitiesTV.visibility = View.VISIBLE
            activitiesTV.text = " פעילויות שהושלמו:\n${amoutOfActivitiesSoldierCompleted}/${amountOfActivitiesCompleted}"


            when(degree){
                0->icon.setColorFilter(ContextCompat.getColor(context, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN)
                1->icon.setColorFilter(ContextCompat.getColor(context, R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
                2->icon.setColorFilter(ContextCompat.getColor(context, R.color.orange), android.graphics.PorterDuff.Mode.SRC_IN)
                3->icon.setColorFilter(ContextCompat.getColor(context, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN)
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