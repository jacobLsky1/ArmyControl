package com.jacoblip.andriod.armycontrol.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.ArmyActivity
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.views.soldiers.MainSoldiersFragment
import org.w3c.dom.Text

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

            nameTV.text = soldier.name
            idNumberTV.text = soldier.idNumber
            phoneTV.text = Util.getPositionByCode(soldier.positionMap)
            activitiesTV.visibility = View.VISIBLE
            activitiesTV.text = " פעילויות שהושלמו -${amoutOfActivitiesSoldierCompleted}/${amountOfActivitiesCompleted}"

            nameTV.setTextColor(resources.getColor(R.color.black))
            idNumberTV.setTextColor(resources.getColor(R.color.black))
            phoneTV.setTextColor(resources.getColor(R.color.black))
            activitiesTV.setTextColor(resources.getColor(R.color.black))

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