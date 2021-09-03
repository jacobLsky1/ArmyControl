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
import java.util.*


 class SoldiersViewModel(private val repository: Repository, var context: Context):ViewModel() {

    private var groupReference = FirebaseDatabase.getInstance().getReference("פלוגה").child("ב")
    private var soldiersReference = FirebaseDatabase.getInstance().getReference("חיילים")
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

     fun updateLists(){
         amountOfSoldiersPresent = 0
         var allSoldiers = listOfAllSoldiers
         allSoldiers?.sortedBy {
             it?.positionMap
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
                    userSoldiers.forEach { soldier ->
                        if (!soldier.equals(overSoldier)) {
                            if (overSoldier.positionMap.length == 1)
                                if(soldier.armyJobMap.length == 2 || soldier.armyJobMap.length == 3)
                                    directSoldiers.add(soldier)
                            if (overSoldier.armyJobMap.length == 2)
                                if( soldier.armyJobMap.length >= 3 )
                                    if(soldier.armyJobMap[2]=='3')
                                       directSoldiers.add(soldier)
                        }
                    }
                }
                2 -> {
                    userSoldiers.forEach { soldier ->
                        if (!soldier.equals(overSoldier)) {
                            if (soldier.armyJobMap=="-${overSoldier.armyJobMap}" || (soldier.armyJobMap.length==5 && soldier.isCommander))
                                directSoldiers.add(soldier)
                        }
                    }
                }
                3 -> {
                    userSoldiers.forEach { soldier ->
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

        for(soldier in soldiers){
          allSoldiers?.remove(soldier)
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

}