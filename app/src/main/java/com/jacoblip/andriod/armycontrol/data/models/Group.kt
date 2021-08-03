package com.jacoblip.andriod.armycontrol.data.models

data class Group(
    var groupType: GroupType = GroupType.פלוגה,
    var groupName: String =  "פלוגה ב" ,
    var subGroups:List<Group>? = listOf(),
    var amountOfSoldiers:Int = 0,
    var amountOfSoldiersAtendent: Int = 0
) {
}