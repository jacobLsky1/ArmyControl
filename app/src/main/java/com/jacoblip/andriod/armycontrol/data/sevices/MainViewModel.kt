 package com.jacoblip.andriod.armycontrol.data.sevices

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.jacoblip.andriod.armycontrol.data.models.Group
import com.jacoblip.andriod.armycontrol.data.models.ListOfSoldiers
import com.jacoblip.andriod.armycontrol.data.models.Soldier

 class MainViewModel(private val repository: Repository, var context: Context):ViewModel() {

    private var groupReference = FirebaseDatabase.getInstance().getReference("פלוגה").child("ב")
    private var soldiersReference = groupReference.child("חיילים")
    var currentFragment:Fragment? = null

    private var _listOfAllSoldiers = MutableLiveData<ListOfSoldiers>()
    var listOfAllSoldiers : LiveData<ListOfSoldiers> = _listOfAllSoldiers

    private var _listOfPersonalSoldiers = MutableLiveData<List<Soldier>>(listOf())
    var listOfPersonalSoldiers:LiveData<List<Soldier>> = _listOfPersonalSoldiers

    private var _listOfPersonalSoldiersForSoldier = MutableLiveData<List<Soldier>>()
    var listOfPersonalSoldiersForSoldier:LiveData<List<Soldier>> = _listOfPersonalSoldiersForSoldier

    private var _userGroup = MutableLiveData<Group>()
    var userGroup:LiveData<Group> = _userGroup
     var biggestGroup:Group? = null

    var listOfSoldiersWithPower:List<Soldier> = listOf()
    var listOfUserCommanders:List<Soldier> = listOf()

     init {
        getAllSoldiers()
         getBigGroup(groupReference)
     }

    private fun getAllSoldiers(){
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                _listOfAllSoldiers.postValue(dataSnapshot.getValue(ListOfSoldiers::class.java))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        soldiersReference.addValueEventListener(menuListener)
    }

     fun updateLists(allSoldiers:List<Soldier>,commandPath: String){
         var position = commandPathToArray(commandPath)
         var listOfSoldiersForGroup = mutableListOf<Soldier>()
         for (soldier in allSoldiers){
             var soldierPosition = commandPathToArray(soldier.stationMap)
             var commanderLastIndex = position[position.lastIndex].toInt()
             if(soldierPosition.size>=position.size&&position[commanderLastIndex]==soldierPosition[commanderLastIndex]){
                 listOfSoldiersForGroup.add(soldier)
             }
         }
         _listOfPersonalSoldiers.postValue(listOfSoldiersForGroup.toList())
         var commanders = mutableListOf<Soldier>()
         var powers = mutableListOf<Soldier>()
         listOfSoldiersForGroup.forEach {
             if(it.isCommander){
                commanders.add(it)
             }
             if(it.usage!=""){
                 powers.add(it)
             }
         }
         listOfUserCommanders = commanders
         listOfSoldiersWithPower = powers
     }

    fun getDirectSoldiersForSoldier(commandPath: String){
        var position = commandPathToArray(commandPath)
        var directSoldiers = mutableListOf<Soldier>()
        var userSoldiers  = listOfPersonalSoldiers.value
        if(userSoldiers!=null) {
            when (position.size) {
                1 -> {
                   userSoldiers.forEach { soldier->
                       if(soldier.usage.length==3)
                           directSoldiers.add(soldier)
                   }
                }
                2 -> {
                    userSoldiers.forEach { soldier->
                        if(soldier.usage.length==4||(soldier.usage.length==5))
                            directSoldiers.add(soldier)
                    }
                }
                3 -> {
                    userSoldiers.forEach { soldier->
                        var map = commandPathToArray(soldier.stationMap)
                        if(soldier.usage.isEmpty()&&map[map.lastIndex]==position[position.lastIndex])
                            directSoldiers.add(soldier)
                    }
                }
            }
        }
        _listOfPersonalSoldiersForSoldier.postValue(directSoldiers.toList())
    }

    fun addSoldier(soldier: Soldier){
        var allSoldiers = listOfAllSoldiers.value?.soldiers?.toMutableList()
        allSoldiers?.add(soldier)
        soldiersReference.setValue(allSoldiers).addOnSuccessListener {
            Log.i("Repository","added soldier")
            var position = commandPathToArray(soldier.stationMap)
            increaceNumber(position)
        }
    }

     fun increaceNumber(position:List<String>){
         when(position.size){
             1->{biggestGroup!!.amountOfSoldiers++}
             2->{
                 biggestGroup!!.amountOfSoldiers++
                 biggestGroup!!.subGroups!![position[1].toInt()].amountOfSoldiers++
             }
             3->{
                 biggestGroup!!.amountOfSoldiers++
                 biggestGroup!!.subGroups!![position[1].toInt()].amountOfSoldiers++
                 biggestGroup!!.subGroups!![position[1].toInt()].subGroups!![position[2].toInt()].amountOfSoldiers++
             }
         }
         groupReference.setValue(biggestGroup)
     }
     fun decreaseNumber(position:List<String>){
         when(position.size){
             1->{biggestGroup!!.amountOfSoldiers--}
             2->{
                 biggestGroup!!.amountOfSoldiers--
                 biggestGroup!!.subGroups!![position[1].toInt()].amountOfSoldiers--
             }
             3->{
                 biggestGroup!!.amountOfSoldiers++
                 biggestGroup!!.subGroups!![position[1].toInt()].amountOfSoldiers--
                 biggestGroup!!.subGroups!![position[1].toInt()].subGroups!![position[2].toInt()].amountOfSoldiers--
             }
         }
         groupReference.setValue(biggestGroup)
     }

    fun removeSoldier(soldier: Soldier){
        var allSoldiers = listOfAllSoldiers.value?.soldiers?.toMutableList()
        allSoldiers?.remove(soldier)
        soldiersReference.setValue(allSoldiers).addOnSuccessListener {
            Log.i("Repository","added soldier")
            var position = commandPathToArray(soldier.stationMap)
            decreaseNumber(position)
        }
    }

    fun deleteSoldiers(soldiers: List<Soldier>){
        for(soldier in soldiers){
           removeSoldier(soldier)
        }
    }

    fun getUserGroup(commandPath:String){
        var position = commandPathToArray(commandPath)
        var group:Group? = biggestGroup
        if(position.size>1){
            for(i in 1..position.lastIndex){
               group = group?.subGroups?.get(position[i].toInt())
            }
        }
        _userGroup.postValue(group)
    }


     private fun getBigGroup(reference:DatabaseReference){
         val menuListener = object : ValueEventListener {
             override fun onDataChange(dataSnapshot: DataSnapshot) {
                 biggestGroup = dataSnapshot.getValue(Group::class.java)
             }

             override fun onCancelled(databaseError: DatabaseError) {
                 // handle error
             }
         }
         reference.addValueEventListener(menuListener)
     }

     private fun commandPathToArray(commandPath: String): List<String> = commandPath.split('.')

     fun resetSoldiersForSoldier(){
         _listOfPersonalSoldiersForSoldier.postValue(null)
     }

}