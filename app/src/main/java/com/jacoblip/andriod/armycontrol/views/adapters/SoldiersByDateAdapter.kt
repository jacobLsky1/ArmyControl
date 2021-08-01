package com.jacoblip.andriod.armycontrol.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.ArmyDay

class SoldiersByDateAdapter(var armyDays:List<ArmyDay>):RecyclerView.Adapter<ArmyDateBySoldiersItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArmyDateBySoldiersItemViewHolder {
        when(viewType){
        }
        Log.i("Adapter","CreateViewHolder")
        return ArmyDateBySoldiersItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_amount_of_soldiers_by_date, parent, false))
    }

    override fun getItemCount() = 10

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