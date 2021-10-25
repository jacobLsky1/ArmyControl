 package com.jacoblip.andriod.armycontrol.data.sevices

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.jacoblip.andriod.armycontrol.data.models.ArmyActivity
import com.jacoblip.andriod.armycontrol.data.models.ArmyDay
import com.jacoblip.andriod.armycontrol.data.models.Group
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.utilities.Util
import org.jetbrains.anko.doAsync
import java.util.*


 class SoldiersViewModel(private val repository: Repository, var context: Context):ViewModel() {

    private val groupReference = Util.groupRef.child("build")
    private val soldiersReference = Util.groupRef.child("soldiers")
     private val codesReference = Util.groupRef.child("entry codes")
    var currentFragment:Fragment? = null

     var soldierCommandPath:String = Util.userCommandPath

     private var biggestGroup :Group? = null

     val soldierStack:Stack<Soldier> = Stack()

     var listOfAllSoldiers :List<Soldier?>? = listOf()

     var amountOfSoldiersPresent = 0

    private var _listOfPersonalSoldiers = MutableLiveData<List<Soldier>>()
    var listOfPersonalSoldiers:LiveData<List<Soldier>> = _listOfPersonalSoldiers

    private var _listOfPersonalSoldiersForSoldier = MutableLiveData<List<Soldier>>()
    var listOfPersonalSoldiersForSoldier:LiveData<List<Soldier>> = _listOfPersonalSoldiersForSoldier

    private var _userGroup = MutableLiveData<Group>()
    var userGroup:LiveData<Group> = _userGroup

     private var _nowSoldier = MutableLiveData<Soldier>()
     var nowSoldier:LiveData<Soldier> = _nowSoldier

    var listOfSoldiersWithPower:List<Soldier> = listOf()
    var listOfUserCommanders:List<Soldier> = listOf()

     var soldiersDeleted : MutableLiveData<Boolean> = MutableLiveData(false)


     init {
         getAllSoldiers()
         getBigGroup(groupReference)
     }

    private fun getAllSoldiers(){
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val t: GenericTypeIndicator<List<Soldier?>?> = object : GenericTypeIndicator<List<Soldier?>?>() {}
                listOfAllSoldiers = dataSnapshot.getValue(t)
                updateLists()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        soldiersReference.addValueEventListener(menuListener)
    }

     fun setSoldier(soldier: Soldier) = _nowSoldier.postValue(soldier)

     fun sortAllSoldiers(allSoldiers:MutableList<Soldier>):MutableList<Soldier>{
         var sortedSoldiers = mutableListOf<Soldier>()
         var headGroup : MutableList<Soldier> = mutableListOf()
         var subGroup1 : MutableList<Soldier> = mutableListOf()
         var subsubGroup1a : MutableList<Soldier> = mutableListOf()
         var subsubGroup1b : MutableList<Soldier> = mutableListOf()
         var subsubGroup1c : MutableList<Soldier> = mutableListOf()
         var subGroup2 : MutableList<Soldier> = mutableListOf()
         var subsubGroup2a : MutableList<Soldier> = mutableListOf()
         var subsubGroup2b : MutableList<Soldier> = mutableListOf()
         var subsubGroup2c : MutableList<Soldier> = mutableListOf()
         var subGroup3 : MutableList<Soldier> = mutableListOf()
         var subsubGroup3a : MutableList<Soldier> = mutableListOf()
         var subsubGroup3b : MutableList<Soldier> = mutableListOf()
         var subsubGroup3c : MutableList<Soldier> = mutableListOf()
         var backGroup : MutableList<Soldier> = mutableListOf()


         for(soldier in allSoldiers){
             var map = soldier.positionMap.split('.')
             if(map.size==1){
                 if(!soldier.isLieutenant)
                     headGroup.add(0,soldier)
                 else{
                     if(headGroup.size==0)
                         headGroup.add(soldier)
                     else
                         headGroup.add(1,soldier)
                 }

             }
             if(map.size==2){
                 var num = map[map.lastIndex].toInt()
                when(num){
                    0->{
                        if(!soldier.isLieutenant)
                            subGroup1.add(0,soldier)
                        else{
                            if(subGroup1.size==0)
                                subGroup1.add(soldier)
                            else
                                subGroup1.add(1,soldier)
                        }
                    }
                    1->{
                        if(!soldier.isLieutenant)
                            subGroup2.add(0,soldier)
                        else{
                            if(subGroup2.size==0)
                                subGroup2.add(soldier)
                            else
                                subGroup2.add(1,soldier)
                        }
                    }
                    2->{
                        if(!soldier.isLieutenant)
                            subGroup3.add(0,soldier)
                        else{
                            if(subGroup3.size==0)
                                subGroup3.add(soldier)
                            else
                                subGroup3.add(1,soldier)
                        }
                    }
                    3->{
                        if(soldier.isCommander){
                            backGroup.add(0,soldier)
                        }else backGroup.add(soldier)
                    }
                }
             }
             if(map.size==3){
                 var num1 = map[map.lastIndex-1].toInt()
                 var num2 = map[map.lastIndex].toInt()
                 if(num1==0&&num2==0){
                     if(soldier.isCommander){
                         subsubGroup1a.add(0,soldier)
                     }
                     else subsubGroup1a.add(soldier)
                 }
                 if(num1==0&&num2==1){
                     if(soldier.isCommander){
                         subsubGroup1b.add(0,soldier)
                     }
                     else subsubGroup1b.add(soldier)
                 }
                 if(num1==0&&num2==2){
                     if(soldier.isCommander){
                         subsubGroup1c.add(0,soldier)
                     }
                     else subsubGroup1c.add(soldier)
                 }
                 if(num1==1&&num2==0){
                     if(soldier.isCommander){
                         subsubGroup2a.add(0,soldier)
                     }
                     else subsubGroup2a.add(soldier)
                 }
                 if(num1==1&&num2==1){
                     if(soldier.isCommander){
                         subsubGroup2b.add(0,soldier)
                     }
                     else subsubGroup2b.add(soldier)
                 }
                 if(num1==1&&num2==2){
                     if(soldier.isCommander){
                         subsubGroup2c.add(0,soldier)
                     }
                     else subsubGroup2c.add(soldier)
                 }
                 if(num1==2&&num2==0){
                     if(soldier.isCommander){
                         subsubGroup3a.add(0,soldier)
                     }
                     else subsubGroup3a.add(soldier)
                 }
                 if(num1==2&&num2==1){
                     if(soldier.isCommander){
                         subsubGroup3b.add(0,soldier)
                     }
                     else subsubGroup3b.add(soldier)
                 }
                 if(num1==2&&num2==2){
                     if(soldier.isCommander){
                         subsubGroup3c.add(0,soldier)
                     }
                     else subsubGroup3c.add(soldier)
                 }
             }

         }

         val allGroups = listOf(headGroup,subGroup1,subsubGroup1a,subsubGroup1b,subsubGroup1c,subGroup2,subsubGroup2a,subsubGroup2b,subsubGroup2c,subGroup3,subsubGroup3a,subsubGroup3b,subsubGroup3c,backGroup)

         for(group in allGroups){
             sortedSoldiers.addAll(group)
         }

         return sortedSoldiers
     }

     fun updateLists(){
         amountOfSoldiersPresent = 0
         var allSoldiers:MutableList<Soldier> = mutableListOf()
         if(listOfAllSoldiers!=null){
             allSoldiers = (listOfAllSoldiers as List<Soldier>).toMutableList()
             allSoldiers = sortAllSoldiers(allSoldiers)
         }

         var position = commandPathToArray(soldierCommandPath)
         var listOfSoldiersForGroup = mutableListOf<Soldier>()
         if (allSoldiers != null) {
             for (soldier in allSoldiers){
                 var soldierPosition = commandPathToArray(soldier!!.positionMap)
                 var commanderLastIndex = position.lastIndex
                 when(position.size){
                     1->{
                         if(soldierPosition.size>=position.size) {
                             listOfSoldiersForGroup.add(soldier)
                             if(soldier.hasArrived)
                                 amountOfSoldiersPresent++
                         }
                     }
                    else->{
                         if(soldierPosition.size>=position.size&& position[commanderLastIndex]==soldierPosition[commanderLastIndex]) {
                             listOfSoldiersForGroup.add(soldier)
                             if(soldier.hasArrived)
                                 amountOfSoldiersPresent++
                         }
                     }

                 }
             }
         }
         _listOfPersonalSoldiers.postValue(listOfSoldiersForGroup.toList())
         var commanders = mutableListOf<Soldier>()
         var powers = mutableListOf<Soldier>()
         listOfSoldiersForGroup.forEach {
             if(it.isCommander){
                commanders.add(it)
             }
             if(it.pakal.isNotEmpty()){
                 powers.add(it)
             }
         }
         listOfUserCommanders = commanders
         listOfSoldiersWithPower = powers

     }


    fun getDirectSoldiersForSoldier(overSoldier: Soldier){
        var position = commandPathToArray(overSoldier.positionMap)
        var directSoldiers = mutableListOf<Soldier>()
        var userSoldiers  = listOfPersonalSoldiers.value
        if(userSoldiers!=null && overSoldier.isCommander) {
            when (position.size) {
                1 -> {
                    for(soldier in userSoldiers) {
                        if (!soldier.equals(overSoldier)) {
                            if (overSoldier.positionMap.length == 1)
                                if(soldier.armyJobMap.length == 2 || soldier.armyJobMap.length == 3) {
                                    directSoldiers.add(soldier)
                                    continue
                                }
                            if (overSoldier.armyJobMap.length == 2)
                                if( soldier.armyJobMap.length >= 3 )
                                    if(soldier.armyJobMap[2]=='3'){
                                       directSoldiers.add(soldier)
                                        continue
                                    }
                        }
                    }
                }
                2 -> {
                    for(soldier in userSoldiers) {
                        if (!soldier.equals(overSoldier)) {
                            if (soldier.armyJobMap=="-${overSoldier.armyJobMap}" ){
                                directSoldiers.add(soldier)
                                continue
                            }
                            if(soldier.armyJobMap.length==5 && soldier.isCommander){
                                var char = soldier.armyJobMap[2]
                                if(char==overSoldier.positionMap[2]){
                                    directSoldiers.add(soldier)
                                    continue
                                }
                            }
                        }
                    }
                }
                3 -> {
                    for(soldier in userSoldiers) {
                        if(!soldier.equals(overSoldier)) {
                            var map = commandPathToArray(soldier.positionMap)
                            if (map.size == 3 && map[map.lastIndex] == position[position.lastIndex])
                                directSoldiers.add(soldier)
                        }
                    }
                }
            }
        }
        _listOfPersonalSoldiersForSoldier.postValue(directSoldiers.toList())
    }

    fun addSoldier(soldier: Soldier){

        if(listOfAllSoldiers==null)
            listOfAllSoldiers = listOf()

        var allSoldiers = listOfAllSoldiers?.toMutableList()
        allSoldiers?.add(soldier)
        soldiersReference.setValue(allSoldiers).addOnSuccessListener {
            Log.i("Repository", "added soldier")
            var position = commandPathToArray(soldier.positionMap)
            increaceNumber(position)
        }
    }

     fun increaceNumber(position: List<String>){
         when(position.size){
             1 -> {
                 biggestGroup?.amountOfSoldiers = biggestGroup?.amountOfSoldiers!!+1
             }
             2 -> {
                 biggestGroup?.amountOfSoldiers= biggestGroup?.amountOfSoldiers!!+1
                 biggestGroup?.subGroups!![position[1].toInt()].amountOfSoldiers++
             }
             3 -> {
                 biggestGroup?.amountOfSoldiers = biggestGroup?.amountOfSoldiers!!+1
                 biggestGroup?.subGroups!![position[1].toInt()].amountOfSoldiers++
                 biggestGroup?.subGroups!![position[1].toInt()].subGroups!![position[2].toInt()].amountOfSoldiers++
             }
         }
         groupReference.setValue(biggestGroup)
     }
     fun decreaseNumber(soldiers: List<Soldier>){

         for (soldier in soldiers) {
             val position = commandPathToArray(soldier.positionMap)
             when (position.size) {
                 1 -> {
                     biggestGroup?.amountOfSoldiers = biggestGroup?.amountOfSoldiers!! - 1
                 }
                 2 -> {
                     biggestGroup?.amountOfSoldiers = biggestGroup?.amountOfSoldiers!! - 1
                     biggestGroup?.subGroups!![position[1].toInt()].amountOfSoldiers--
                 }
                 3 -> {
                     biggestGroup?.amountOfSoldiers = biggestGroup?.amountOfSoldiers!! - 1
                     biggestGroup?.subGroups!![position[1].toInt()].amountOfSoldiers--
                     biggestGroup?.subGroups!![position[1].toInt()].subGroups!![position[2].toInt()].amountOfSoldiers--
                 }
             }
         }
         groupReference.setValue(biggestGroup)
     }

    fun removeSoldiers(soldiers: List<Soldier>){
        var allSoldiers = listOfAllSoldiers?.toMutableList()
        var deletedSoldiersIds = mutableListOf<String>()
        for(soldier in soldiers){
          allSoldiers?.remove(soldier)
            if(soldier.entryCode!=""&&soldier.positionMap!="1") {
                codesReference.child(soldier.idNumber).setValue(null)
            }
         }
        soldiersReference.setValue(allSoldiers).addOnSuccessListener {
            Log.i("Repository", "added soldier")
            decreaseNumber(soldiers)
        }
    }

    fun getUserGroup(){

        var position = commandPathToArray(soldierCommandPath)
        var group:Group? = biggestGroup
        if(position.size>1){
            for(i in 1..position.lastIndex){
               group = group?.subGroups?.get(position[i].toInt())
            }
        }
        _userGroup.postValue(group)
    }


     private fun getBigGroup(reference: DatabaseReference){
         val menuListener = object : ValueEventListener {
             override fun onDataChange(dataSnapshot: DataSnapshot) {
                 biggestGroup = dataSnapshot.getValue(Group::class.java)
                 getUserGroup()
             }

             override fun onCancelled(databaseError: DatabaseError) {
                 // handle error
             }
         }
         reference.addValueEventListener(menuListener)
     }

     private fun commandPathToArray(commandPath: String): List<String> = commandPath.split('.')


     fun saveSoldier(newSoldier:Soldier,oldSoldier:Soldier){
         var allSoldiers = listOfAllSoldiers?.toMutableList()
         val index = allSoldiers!!.indexOf(oldSoldier)
         allSoldiers[index] = newSoldier
         soldiersReference.setValue(allSoldiers).addOnSuccessListener {
             Log.i("viewModel","updated soldier")
         }
         if(oldSoldier.entryCode!=""&&newSoldier.entryCode==""){
             codesReference.child(oldSoldier.idNumber).setValue(null)
         }
         if(oldSoldier.idNumber!=newSoldier.idNumber){
             codesReference.child(oldSoldier.idNumber).setValue(null)
         }

         if(newSoldier.entryCode!=""){
             codesReference.child(newSoldier.idNumber).setValue("${newSoldier.entryCode} ${newSoldier.positionMap}")
         }
         _nowSoldier.postValue(newSoldier)
     }

     fun popStack(){
         soldierStack.pop()
         if(soldierStack.isNotEmpty())
         _nowSoldier.postValue(soldierStack.peek())
     }

     fun swapOutNewSoldier(newSoldier: Soldier){
         soldierStack.pop()
         soldierStack.push(newSoldier)
         _nowSoldier.postValue(soldierStack.peek())
     }

     fun getSoldierActivities(soldier: Soldier,armyDays:List<ArmyDay?>?):List<ArmyActivity>{
         return if(armyDays!=null) {
             val list: MutableList<ArmyActivity> = mutableListOf()
             for (i in armyDays.indices) {
                 val day = armyDays[i]
                 for (j in day!!.activities.indices) {
                     val activity = day.activities[j]
                     if (activity.attendees.contains(soldier.idNumber))
                         list.add(activity)
                 }
             }
             list
         }else emptyList()
     }

     fun addActivityToSoldiers(oldArmyActivity: ArmyActivity?,newArmyActivity: ArmyActivity) {
         var allSoldiers = listOfAllSoldiers
         for(soldier in allSoldiers!!){
             if(newArmyActivity.attendees.contains(soldier!!.idNumber)){
                 if(oldArmyActivity==null) {
                     val list = soldier.Activates.toMutableList()
                     list.add(newArmyActivity)
                     list.sortBy { it.date }
                     soldier.Activates = list
                 }else{
                     val inx = soldier.Activates.indexOf(oldArmyActivity)
                     val list = soldier.Activates.toMutableList()
                     list[inx] = newArmyActivity
                     list.sortBy { it.date }
                     soldier.Activates = list
                 }
             }
         }
         soldiersReference.setValue(allSoldiers)
     }

     fun removeActivitiesFromSoldiers(activities: List<ArmyActivity>){
         var dataChanged = false
         doAsync {
             for (activity in activities) {
                 if(listOfAllSoldiers?.size!=0) {
                     for (soldier in listOfAllSoldiers!!){
                         if(soldier!!.Activates.contains(activity)){
                             var list = soldier!!.Activates.toMutableList()
                             list.remove(activity)
                             soldier.Activates = list.toList()
                             dataChanged = true
                         }
                     }
                 }
             }
             if(dataChanged) {
                 soldiersReference.setValue(listOfAllSoldiers)
             }
         }
     }

}