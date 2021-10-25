package com.jacoblip.andriod.armycontrol.data.models

import androidx.annotation.Keep

data class Group(
    var groupType: GroupType = GroupType.פלוגה,
    var groupName: String =  "פלוגה ב" ,
    var subGroups:List<Group>? = listOf(),
    var amountOfSoldiers:Int = 0
) {
}