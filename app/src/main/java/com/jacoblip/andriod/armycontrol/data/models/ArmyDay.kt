package com.jacoblip.andriod.armycontrol.data.models

import java.util.*

data class ArmyDay(
    var date:String = "",
    var activities:List<ArmyActivity>,
    var amountOfSoldiers:Int = 0
) {
}