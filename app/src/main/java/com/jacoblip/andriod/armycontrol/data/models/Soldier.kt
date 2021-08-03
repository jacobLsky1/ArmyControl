package com.jacoblip.andriod.armycontrol.data.models

import java.util.*

data class Soldier(
    var name:String = "",
    var idNumber:String = "",
    var age:Int = 0,
    var medicalProblems:String = "",
    var hasArrived:Boolean = false,
    var whyNotArriving:String = "",
    var listOfDatesInService:List<Date> = listOf(),
    var phoneNumber:String = "",
    var civilianJob:String = "",
    var usage:String = "",
    var stationMap:String = "",
    var isCommander:Boolean = false,
    var ActivatesPassed:List<ArmyActivity> = listOf()
) {
}