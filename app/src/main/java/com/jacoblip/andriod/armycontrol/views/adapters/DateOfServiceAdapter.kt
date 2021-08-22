package com.jacoblip.andriod.armycontrol.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jacoblip.andriod.armycontrol.R

class DateOfServiceAdapter(var daysInService:List<String>):RecyclerView.Adapter<ArmyDateBySoldiersItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArmyDateBySoldiersItemViewHolder {
        when(viewType){
        }
        Log.i("Adapter","CreateViewHolder")
        return ArmyDateBySoldiersItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.s_item_date_of_service, parent, false))
    }

    override fun getItemCount():Int {
       return 5
    }

    override fun onBindViewHolder(holder: ArmyDateBySoldiersItemViewHolder, position: Int) {

        holder.itemView.apply {

        }
    }

    override fun getItemViewType(position: Int): Int {
        when(position){

        }
        return 0
    }


}