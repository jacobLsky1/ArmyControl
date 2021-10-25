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
import com.jacoblip.andriod.armycontrol.views.soldiers.MainSoldiersFragment

class SoldiersPowerListAdapter(var soldiers: List<Soldier>, var callbacks: MainSoldiersFragment.SoldierCallbacks, var soldierSelectedCallbacks: MainSoldiersFragment.SoldierSelectedFromRV?):RecyclerView.Adapter<SoldierItemViewHolder>() {
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
            var usageTV = findViewById<TextView>(R.id.phoneNumberTV)
            var positionMap = findViewById<TextView>(R.id.amountOfActivitiesPassedTV)
            var imageView = findViewById<ImageView>(R.id.soldierIcon)

            imageView.visibility = View.GONE
            nameTV.text = soldier.name
            idNumberTV.text = "עיסוק אזרחי : ${soldier.civilianJob}"
            usageTV.text = soldier.pakal.toString()
            positionMap.visibility = View.VISIBLE
            positionMap.text = Util.getPositionByCode(soldier.positionMap)

            setOnClickListener {
                callbacks.onSoldierSelectedSelected(soldier, callbacks, soldierSelectedCallbacks)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        when(position){

        }
        return 0
    }


}