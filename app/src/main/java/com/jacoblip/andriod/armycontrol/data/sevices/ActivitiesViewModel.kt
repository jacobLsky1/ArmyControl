package com.jacoblip.andriod.armycontrol.data.sevices

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.jacoblip.andriod.armycontrol.data.models.ArmyActivity
import com.jacoblip.andriod.armycontrol.data.models.ArmyDay
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.utilities.Util
import java.time.LocalDate

class ActivitiesViewModel(repository: Repository,context: Context):ViewModel() {

    private var activityReference = FirebaseDatabase.getInstance().getReference("ימי שירות")
    private var soldiersReference = FirebaseDatabase.getInstance().getReference("חיילים")

    private var listOfAllArmyDays:MutableList<ArmyDay?>? = mutableListOf()


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

    fun addArmyDays(days:List<ArmyDay>){
        var armyDays = days.toMutableList()
        for(i in armyDays.indices){
            var newDay = armyDays[i]
            for(existingArmyDay in listOfAllArmyDays!!){
                if(newDay.date==existingArmyDay?.date){
                    listOfAllArmyDays!![listOfAllArmyDays!!.indexOf(existingArmyDay)] = newDay
                     armyDays.removeAt(i)
                }
            }
        }
        listOfAllArmyDays?.addAll(armyDays)
        listOfAllArmyDays?.sortedBy {
            it!!.date
        }

        activityReference.setValue(listOfAllArmyDays?.toList())
    }

    fun saveActivity(oldActivity:ArmyActivity?,newActivity:ArmyActivity,soldiers: List<Soldier>){
        var armyDay = Util.currentDate.value
        if(armyDay==null){
            var dayExists:ArmyDay? = null
            for(day in listOfAllArmyDays!!){
                if(day?.date==newActivity.date){
                    dayExists = day
                    break
                }
            }
            if(dayExists!=null){
                armyDay = dayExists
            }else{
                armyDay = ArmyDay(newActivity.date, listOf(newActivity),soldiers)
                var list = listOf(armyDay)
                addArmyDays(list)
                Util.currentDate.postValue(armyDay)
                return
            }
        }
        val listOfActivities = armyDay.activities.toMutableList()
        if(oldActivity!=null){
            var indx = listOfActivities.indexOf(oldActivity)
            listOfActivities[indx] = newActivity
        }else{
            listOfActivities.add(newActivity)
        }
        listOfActivities.sortBy {
            it.startTime
        }
        armyDay.activities = listOfActivities.toList()
        var list = listOf(armyDay)
        addArmyDays(list)
        Util.currentDate.postValue(armyDay)
    }

    fun deleteAllActivities(allSoldiers:List<Soldier?>?){
        if(allSoldiers!=null) {
            for (soldier in allSoldiers){
                soldier!!.Activates = listOf()
            }
        }
        soldiersReference.setValue(allSoldiers)
        activityReference.setValue(listOf<ArmyDay>())
        Util.currentDate.postValue(null)
    }

}