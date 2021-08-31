package com.jacoblip.andriod.armycontrol.data.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.util.*

data class  ArmyDay(
    var date:String = "",
    var activities:List<ArmyActivity> = listOf(),
    var amountOfSoldiers:List<Soldier> = listOf()
){

}