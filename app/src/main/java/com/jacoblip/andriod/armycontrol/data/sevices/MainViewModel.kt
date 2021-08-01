 package com.jacoblip.andriod.armycontrol.data.sevices

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.jacoblip.andriod.armycontrol.data.models.Group
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.utilities.Util
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainViewModel(private val repository: Repository, var context: Context):ViewModel() {


    var userGroup:MutableLiveData<Group> = MutableLiveData()
    var biggestGroup:MutableLiveData<Group> = MutableLiveData()
    var allSoldiers:MutableLiveData<List<Soldier>> = MutableLiveData()
    var allCommanders:List<Soldier> = listOf()
    var listOfSoldiersWithPower:List<Soldier> = listOf()
    var userPosition:String = ""
    var refrence = FirebaseDatabase.getInstance().getReference("פלוגה").child("ב")
    var currentFragment:Fragment? = null

    fun getBiggestGroup(){
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                biggestGroup.postValue(dataSnapshot.getValue(Group::class.java))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        refrence.addValueEventListener(menuListener)
    }

    fun getGroupOfSoldiers(position:String){
        var group :Group? = biggestGroup.value
        userPosition = position
        val positionList = position.split('.')
        if(group!=null) {
            for (i in 1..positionList.lastIndex) {
                var num = positionList[i].toInt()
                group = group!!.subGroups!![num]
            }
            userGroup.postValue(group)
        }
    }

    fun getAllSoldiersFromGroup(group: Group){
        var bigListOfSoldiers = getAllSoilders(group).toList()
        var listOfSoldiersId = mutableListOf<String>()
        var listOfSoldiers = mutableListOf<Soldier>()
        for (soldier in bigListOfSoldiers){
            if (!listOfSoldiersId.contains(soldier.idNumber)) {
                listOfSoldiersId.add(soldier.idNumber)
                listOfSoldiers.add(soldier)
            }
        }
        allSoldiers.postValue(listOfSoldiers)
        updateLists(listOfSoldiers)
    }

    fun updateLists(soldiers:List<Soldier>){
        var commanders:MutableList<Soldier> = mutableListOf()
        var powers:MutableList<Soldier> = mutableListOf()
        for(soldier in soldiers){
            if(soldier.isCommander){
                commanders.add(soldier)
            }
            if(!soldier.isCommander&&soldier.usage!=""){
                powers.add(soldier)
            }
        }
        allCommanders =commanders.toList()
        listOfSoldiersWithPower = powers.toList()
    }

    fun getAllSoilders(group:Group):MutableList<Soldier>{
        var listOfSoldier:MutableList<Soldier> = mutableListOf()
        if(group.commander!=null) {
            if (!listOfSoldier.contains(group.commander!!)) {
                group.commander?.let { listOfSoldier.add(it) }
            }
        }
        if(group.backUpCommander!=null) {
            if (!listOfSoldier.contains(group.backUpCommander!!)) {
                group.backUpCommander?.let { listOfSoldier.add(it) }
            }
        }
        listOfSoldier.addAll(group.listOfDirectSoldiers)
        if(group.subGroups!=null){
            for(group in group.subGroups!!){
                listOfSoldier.addAll(getAllSoilders(group))
            }
        }
        return listOfSoldier
    }

    fun getRefByPosition(listPosition:List<String>):DatabaseReference{
        var ref = Util.refrences
        for(i in 1 until listPosition.size){
            var num = listPosition[i].toInt()
            ref = ref!!.listOfChildRefrences!![num]
        }
        return ref.refrences!!
    }

    fun addSoldier(soldier: Soldier){
        var groups = mutableListOf<Group>()
        var refs = mutableListOf<DatabaseReference>()
        groups.add(biggestGroup.value!!)
        refs.add(Util.refrences.refrences!!)
        var listPosition = soldier.stationMap.split('.')
        if(listPosition.size==2){
            var num = listPosition[1].toInt()
            var subGroup = groups[0].subGroups!![num]
            var subReference = Util.refrences.listOfChildRefrences!![num].refrences
            groups.add(subGroup)
            refs.add(subReference!!)
        }
        if(listPosition.size==3){
            var num1 = listPosition[1].toInt()
            var num2 = listPosition[2].toInt()
            var subGroup = groups[0].subGroups!![num1]
            var subsubGroup = groups[0].subGroups!![num1].subGroups!![num2]
            var subRef =  Util.refrences.listOfChildRefrences!![num1]
            var subsubRef = subRef.listOfChildRefrences!![num2]
            groups.add(subGroup)
            groups.add(subsubGroup)
            refs.add(subRef!!.refrences!!)
            refs.add(subsubRef!!.refrences!!)
        }
      repository.addSoldier(soldier,groups,refs)
    }

    fun removeSoldier(soldier: Soldier){
        var groups = mutableListOf<Group>()
        var refs = mutableListOf<DatabaseReference>()
        groups.add(biggestGroup.value!!)
        refs.add(Util.refrences.refrences!!)
        var listPosition = soldier.stationMap.split('.')
        if(listPosition.size==2){
            var num = listPosition[1].toInt()
            var subGroup = groups[0].subGroups!![num]
            var subReference = Util.refrences.listOfChildRefrences!![num].refrences
            groups.add(subGroup)
            refs.add(subReference!!)
        }
        if(listPosition.size==3){
            var num1 = listPosition[1].toInt()
            var num2 = listPosition[2].toInt()
            var subGroup = groups[0].subGroups!![num1]
            var subsubGroup = groups[0].subGroups!![num1].subGroups!![num2]
            var subRef =  Util.refrences.listOfChildRefrences!![num1]
            var subsubRef = subRef.listOfChildRefrences!![num2]
            groups.add(subGroup)
            groups.add(subsubGroup)
            refs.add(subRef!!.refrences!!)
            refs.add(subsubRef!!.refrences!!)
        }
        repository.removeSoldier(soldier,groups,refs)
    }

    fun deleteSoldiers(soldiers: List<Soldier>){
        for(soldier in soldiers){
           removeSoldier(soldier)
        }
    }

}