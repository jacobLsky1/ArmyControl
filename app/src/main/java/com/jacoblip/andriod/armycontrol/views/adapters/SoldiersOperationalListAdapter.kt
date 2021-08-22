package com.jacoblip.andriod.armycontrol.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.views.soldiers.MainSoldiersFragment

class SoldiersOperationalListAdapter(var soldiers:List<Soldier>, var callbacks: MainSoldiersFragment.SoldierCallbacks, var callBacks2: MainSoldiersFragment.SoldierSelectedFromRV?):RecyclerView.Adapter<SoldierItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoldierItemViewHolder {
        when(viewType){
        }
        return SoldierItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.s_item_soilder, parent, false))
    }

    override fun getItemCount() = soldiers.size

    override fun onBindViewHolder(holder: SoldierItemViewHolder, position: Int) {
        var soldier = soldiers[position]
        holder.itemView.apply {
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