package com.jacoblip.andriod.armycontrol.views.adapters

import android.graphics.Color
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.views.soldiers.MainSoldiersFragment

class SoldiersAllSoldiersAdapter(var soldiers: List<Soldier>, var callbacks: MainSoldiersFragment.SoldierCallbacks?, var soldierSelectedCallbacks: MainSoldiersFragment.SoldierSelectedFromRV?):RecyclerView.Adapter<SoldierItemViewHolder>() {

    var listOfSoldiers:MutableList<Soldier> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoldierItemViewHolder {
        when(viewType){
        }
        return SoldierItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.s_item_soilder, parent, false))
    }

    override fun getItemCount() = soldiers.size

    override fun onBindViewHolder(holder: SoldierItemViewHolder, position: Int) {
        var soldier = soldiers[position]
        holder.itemView.apply {
            var nameTV = findViewById<TextView>(R.id.soldierNameTV)
            var idNumberTV = findViewById<TextView>(R.id.IdNumberTV)
            var phoneTV = findViewById<TextView>(R.id.phoneNumberTV)
            var checkedImageView = findViewById<ImageView>(R.id.checkedImageView)
            var isHereTV = findViewById<TextView>(R.id.amountOfActivitiesPassedTV)
            var stationMapTV = findViewById<TextView>(R.id.soldierStationMapTV)
            stationMapTV.visibility = View.VISIBLE

            nameTV.text = soldier.name
            idNumberTV.text = soldier.idNumber
            phoneTV.text = soldier.phoneNumber
            Linkify.addLinks(phoneTV, Linkify.PHONE_NUMBERS);
            phoneTV.linksClickable = true;
            stationMapTV.text = Util.getPositionByCode(soldier.positionMap)

            if(soldier.hasArrived){
                isHereTV.visibility = View.VISIBLE
                isHereTV.text = "נוכח"
                isHereTV.setTextColor(Color.GREEN)
            }else{
                isHereTV.visibility = View.VISIBLE
                isHereTV.text = "לא נוכח"
                isHereTV.setTextColor(Color.RED)
            }

            if(callbacks!=null&&soldierSelectedCallbacks!=null) {
                setOnLongClickListener {
                    if (!Util.inSelectionMode.value!! && !listOfSoldiers.contains(soldier)) {
                        Util.inSelectionMode.postValue(true)
                        soldierSelectedCallbacks!!.onSoldierSelectedFromRV(soldier, true)
                        checkedImageView.visibility = View.VISIBLE
                        listOfSoldiers.add(soldier)
                    }
                    true
                }
                setOnClickListener {
                    if (Util.inSelectionMode.value!!) {
                        if (!listOfSoldiers.contains(soldier)) {
                            listOfSoldiers.add(soldier)
                            soldierSelectedCallbacks!!.onSoldierSelectedFromRV(soldier, true)
                            checkedImageView.visibility = View.VISIBLE
                        } else {
                            listOfSoldiers.remove(soldier)
                            soldierSelectedCallbacks!!.onSoldierSelectedFromRV(soldier, false)
                            checkedImageView.visibility = View.GONE
                        }
                    } else {

                        callbacks!!.onSoldierSelectedSelected(soldier, callbacks!!, soldierSelectedCallbacks
                        )
                    }
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