package com.jacoblip.andriod.armycontrol.data.sevices

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.jacoblip.andriod.armycontrol.data.models.Group
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class Repository() {

    fun addSoldier(soldier: Soldier,groups: List<Group>,refs:List<DatabaseReference>) {
        when(groups.size){
            1->{
                var group0 = groups[0]
                if(group0!=null) {
                    if (soldier.usage[0] != '-') {
                        if (group0.commander == null) {
                            group0.amountOfSoldiers++
                            group0.commander = soldier
                            group0.commander!!.listOfDirectSoldiers =
                                group0.listOfDirectSoldiers
                        } else {
                            soldier.listOfDirectSoldiers =
                                group0.commander!!.listOfDirectSoldiers
                            group0.commander = soldier
                        }
                    } else {
                        if (group0.backUpCommander == null) {
                            group0.amountOfSoldiers++
                            group0.backUpCommander = soldier
                        } else {
                            soldier.listOfDirectSoldiers =
                                group0.backUpCommander!!.listOfDirectSoldiers
                            group0.backUpCommander = soldier
                        }
                    }

                    refs[0].setValue(group0).addOnSuccessListener {
                        Log.i("repository", "updated group0")
                    }
                }
            }
            2->{

                var group0 = groups[0]
                var group1 = groups[1]

                if(group0!=null&&group1!=null) {
                    if (soldier.usage[0] != '-') {
                        if (group1.commander == null) {
                            group1.amountOfSoldiers++
                            group0.amountOfSoldiers++
                            group1.commander = soldier
                            group1.commander!!.listOfDirectSoldiers =
                                group1.listOfDirectSoldiers
                            addSoldierToComander(group0, soldier)
                        } else {
                            removeSoldierFromCommander(group0, group1.commander!!)
                            soldier.listOfDirectSoldiers = group1.commander!!.listOfDirectSoldiers
                            addSoldierToComander(group0, soldier)

                        }
                    } else {
                        if (group1.backUpCommander == null) {
                            group1.amountOfSoldiers++
                            group0.amountOfSoldiers++
                        }
                        group1.backUpCommander = soldier
                    }

                    refs[0].setValue(group0).addOnSuccessListener {
                        Log.i("repository", "updated group0")
                    }.continueWith {
                        refs[1].setValue(group1).addOnSuccessListener {
                            Log.i("repository", "updated group1")
                        }
                    }
                }
            }
            3->{
                var group0 = groups[0]
                var group1 = groups[1]
                var group2 = groups[2]
                if(group0!=null&&group1!=null&&group2!=null) {
                    if (soldier.isCommander) {
                        if (group2.commander == null) {
                            group2.amountOfSoldiers++
                            group1.amountOfSoldiers++
                            group0.amountOfSoldiers++
                            group2.commander = soldier
                            group2.commander!!.listOfDirectSoldiers = group2.listOfDirectSoldiers
                            addSoldierToComander(group1, soldier)
                        } else {
                            removeSoldierFromCommander(group1, group2.commander!!)
                            soldier.listOfDirectSoldiers = group2.commander!!.listOfDirectSoldiers
                            addSoldierToComander(group1, soldier)
                        }
                    } else {
                        group2.amountOfSoldiers++
                        group1.amountOfSoldiers++
                        group0.amountOfSoldiers++
                        addSoldierToComander(group2,soldier)
                    }

                    refs[0].setValue(group0).addOnSuccessListener {
                        Log.i("repository", "updated group0")
                    }.continueWith {
                        refs[1].setValue(group1).addOnSuccessListener {
                            Log.i("repository", "updated group1")
                        }
                    }.continueWith {
                        refs[2].setValue(group2).addOnSuccessListener {
                            Log.i("repository", "updated group2")
                        }
                    }
                }
            }
        }
    }

    fun removeSoldier(soldier: Soldier, groups: List<Group>, refs:List<DatabaseReference>){
        when(groups.size){
            1->{
                var group0 = groups[0]

                if(group0!=null) {
                    if(soldier.usage[0]!='-'){
                        group0.commander=null
                    }else{
                        group0.backUpCommander=null
                    }
                    group0.amountOfSoldiers--

                    refs[0].setValue(group0).addOnSuccessListener {
                        Log.i("repository", "updated group0")
                    }
                }
            }
            2->{
                var group0 = groups[0]
                var group1 = groups[1]
                if(group0!=null&&group1!=null) {
                    removeSoldierFromCommander(group0,soldier)
                    if(soldier.usage[0]!='-'){
                        group1.commander = null
                    }else{
                        group1.backUpCommander = null
                    }
                    group0.amountOfSoldiers--
                    group1.amountOfSoldiers--

                    refs[0].setValue(group0).addOnSuccessListener {
                        Log.i("repository", "updated group0")
                    }.continueWith {
                        refs[1].setValue(group1).addOnSuccessListener {
                            Log.i("repository", "updated group1")
                        }
                    }
                }
            }
            3->{
                var group0 = groups[0]
                var group1 = groups[1]
                var group2 = groups[2]
                if(group0!=null&&group1!=null&&group2!=null) {

                    if(soldier.isCommander){
                        removeSoldierFromCommander(group1,soldier)
                        group1.commander = null
                    }else{
                        removeSoldierFromCommander(group2,soldier)
                    }
                    group0.amountOfSoldiers--
                    group1.amountOfSoldiers--
                    group2.amountOfSoldiers--

                    refs[0].setValue(group0).addOnSuccessListener {
                        Log.i("repository", "updated group0")
                    }.continueWith {
                        refs[1].setValue(group1).addOnSuccessListener {
                            Log.i("repository", "updated group1")
                        }
                    }.continueWith {
                        refs[2].setValue(group2).addOnSuccessListener {
                            Log.i("repository", "updated group2")
                        }
                    }
                }
            }
        }
    }

    fun addSoldierToComander(group0:Group,soldier: Soldier){
        var list1= group0.listOfDirectSoldiers.toMutableList()
        list1.add(soldier)
        var list2 = group0.commander?.listOfDirectSoldiers?.toMutableList()
        list2?.add(soldier)
        group0.commander?.listOfDirectSoldiers = list2?.toList()!!
        group0.listOfDirectSoldiers = list1
    }

    fun removeSoldierFromCommander(group0:Group,soldier: Soldier){
        var list1= group0.listOfDirectSoldiers.toMutableList()
        list1.remove(soldier)
        group0.commander?.listOfDirectSoldiers?.toMutableList()?.remove(soldier)
    }




}