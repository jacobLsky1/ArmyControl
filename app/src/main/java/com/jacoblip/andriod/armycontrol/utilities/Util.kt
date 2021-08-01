package com.jacoblip.andriod.armycontrol.utilities

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jacoblip.andriod.armycontrol.data.models.Refrences

class Util {
    companion object{

        var inSelectionMode :MutableLiveData<Boolean> = MutableLiveData(false)
        var addFABY = 0F

        val group = FirebaseDatabase.getInstance().getReference("פלוגה").child("ב")
        val group0 = group.child("subGroups").child("0")
        val group1 = group.child("subGroups").child("1")
        val group2 = group.child("subGroups").child("2")
        val group3 = group.child("subGroups").child("3")
        val group00 = group0.child("subGroups").child("0")
        val group01 = group0.child("subGroups").child("1")
        val group02 = group0.child("subGroups").child("2")
        val group10 = group1.child("subGroups").child("0")
        val group11 = group1.child("subGroups").child("1")
        val group12 = group1.child("subGroups").child("2")
        val group20 = group2.child("subGroups").child("0")
        val group21 = group2.child("subGroups").child("1")
        val group22 = group2.child("subGroups").child("2")

        var refrences = Refrences(group, listOf(
                Refrences(group0, listOf(Refrences(group00,null),Refrences(group01,null),Refrences(group02,null))),
                Refrences(group1, listOf(Refrences(group10,null),Refrences(group11,null),Refrences(group12,null))),
                Refrences(group2, listOf(Refrences(group20,null),Refrences(group21,null),Refrences(group22,null))),
                Refrences(group3,null)
        ))

        var hasInternet:MutableLiveData<Boolean> = MutableLiveData(true)

        val armyCommandersPositions = arrayOf("מפקד פלוגה","סגן מפקד פלוגה","רספ","מפקד מחלקה 1","מפקד מחלקה 2","מפקד מחלקה 3","סמל מחלקה 1","סמל מחלקה 2","סמל מחלקה 3"
               ,"מפקד כיתה א מח-1","מפקד כיתה ב מח-1","מפקד כיתה ג מח-1","מפקד כיתה א מח-2","מפקד כיתה ב מח-2","מפקד כיתה ג מח-2"
        ,"מפקד כיתה א מח-3","מפקד כיתה ב מח-3","מפקד כיתה ג מח-3","חייל")

        val armySoldierPositions = arrayOf("פיקוד הפלוגה","מפלג","מחלקה 1","מחלקה 2","מחלקה 3","כיתה א מח-1","כיתה ב מח-1","כיתה ג מח-1"
        ,"כיתה א מח-2","כיתה ב מח-2","כיתה ג מח-2","כיתה א מח-3","כיתה ב מח-3","כיתה ג מח-3")





        fun getArmyCommanderPosition(position:String):String{
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
    }
}