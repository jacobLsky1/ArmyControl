package com.jacoblip.andriod.armycontrol.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jacoblip.andriod.armycontrol.R


class SignedItemsAdapter(val signedItemsList: MutableList<String>) : RecyclerView.Adapter<SoldierItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoldierItemViewHolder {
        return SoldierItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.s_item_signed_item, parent, false))
    }

    override fun getItemCount() = signedItemsList.size

    override fun onBindViewHolder(holder: SoldierItemViewHolder, position: Int) {
        val item = signedItemsList[position]
        holder.itemView.apply {
            var tv = findViewById<TextView>(R.id.signedItemTV)
            tv.text = item
        }
    }

}
