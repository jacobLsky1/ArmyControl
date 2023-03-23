package com.jacoblip.andriod.armycontrol.data.models

import com.bignerdranch.expandablerecyclerview.model.Parent
import java.io.Serializable

data class SoldierGroupForList(

    var items:List<ItemForSoldierList>? = null

): Serializable, Parent<ItemForSoldierList> {
    override fun getChildList() = items

    override fun isInitiallyExpanded() = false
}
