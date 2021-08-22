package com.jacoblip.andriod.armycontrol.data.models

import android.provider.CalendarContract

data class ArmyActivity(
        var type:String = "",
        var name:String = "",
        var date:String = "",
        var startTime:String = "",
        var endTime:String = "",
        var location:String = "",
        var attendees: List<String> = listOf()
) {
}