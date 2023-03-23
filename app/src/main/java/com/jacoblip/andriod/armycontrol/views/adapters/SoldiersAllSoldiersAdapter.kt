package com.jacoblip.andriod.armycontrol.views.adapters

import android.graphics.Color
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.ddd.androidutils.DoubleClick
import com.ddd.androidutils.DoubleClickListener
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.views.soldiers.MainSoldiersFragment
import com.jacoblip.andriod.armycontrol.views.soldiers.RVSoldiersFragment

class SoldiersAllSoldiersAdapter(var soldiers: List<Soldier>, var callbacks: MainSoldiersFragment.SoldierCallbacks?, var soldierSelectedCallbacks: MainSoldiersFragment.SoldierSelectedFromRV?,var soldierAtedCallback:RVSoldiersFragment.SoldierArrivingCallbacks?):RecyclerView.Adapter<SoldierItemViewHolder>() {

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
            var hasSoldierArrivedButton = findViewById<Button>(R.id.hasArrivedButton)
            var icon = findViewById<ImageView>(R.id.soldierIcon)
            stationMapTV.visibility = View.VISIBLE
            hasSoldierArrivedButton.isVisible = soldierAtedCallback!=null
            isHereTV.visibility = View.INVISIBLE
            nameTV.text = soldier.name
            idNumberTV.text = soldier.idNumber
            phoneTV.text = soldier.phoneNumber
            Linkify.addLinks(phoneTV, Linkify.PHONE_NUMBERS);
            phoneTV.linksClickable = true;
            stationMapTV.text = Util.getPositionByCode(soldier.positionMap)

            if(soldier.isCommander){
                icon.setBackgroundResource(R.drawable.commander_pic);
            }else{
                icon.setBackgroundResource(R.drawable.soilder_pic);
            }

            hasSoldierArrivedButton.text = if(soldier.hasArrived)"נמצא" else "לא נמצא"
            hasSoldierArrivedButton.setTextColor(if(soldier.hasArrived)Color.GREEN else Color.RED)
            hasSoldierArrivedButton.setOnClickListener {
                var newSodiler = soldier
                newSodiler.hasArrived = !soldier.hasArrived
                hasSoldierArrivedButton.text = if(newSodiler.hasArrived)"נמצא" else "לא נמצא"
                hasSoldierArrivedButton.setTextColor(if(newSodiler.hasArrived)Color.GREEN else Color.RED)
                soldierAtedCallback!!.soldierAttendantsChange(soldier,newSodiler)
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