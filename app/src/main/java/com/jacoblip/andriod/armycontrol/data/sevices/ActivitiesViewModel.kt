package com.jacoblip.andriod.armycontrol.data.sevices

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.jacoblip.andriod.armycontrol.data.models.ArmyActivity
import com.jacoblip.andriod.armycontrol.data.models.ArmyDay
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.utilities.Util
import java.time.LocalDate
@RequiresApi(Build.VERSION_CODES.O)
class ActivitiesViewModel(repository: Repository,context: Context):ViewModel() {

    private var activityReference = Util.groupRef.child("service days")
    private var soldiersReference = Util.groupRef.child("soldiers")

    private var listOfAllArmyDays:MutableList<ArmyDay?>? = mutableListOf()


    private var _listOfArmyDays = MutableLiveData<List<ArmyDay?>?>()
    var listOfArmyDays : LiveData<List<ArmyDay?>?> =_listOfArmyDays

    var activitiesDeleted : MutableLiveData<Boolean> = MutableLiveData(false)

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
                listOfAllArmyDays!!.sortBy { LocalDate.parse(it!!.date)  }
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
            for(day in armyDays){
                var list = listOfAllArmyDays!!.filter { it!!.date == day.date }
                if(list.isEmpty()){
                    listOfAllArmyDays?.add(day)
                }
            }
            for(existingArmyDay in listOfAllArmyDays!!){
                if(newDay.date==existingArmyDay?.date){
                    listOfAllArmyDays!![listOfAllArmyDays!!.indexOf(existingArmyDay)] = newDay
                }
            }
        }
        listOfAllArmyDays?.sortedBy {
            it!!.date
        }
        activityReference.setValue(listOfAllArmyDays?.toList())
    }

    fun saveActivity(oldActivity:ArmyActivity?,newActivity:ArmyActivity,soldiers: List<String>){
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

    fun removeActivities(activities:List<ArmyActivity>,armyDay: ArmyDay){
       var armyDayActivities = armyDay.activities.toMutableList()

        for(activity in activities){
            armyDayActivities.remove(activity)
        }
        for(i in listOfAllArmyDays!!.indices){
            var curDay = listOfAllArmyDays!![i]
            if(curDay?.date==armyDay.date){
                listOfAllArmyDays!![i]!!.activities = armyDayActivities
                break
            }
        }
        activityReference.setValue(listOfAllArmyDays?.toList())
    }

    fun editArmyDay(editedArmyDay:ArmyDay){
        for(day in listOfAllArmyDays!!){
            if(editedArmyDay.date==day!!.date){
                listOfAllArmyDays!![listOfAllArmyDays!!.indexOf(day)] = editedArmyDay
                break
            }
        }
        activityReference.setValue(listOfAllArmyDays?.toList())
    }

    fun deleteArmyDay(armyDay: ArmyDay){
        listOfAllArmyDays!!.remove(armyDay)
        activityReference.setValue(listOfAllArmyDays?.toList())
    }

    fun updateSoldierIDForDays(oldID:String,newID:String){
        if(listOfAllArmyDays?.size!=0){
            for(day in listOfAllArmyDays!!){
                if(day!!.listOfSoldiers.contains(oldID)){
                    var list = day!!.listOfSoldiers.toMutableList()
                    list[day!!.listOfSoldiers.indexOf(oldID)] = newID
                    day.listOfSoldiers = list.toList()
                }
            }
            activityReference.setValue(listOfAllArmyDays?.toList())
        }
    }

    fun removeSoldiersFromDays(soldiers:List<Soldier>){
        if(listOfAllArmyDays?.size!=0) {
            for (soldier in soldiers) {
                for (day in listOfAllArmyDays!!){
                    if(day!!.listOfSoldiers.contains(soldier.idNumber)){
                        for(activity in day!!.activities){
                            var list = activity.attendees.toMutableList()
                            if(list.contains(soldier.idNumber)){
                                list.remove(soldier.idNumber)
                            }
                        }
                        var list = day!!.listOfSoldiers.toMutableList()
                        list.remove(soldier.idNumber)
                    }
                }
            }
        }
    }

}