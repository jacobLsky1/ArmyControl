package com.jacoblip.andriod.armycontrol.data.models

import android.os.Build
import android.provider.CalendarContract
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.util.*


data class ArmyActivity(
        var type:String = "",
        var name:String = "",
        var date:String = "",
        var startTime:String = "",
        var endTime:String = "",
        var location:String = "",
        var attendees: List<String> = listOf(),
        var completed:Boolean = false
){
}