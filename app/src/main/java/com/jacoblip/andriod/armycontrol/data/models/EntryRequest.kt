package com.jacoblip.andriod.armycontrol.data.models

import android.view.View
import androidx.annotation.Keep

data class EntryRequest(
    var view: View? = null,
    var email:String = "",
    var emailPassword:String = "",
    var userId:String = "",
    var userCode:String = "",
    var groupId:String = ""
    ) {
}