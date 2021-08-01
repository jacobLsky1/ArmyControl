package com.jacoblip.andriod.armycontrol.data.models

import com.google.firebase.database.DatabaseReference

data class Refrences(
        var refrences: DatabaseReference? = null,
        var listOfChildRefrences:List<Refrences>? = null
) {
}