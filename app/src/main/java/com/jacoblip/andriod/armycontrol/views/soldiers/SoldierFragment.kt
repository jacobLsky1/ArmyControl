package com.jacoblip.andriod.armycontrol.views.soldiers

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.ArmyActivity
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.data.sevices.ActivitiesViewModel
import com.jacoblip.andriod.armycontrol.data.sevices.SoldiersViewModel
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.views.adapters.ArmyActivityAdapter
import com.jacoblip.andriod.armycontrol.views.adapters.DateOfServiceAdapter
import com.jacoblip.andriod.armycontrol.views.adapters.SoldierActivitiesAdapter
import com.jacoblip.andriod.armycontrol.views.adapters.SoldiersAllSoldiersAdapter

class SoldierFragment(var soldier: Soldier, var callBacks: MainSoldiersFragment.SoldierCallbacks, var callBacks2: MainSoldiersFragment.SoldierSelectedFromRV?): Fragment() {

    lateinit var soldiersViewModel: SoldiersViewModel
    lateinit var activitiesViewModel: ActivitiesViewModel
    var directSoldiersOfSoldier:List<Soldier> = listOf()
    var fragmentInit = false

    lateinit var editSoldierButton: Button
    lateinit var soldierName:TextView
    lateinit var soldierIdNumber:TextView
    lateinit var soldierIsCommander:TextView
    lateinit var soldierPhoneNumber:TextView
    lateinit var soldierCivilianJob:TextView
    lateinit var soldierPosition:TextView
    lateinit var datesOfServiceRV:RecyclerView
    lateinit var soldierIsComingTV: TextView
    lateinit var soldierIsHereTV:TextView
    lateinit var whySoldierIsNotComingTV:TextView
    lateinit var directSoldiersRV:RecyclerView
    lateinit var soldierActivityRV:RecyclerView

    interface EditSoldierCallbacks {
        fun onEditSoldierSelected(soldier: Soldier)
    }

    private var editSoldierCallbacks: EditSoldierCallbacks? = null

    //the callback functions
    override fun onAttach(context: Context) {
        super.onAttach(context)
        editSoldierCallbacks = context as EditSoldierCallbacks?
    }

    override fun onDetach() {
        super.onDetach()
        editSoldierCallbacks = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        soldiersViewModel = ViewModelProvider(requireActivity()).get(SoldiersViewModel::class.java)
        activitiesViewModel = ViewModelProvider(requireActivity()).get(ActivitiesViewModel::class.java)
        soldiersViewModel.currentFragment = this
        soldiersViewModel.getDirectSoldiersForSoldier(soldier)
        val view= inflater.inflate(R.layout.s_fragment_soilder,container,false)
        setUpViews(view)
        setUpObservers()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragment()
        setUpRecyclerViews()
        fragmentInit = true
    }

    private fun setUpObservers(){
        soldiersViewModel.listOfPersonalSoldiersForSoldier.observe(viewLifecycleOwner, Observer {
            if(it!=null){
                directSoldiersOfSoldier = it
                directSoldiersRV.adapter = SoldiersAllSoldiersAdapter(directSoldiersOfSoldier, callBacks,callBacks2!!)
            }
        })


        soldiersViewModel.nowSoldier.observe(viewLifecycleOwner, Observer {
            if(it!=null&&fragmentInit){
                soldier = it
                setFragment()
                soldiersViewModel.getDirectSoldiersForSoldier(soldier)
            }
        })

    }

    private fun setFragment(){
        soldierName.text = soldier.name
        soldierIdNumber.text = soldier.idNumber
        soldierIsCommander.text = Util.getArmyJobByCode(soldier.armyJobMap)
        soldierPhoneNumber.text = soldier.phoneNumber
        soldierCivilianJob.text = soldier.civilianJob
        soldierPosition.text = Util.getPositionByCode(soldier.positionMap)

        editSoldierButton.setOnClickListener {
            editSoldierCallbacks?.onEditSoldierSelected(soldier)
        }
    }

    fun setUpRecyclerViews(){
        directSoldiersRV.layoutManager = LinearLayoutManager(requireContext())
        soldierActivityRV.layoutManager = LinearLayoutManager(requireContext())
        datesOfServiceRV.layoutManager =  LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        var soldierActivities = soldiersViewModel.getSoldierActivities(soldier,activitiesViewModel.listOfArmyDays.value!!)
        var allActivities = getAllActivities()
        soldierActivityRV.adapter = SoldierActivitiesAdapter(soldierActivities,allActivities)
        val dates = getSoldierDatesOfService()
        datesOfServiceRV.adapter = DateOfServiceAdapter(dates)
        (datesOfServiceRV.adapter as DateOfServiceAdapter).notifyDataSetChanged()


    }

    private fun getAllActivities():List<ArmyActivity>{
        val list:MutableList<ArmyActivity> = mutableListOf()
        var armyDays = activitiesViewModel.listOfArmyDays.value!!
        if(armyDays== null)
            armyDays = listOf()

        for(i in armyDays.indices){
            var day = armyDays[i]
            for(j in day!!.activities.indices){
                list.add(day!!.activities[j])
            }
        }

        return list
    }

    fun getSoldierDatesOfService():List<String>{
        val daysOfService = activitiesViewModel.listOfArmyDays.value
        var listOfDates = mutableListOf<String>()
        if(daysOfService!= null){
            for(day in daysOfService){
                if(day?.amountOfSoldiers!!.contains(soldier)){
                    listOfDates.add(day.date)
                }
            }
        }
        return listOfDates
    }

    fun onBackPressed(){
        soldiersViewModel.popStack()
    }


    private fun setUpViews(view: View){
        view.apply {
            editSoldierButton = findViewById(R.id.saveChangesButton)
            soldierName = findViewById(R.id.editTextSoldierName)
            soldierIdNumber = findViewById(R.id.editTextSoldierIdNumber)
            soldierIsCommander = findViewById(R.id.SoldierIsCommanderSwitch)
            soldierPhoneNumber = findViewById(R.id.editTextSoldierAge)
            soldierCivilianJob = findViewById(R.id.editTextMedData)
            soldierPosition = findViewById(R.id.editTextArmyJobAndPosition)
            datesOfServiceRV = findViewById(R.id.datesOfService_RV)
            soldierIsComingTV = findViewById(R.id.isArrivingSwitch)
            soldierIsHereTV = findViewById(R.id.hasArrivedSwitch)
            whySoldierIsNotComingTV  = findViewById(R.id.editTextWhyNotArriving)
            directSoldiersRV = findViewById(R.id.editSoldierDirectSoldiers_RV)
            soldierActivityRV = findViewById(R.id.editSoldierCompletedActivities_RV)

        }
    }


    companion object{
        fun newInstance(soldier: Soldier, callBacks: MainSoldiersFragment.SoldierCallbacks, callBacks2: MainSoldiersFragment.SoldierSelectedFromRV?): SoldierFragment {
            return SoldierFragment(soldier,callBacks,callBacks2)
        }
    }
}