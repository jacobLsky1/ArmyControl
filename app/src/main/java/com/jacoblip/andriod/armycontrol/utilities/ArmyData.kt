package com.jacoblip.andriod.armycontrol.utilities

import com.jacoblip.andriod.armycontrol.data.models.Group
import com.jacoblip.andriod.armycontrol.data.models.GroupType
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import java.util.*

class ArmyData {
    companion object{
        val armyData = Group(GroupType.פלוגה,"פלוגה ב",
            listOf(
                Group(GroupType.מחלקה, "פלוגה ב מחלקה 1", listOf(
                    Group(GroupType.כיתה,"מחלקה 1 כיתה א",null,0,0),
                    Group(GroupType.כיתה,"מחלקה 1 כיתה ב",null,0,0),
                    Group(GroupType.כיתה,"מחלקה 1 כיתה ג",null,0,0)
                ),0,0),
                Group(GroupType.מחלקה, "פלוגה ב מחלקה 2",listOf(
                    Group(GroupType.כיתה,"מחלקה 2 כיתה א",null,0,0),
                    Group(GroupType.כיתה, "מחלקה 2 כיתה ב",null,0,0),
                    Group(GroupType.כיתה, "מחלקה 2 כיתה ג",null,0,0)
                ),0,0),
                Group(GroupType.מחלקה,"פלוגה ב מחלקה 3", listOf(
                    Group(GroupType.כיתה, "מחלקה 3 כיתה א",null,0,0),
                    Group(GroupType.כיתה, "מחלקה 3 כיתה ב",null,0,0),
                    Group(GroupType.כיתה, "מחלקה 3 כיתה ג",null,0,0)
                ),0,0),
                    Group(GroupType.מחלקה, "פלוגה ב מפלג",listOf(),0,0)
                )
            ,0,0)

        val listOfSoldiers = listOf<Soldier>()
    }
}