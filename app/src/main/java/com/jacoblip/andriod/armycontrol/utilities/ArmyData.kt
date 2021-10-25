package com.jacoblip.andriod.armycontrol.utilities

import com.jacoblip.andriod.armycontrol.data.models.Group
import com.jacoblip.andriod.armycontrol.data.models.GroupType
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import java.util.*

class ArmyData {
    companion object{
        val armyData0 = Group(GroupType.פלוגה,"פלוגה א",
            listOf(
                Group(GroupType.מחלקה, "פלוגה א מחלקה 1", listOf(
                    Group(GroupType.כיתה,"מחלקה 1 כיתה א",null,0),
                    Group(GroupType.כיתה,"מחלקה 1 כיתה ב",null,0),
                    Group(GroupType.כיתה,"מחלקה 1 כיתה ג",null,0)
                ),0),
                Group(GroupType.מחלקה, "פלוגה א מחלקה 2",listOf(
                    Group(GroupType.כיתה,"מחלקה 2 כיתה א",null,0),
                    Group(GroupType.כיתה, "מחלקה 2 כיתה ב",null,0),
                    Group(GroupType.כיתה, "מחלקה 2 כיתה ג",null,0)
                ),0),
                Group(GroupType.מחלקה,"פלוגה א מחלקה 3", listOf(
                    Group(GroupType.כיתה, "מחלקה 3 כיתה א",null,0),
                    Group(GroupType.כיתה, "מחלקה 3 כיתה ב",null,0),
                    Group(GroupType.כיתה, "מחלקה 3 כיתה ג",null,0)
                ),0),
                    Group(GroupType.מחלקה, "פלוגה א מפלג",listOf(),0)
                )
            ,0)

        val armyData1 = Group(GroupType.פלוגה,"פלוגה ב",
            listOf(
                Group(GroupType.מחלקה, "פלוגה ב מחלקה 1", listOf(
                    Group(GroupType.כיתה,"מחלקה 1 כיתה א",null,0),
                    Group(GroupType.כיתה,"מחלקה 1 כיתה ב",null,0),
                    Group(GroupType.כיתה,"מחלקה 1 כיתה ג",null,0)
                ),0),
                Group(GroupType.מחלקה, "פלוגה ב מחלקה 2",listOf(
                    Group(GroupType.כיתה,"מחלקה 2 כיתה א",null,0),
                    Group(GroupType.כיתה, "מחלקה 2 כיתה ב",null,0),
                    Group(GroupType.כיתה, "מחלקה 2 כיתה ג",null,0)
                ),0),
                Group(GroupType.מחלקה,"פלוגה ב מחלקה 3", listOf(
                    Group(GroupType.כיתה, "מחלקה 3 כיתה א",null,0),
                    Group(GroupType.כיתה, "מחלקה 3 כיתה ב",null,0),
                    Group(GroupType.כיתה, "מחלקה 3 כיתה ג",null,0)
                ),0),
                Group(GroupType.מחלקה, "פלוגה ב מפלג",listOf(),0)
            )
            ,0)

        val armyData2 = Group(GroupType.פלוגה,"פלוגה ג",
            listOf(
                Group(GroupType.מחלקה, "פלוגה ג מחלקה 1", listOf(
                    Group(GroupType.כיתה,"מחלקה 1 כיתה א",null,0),
                    Group(GroupType.כיתה,"מחלקה 1 כיתה ב",null,0),
                    Group(GroupType.כיתה,"מחלקה 1 כיתה ג",null,0)
                ),0),
                Group(GroupType.מחלקה, "פלוגה ג מחלקה 2",listOf(
                    Group(GroupType.כיתה,"מחלקה 2 כיתה א",null,0),
                    Group(GroupType.כיתה, "מחלקה 2 כיתה ב",null,0),
                    Group(GroupType.כיתה, "מחלקה 2 כיתה ג",null,0)
                ),0),
                Group(GroupType.מחלקה,"פלוגה ג מחלקה 3", listOf(
                    Group(GroupType.כיתה, "מחלקה 3 כיתה א",null,0),
                    Group(GroupType.כיתה, "מחלקה 3 כיתה ב",null,0),
                    Group(GroupType.כיתה, "מחלקה 3 כיתה ג",null,0)
                ),0),
                Group(GroupType.מחלקה, "פלוגה ג מפלג",listOf(),0)
            )
            ,0)

        val armyData3 = Group(GroupType.פלוגה,"פלוגה מסייעת",
            listOf(
                Group(GroupType.מחלקה, "פל' מסייעת מחלקה 1", listOf(
                    Group(GroupType.כיתה,"מחלקה 1 כיתה א",null,0),
                    Group(GroupType.כיתה,"מחלקה 1 כיתה ב",null,0),
                    Group(GroupType.כיתה,"מחלקה 1 כיתה ג",null,0)
                ),0),
                Group(GroupType.מחלקה, "פל' מסייעת מחלקה 2",listOf(
                    Group(GroupType.כיתה,"מחלקה 2 כיתה א",null,0),
                    Group(GroupType.כיתה, "מחלקה 2 כיתה ב",null,0),
                    Group(GroupType.כיתה, "מחלקה 2 כיתה ג",null,0)
                ),0),
                Group(GroupType.מחלקה,"פל' מסייעת מחלקה 3", listOf(
                    Group(GroupType.כיתה, "מחלקה 3 כיתה א",null,0),
                    Group(GroupType.כיתה, "מחלקה 3 כיתה ב",null,0),
                    Group(GroupType.כיתה, "מחלקה 3 כיתה ג",null,0)
                ),0),
                Group(GroupType.מחלקה, "פל' מסייעת מפלג",listOf(),0)
            )
            ,0)

        val listOfSoldiers = listOf<Soldier>()

        val code0 = "5930278 8720395 1"
        val code1 = "8049909 9099408 1"
        val code2 = "5814513 3154185 1"
        val code3 = "5913690 0963195 1"


    }
}