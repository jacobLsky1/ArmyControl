package com.jacoblip.andriod.armycontrol.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.data.sevices.MainViewModel
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.views.adapters.ArmyActivityAdapter
import com.jacoblip.andriod.armycontrol.views.adapters.DateOfServiceAdapter
import com.jacoblip.andriod.armycontrol.views.adapters.SoldiersAllSoldiersAdapter

class SoldierFragment(soldier: Soldier,callBacks: MainSoldiersFragment.SoldierCallbacks,callBacks2: RVSoldiersFragment.SoldierSelectedFromRV?): Fragment() {

    lateinit var viewModel: MainViewModel
    val soldier = soldier
    val callBacks = callBacks
    var callBacks2 = callBacks2

    lateinit var editSoldierButton: Button
    lateinit var soldierName:TextView
    lateinit var soldierIdNumber:TextView
    lateinit var soldierIsCommander:TextView
    lateinit var soldierPhoneNumber:TextView
    lateinit var soldierCivilianJob:TextView
    lateinit var soldierPosition:TextView
    lateinit var datesOfServiceRV:RecyclerView
    lateinit var soldierIsComingSwitch: Switch
    lateinit var soldierIsHereSwitch:Switch
    lateinit var whySoldierIsNotComing:EditText
    lateinit var directSoldiersRV:RecyclerView
    lateinit var soldierActivityRV:RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.currentFragment = this
        val view= inflater.inflate(R.layout.fragment_soilder,container,false)
        setUpViews(view)
        setFragment()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerViews()
    }

    fun setFragment(){
        soldierName.text = soldier.name
        soldierIdNumber.text = soldier.idNumber
        soldierIsCommander.text = if(soldier.isCommander){"מפקד"}else{"חייל"}
        soldierPhoneNumber.text = soldier.phoneNumber
        soldierCivilianJob.text = soldier.civilianJob
        soldierPosition.text = Util.getPositionByCode(soldier.stationMap)

    }

    fun setUpRecyclerViews(){
        directSoldiersRV.layoutManager = LinearLayoutManager(requireContext())
        soldierActivityRV.layoutManager = LinearLayoutManager(requireContext())
        datesOfServiceRV.layoutManager =  LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

        directSoldiersRV.adapter = SoldiersAllSoldiersAdapter(soldier.listOfDirectSoldiers, callBacks,callBacks2)
        soldierActivityRV.adapter = ArmyActivityAdapter(listOf())
        datesOfServiceRV.adapter = DateOfServiceAdapter(listOf())


    }

    fun onBackPressed(){
        directSoldiersRV.adapter = SoldiersAllSoldiersAdapter(soldier.listOfDirectSoldiers, callBacks,callBacks2)
    }


    fun setUpViews(view: View){
        view.apply {
            editSoldierButton = findViewById(R.id.editSoldierButton)
            soldierName = findViewById(R.id.soldierFragmentSoldierName)
            soldierIdNumber = findViewById(R.id.soldierFragmentSoldierIdNumber)
            soldierIsCommander = findViewById(R.id.soldierFragmentSoldierIsCommander)
            soldierPhoneNumber = findViewById(R.id.soldierFragmentSoldierPhone)
            soldierCivilianJob = findViewById(R.id.soldierCivilianJob)
            soldierPosition = findViewById(R.id.soldierFragmentArmyJobAndPosition)
            datesOfServiceRV = findViewById(R.id.datesOfService_RV)
            soldierIsComingSwitch = findViewById(R.id.IsArrivingSwitch)
            soldierIsHereSwitch = findViewById(R.id.hasArrivedSwitch)
            whySoldierIsNotComing  = findViewById(R.id.editTextWhyNotArriving)
            directSoldiersRV = findViewById(R.id.directSoldiers_RV)
            soldierActivityRV = findViewById(R.id.completedActivities_RV)

        }
    }

    companion object{
        fun newInstance(soldier: Soldier,callBacks: MainSoldiersFragment.SoldierCallbacks,callBacks2:RVSoldiersFragment.SoldierSelectedFromRV?):SoldierFragment{
            return SoldierFragment(soldier,callBacks,callBacks2)
        }
    }
}