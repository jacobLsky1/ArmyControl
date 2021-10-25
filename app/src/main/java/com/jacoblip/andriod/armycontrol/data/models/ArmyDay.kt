package com.jacoblip.andriod.armycontrol.data.models

import android.os.Build
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.util.*
@RequiresApi(Build.VERSION_CODES.O)
data class  ArmyDay(
    var date:String = "",
    var activities:List<ArmyActivity> = listOf(),
    var listOfSoldiers:List<String> = listOf()
){
}