package com.jacoblip.andriod.armycontrol.data.sevices

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.jacoblip.andriod.armycontrol.data.models.ArmyDay
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import java.time.LocalDate

class ActivitiesViewModel(repository: Repository,context: Context):ViewModel() {

    private var activityReference = FirebaseDatabase.getInstance().getReference("ימי שירות")

    private var listOfAllArmyDays:MutableList<ArmyDay?>? = mutableListOf()

    var currentDay:ArmyDay? = null

    private var _listOfArmyDays = MutableLiveData<List<ArmyDay?>?>()
    var listOfArmyDays : LiveData<List<ArmyDay?>?> =_listOfArmyDays

    init {
        getAllArmyDays()
    }

    private fun getAllArmyDays(){
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val t: GenericTypeIndicator<List<ArmyDay?>?> = object : GenericTypeIndicator<List<ArmyDay?>?>() {}
                listOfAllArmyDays = dataSnapshot.getValue(t)?.toMutableList()
                if(listOfAllArmyDays==null){
                    listOfAllArmyDays = mutableListOf()
                }
                _listOfArmyDays.postValue(listOfAllArmyDays)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        activityReference.addValueEventListener(menuListener)
    }

    fun addArmyDays(armyDays:List<ArmyDay>){
        armyDays.forEach {
            for(existingArmyDay in listOfAllArmyDays!!){
                if(it.date==existingArmyDay?.date){
                    listOfAllArmyDays!![listOfAllArmyDays!!.indexOf(existingArmyDay)] = it
                    armyDays.toMutableList().remove(it)
                }
            }
        }
        listOfAllArmyDays?.addAll(armyDays)

        activityReference.setValue(listOfAllArmyDays?.toList())
    }

    fun deleteAllActivities(){
        activityReference.setValue(listOf<ArmyDay>())
    }

}