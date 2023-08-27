package com.jacoblip.andriod.armycontrol.utilities

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.jacoblip.andriod.armycontrol.data.models.ArmyDay
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.views.adapters.SoldiersByDateAdapter

class Util {
    companion object{

        var inSelectionMode :MutableLiveData<Boolean> = MutableLiveData(false)
        var addFABY:MutableLiveData<Float> = MutableLiveData(0F)

        var userCommandPath:String = ""

        var hasInternet:MutableLiveData<Boolean> = MutableLiveData(true)

        val activityTypes = arrayOf("גיבוש","שטח בנוי","שטח פתוח","קליעה","קרב מגע","חינוך","תעסוקה מבצעית")

        val armyJobs = arrayOf("אלפה","חובש","מאג","מטול","מתשאל","נגב","נהג","פקל מפקד","פקל פריצה","צלף","קלע","קשר")


        fun getListOfArmyPositions():List<String>{
            val topPos = userCommandPath.split('.')
            when(topPos.size){
                1->{
                    return listOf("פיקוד הפלוגה","מפלג","מחלקה 1","מחלקה 2","מחלקה 3","כיתה א מח-1","כיתה ב מח-1","כיתה ג מח-1"
                        ,"כיתה א מח-2","כיתה ב מח-2","כיתה ג מח-2","כיתה א מח-3","כיתה ב מח-3","כיתה ג מח-3")
                }
                2->{
                    if(topPos[1]=="0"){
                        return listOf("מחלקה 1","כיתה א מח-1","כיתה ב מח-1","כיתה ג מח-1")
                    }
                    if(topPos[1]=="1"){
                        return listOf("מחלקה 2","כיתה א מח-2","כיתה ב מח-2","כיתה ג מח-2")
                    }
                    if(topPos[1]=="2"){
                        return listOf("מחלקה 3","כיתה א מח-3","כיתה ב מח-3","כיתה ג מח-3")
                    }

                }
                3->{
                    return listOf(getPositionByCode(userCommandPath))
                }
            }
            return emptyList()
        }


        fun getListOfCommanderPositions():List<String>{
            val topPos = userCommandPath.split('.')
            when(topPos.size){
                1->{
                    return listOf("מפקד פלוגה","סגן מפקד פלוגה","רספ","מפקד מחלקה 1","מפקד מחלקה 2","מפקד מחלקה 3","סמל מחלקה 1","סמל מחלקה 2","סמל מחלקה 3"
                        ,"מפקד כיתה א מח-1","מפקד כיתה ב מח-1","מפקד כיתה ג מח-1","מפקד כיתה א מח-2","מפקד כיתה ב מח-2","מפקד כיתה ג מח-2"
                        ,"מפקד כיתה א מח-3","מפקד כיתה ב מח-3","מפקד כיתה ג מח-3","חייל")
                }
                2->{
                    if(topPos[1]=="0"){
                        return listOf("מפקד מחלקה 1","סמל מחלקה 1", "מפקד כיתה א מח-1","מפקד כיתה ב מח-1","מפקד כיתה ג מח-1","חייל")
                    }
                    if(topPos[1]=="1"){
                        return listOf("מפקד מחלקה 2","סמל מחלקה 2", "מפקד כיתה א מח-2","מפקד כיתה ב מח-2","מפקד כיתה ג מח-2","חייל")
                    }
                    if(topPos[1]=="2"){
                        return listOf("מפקד מחלקה 3","סמל מחלקה 3", "מפקד כיתה א מח-3","מפקד כיתה ב מח-3","מפקד כיתה ג מח-3","חייל")
                    }
                }
                3->{
                    return listOf(getArmyJobByCode(userCommandPath),"חייל")
                }
            }
            return emptyList()
        }


        fun getArmyJobPosition(position:String):String{
            var str = ""
            when(position){
                "מפקד פלוגה" ->{str = "1"}
                "סגן מפקד פלוגה" ->{str = "-1"}
                "רספ" ->{str = "1.3"}
                "מפקד מחלקה 1" ->{str = "1.0"}
                "מפקד מחלקה 2" ->{str = "1.1"}
                "מפקד מחלקה 3" ->{str = "1.2"}
                "סמל מחלקה 1" ->{str = "-1.0"}
                "סמל מחלקה 2" ->{str = "-1.1"}
                "סמל מחלקה 3" ->{str = "-1.2"}
                "מפקד כיתה א מח-1" ->{str = "1.0.0"}
                "מפקד כיתה ב מח-1" ->{str = "1.0.1"}
                "מפקד כיתה ג מח-1" ->{str = "1.0.2"}
                "מפקד כיתה א מח-2" ->{str = "1.1.0"}
                "מפקד כיתה ב מח-2" ->{str = "1.1.1"}
                "מפקד כיתה ג מח-2" ->{str = "1.1.2"}
                "מפקד כיתה א מח-3" ->{str = "1.2.0"}
                "מפקד כיתה ב מח-3" ->{str = "1.2.1"}
                "מפקד כיתה ג מח-3" ->{str = "1.2.2"}
                "חייל" ->{str = ""}
            }

            return str
        }

        fun getSoldierArmyPosition(position: String):String{
            var str= ""
            when(position){
                "פיקוד הפלוגה" ->{str = "1"}
                "מפלג"->{str = "1.3"}
                "מחלקה 1" ->{str = "1.0"}
                "מחלקה 2" ->{str = "1.1"}
                "מחלקה 3" ->{str = "1.2"}
                "כיתה א מח-1" ->{str = "1.0.0"}
                "כיתה ב מח-1" ->{str = "1.0.1"}
                "כיתה ג מח-1" ->{str = "1.0.2"}
                "כיתה א מח-2" ->{str = "1.1.0"}
                "כיתה ב מח-2" ->{str = "1.1.1"}
                "כיתה ג מח-2" ->{str = "1.1.2"}
                "כיתה א מח-3" ->{str = "1.2.0"}
                "כיתה ב מח-3" ->{str = "1.2.1"}
                "כיתה ג מח-3" ->{str = "1.2.2"}
            }
            return str
        }

        fun getPositionByCode(string: String):String{
            var str = ""
            when(string){
                "1" ->{str = "פיקוד הפלוגה"}
                "1.3"->{str = "מפלג"}
                "1.0" ->{str = "מחלקה 1"}
                "1.1" ->{str = "מחלקה 2"}
                "1.2" ->{str = "מחלקה 3"}
                "1.0.0" ->{str = "כיתה א מח-1"}
                "1.0.1" ->{str = "כיתה ב מח-1"}
                "1.0.2" ->{str = "כיתה ג מח-1"}
                "1.1.0" ->{str = "כיתה א מח-2"}
                "1.1.1" ->{str = "כיתה ב מח-2"}
                "1.1.2" ->{str = "כיתה ג מח-2"}
                "1.2.0" ->{str = "כיתה א מח-3"}
                "1.2.1" ->{str = "כיתה ב מח-3"}
                "1.2.2" ->{str = "כיתה ג מח-3"}
            }
            return str
        }


        fun getArmyJobByCode(string:String):String{
            var str = ""
            when(string){
                "1" ->{str = "מפקד פלוגה"}
                "-1" ->{str = "סגן מפקד פלוגה"}
                "1.3" ->{str = "רספ"}
                "1.0" ->{str = "מפקד מחלקה 1"}
                "1.1" ->{str = "מפקד מחלקה 2"}
                "1.2" ->{str = "מפקד מחלקה 3"}
                "-1.0" ->{str = "סמל מחלקה 1"}
                "-1.1" ->{str = "סמל מחלקה 2"}
                "-1.2" ->{str = "סמל מחלקה 3"}
                "1.0.0" ->{str = "מפקד כיתה א מח-1"}
                "1.0.1" ->{str = "מפקד כיתה ב מח-1"}
                "1.0.2" ->{str = "מפקד כיתה ג מח-1"}
                "1.1.0" ->{str = "מפקד כיתה א מח-2"}
                "1.1.1" ->{str = "מפקד כיתה ב מח-2"}
                "1.1.2" ->{str = "מפקד כיתה ג מח-2"}
                "1.2.0" ->{str = "מפקד כיתה א מח-3"}
                "1.2.1" ->{str = "מפקד כיתה ב מח-3"}
                "1.2.2" ->{str = "מפקד כיתה ג מח-3"}
                "" ->{str = "חייל"}
            }

            return str
        }

        fun getDayOfWeek(str:String):String{
            return when(str){
                "SUNDAY" ->{"יום ראשון"}
                "MONDAY" ->{"יום שני"}
                "TUESDAY" ->{"יום שלישי"}
                "WEDNESDAY" ->{"יום רביעי"}
                "THURSDAY" ->{"יום חמישי"}
                "FRIDAY" ->{"יום שישי"}
                "SATURDAY" ->{"יום שבת"}
                else -> ""
            }
        }

        var excelArmyJobs = listOf<String>("מפ","סמפ","רספ","ממ","סמל","מכ","לוחם")
        var excelArmyPositions = listOf<String>("פלוגה א","פלוגה ב","פלוגה ג","פלוגה ד","מסייעת","פיקוד הפלוגה","מפלג","מחלקה 1","מחלקה 2","מחלקה 3","כיתה א","כיתה ב","כיתה ג")

        var soldiersToAddToActivityLD:MutableLiveData<List<String>> = MutableLiveData(listOf())
        var soldiersExistingInActivityThatAreNotFromGroup:MutableLiveData<List<String>> = MutableLiveData(listOf())
        var currentDate:MutableLiveData<ArmyDay> = MutableLiveData()

        var inActivitiesFragment = false

        var groupRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("X")
        var groupName  = ""
        var groupCode = ""


        var listOfOldSoldiersUpdatedByIsPresent:MutableList<Soldier> = mutableListOf()
        var listOfNewSoldiersUpdatedByIsPresent:MutableList<Soldier> = mutableListOf()
    }


}