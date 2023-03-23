package com.jacoblip.andriod.armycontrol.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.views.soldiers.MainSoldiersFragment

class SoldiersCommandersAdapter(var soldiers: List<Soldier>, var callbacks: MainSoldiersFragment.SoldierCallbacks, var soldierSelectedCallbacks: MainSoldiersFragment.SoldierSelectedFromRV?):RecyclerView.Adapter<SoldierItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoldierItemViewHolder {
        when(viewType){
        }
        return SoldierItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.s_item_soilder, parent, false))
    }

    override fun getItemCount() = soldiers.size

    override fun onBindViewHolder(holder: SoldierItemViewHolder, position: Int) {

        var soldier = soldiers[position]
        holder.itemView.apply {
            var icon = findViewById<ImageView>(R.id.soldierIcon)
            var nameTV = findViewById<TextView>(R.id.soldierNameTV)
            var idNumberTV = findViewById<TextView>(R.id.IdNumberTV)
            var usageTV = findViewById<TextView>(R.id.phoneNumberTV)

            icon.setBackgroundResource(R.drawable.commander_pic);
            nameTV.text = soldier.name
            idNumberTV.text = Util.getPositionByCode(soldier.positionMap)
            usageTV.text = Util.getArmyJobByCode(soldier.armyJobMap)
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