package com.jacoblip.andriod.armycontrol.data.models

import java.util.*

data class Soldier(
        var name:String = "",
        var idNumber:String = "",
        var age:String = "",
        var medicalProblems:String = "",
        var hasArrived:Boolean = false,
        var whyNotArriving:String = "",
        var listOfDatesInService:List<Date> = listOf(),
        var phoneNumber:String = "",
        var civilianJob:String = "",
        var armyJobMap:String = "",
        var positionMap:String = "",
        var isCommander:Boolean = false,
        var isLieutenant:Boolean = false,
        var pakal:List<String> = listOf(),
        var ActivatesPassed:List<ArmyActivity> = listOf()
) {
}