package com.jacoblip.andriod.armycontrol.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.views.MainSoldiersFragment
import com.jacoblip.andriod.armycontrol.views.RVSoldiersFragment

class SoldiersAllSoldiersAdapter(var soldiers: List<Soldier>, var callbacks: MainSoldiersFragment.SoldierCallbacks, var soldierSelectedCallbacks: RVSoldiersFragment.SoldierSelectedFromRV?):RecyclerView.Adapter<SoldierItemViewHolder>() {

    var listOfSoldiers:MutableList<Soldier> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoldierItemViewHolder {
        when(viewType){
        }
        return SoldierItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_soilder, parent, false))
    }

    override fun getItemCount() = soldiers.size

    override fun onBindViewHolder(holder: SoldierItemViewHolder, position: Int) {
        var soldier = soldiers[position]
        holder.itemView.apply {
            var nameTV = findViewById<TextView>(R.id.soldierNameTV)
            var idNumberTV = findViewById<TextView>(R.id.IdNumberTV)
            var usageTV = findViewById<TextView>(R.id.phoneNumberTV)
            var checkedImageView = findViewById<ImageView>(R.id.checkedImageView)

            nameTV.text = soldier.name
            idNumberTV.text = soldier.idNumber
            usageTV.text = soldier.phoneNumber
            setOnLongClickListener {
                if (!Util.inSelectionMode.value!!&&!listOfSoldiers.contains(soldier)) {
                    Util.inSelectionMode.postValue(true)
                    soldierSelectedCallbacks!!.onSoldierSelected(soldier,true)
                    checkedImageView.visibility = View.VISIBLE
                    listOfSoldiers.add(soldier)
                }
                true
            }
            setOnClickListener {
                if(Util.inSelectionMode.value!!){
                    if(!listOfSoldiers.contains(soldier)) {
                        listOfSoldiers.add(soldier)
                        soldierSelectedCallbacks!!.onSoldierSelected(soldier, true)
                        checkedImageView.visibility = View.VISIBLE
                    }else {
                        listOfSoldiers.remove(soldier)
                        soldierSelectedCallbacks!!.onSoldierSelected(soldier, false)
                        checkedImageView.visibility = View.GONE
                    }
                }else{
                    callbacks.onSoldierSelectedSelected(soldier, callbacks,soldierSelectedCallbacks)
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