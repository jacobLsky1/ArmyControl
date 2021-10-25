package com.jacoblip.andriod.armycontrol.views.soldiers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.ArmyActivity
import com.jacoblip.andriod.armycontrol.data.models.ArmyDay
import com.jacoblip.andriod.armycontrol.data.sevices.ActivitiesViewModel
import com.jacoblip.andriod.armycontrol.data.sevices.SoldiersViewModel
import com.jacoblip.andriod.armycontrol.views.adapters.SoldiersAllSoldiersAdapter
import com.jacoblip.andriod.armycontrol.views.adapters.SoldiersCommandersAdapter
import com.jacoblip.andriod.armycontrol.views.adapters.SoldiersOperationalListAdapter
import com.jacoblip.andriod.armycontrol.views.adapters.SoldiersPowerListAdapter

class RVSoldiersFragment(var callbacks: MainSoldiersFragment.SoldierCallbacks, var numberSelected: Int,
                         private var callbacksRV: MainSoldiersFragment.SoldierSelectedFromRV): Fragment(){

    lateinit var all_soldiers_RV: RecyclerView
    lateinit var soldiersViewModel: SoldiersViewModel
    lateinit var activitiesViewModel: ActivitiesViewModel
    lateinit var noSoldiersFoundTV:TextView

    /*

     */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        soldiersViewModel = ViewModelProvider(requireActivity()).get(SoldiersViewModel::class.java)
        activitiesViewModel = ViewModelProvider(requireActivity()).get(ActivitiesViewModel::class.java)
        soldiersViewModel.currentFragment = this
        val view= inflater.inflate(R.layout.s_fragment_all_soilders,container,false)
        view.apply {
            all_soldiers_RV = findViewById(R.id.allSoldiers_RV)
            noSoldiersFoundTV = findViewById(R.id.noSoldiersFoundTV)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        all_soldiers_RV.layoutManager = LinearLayoutManager(requireContext())

        setUpObservers()
    }

    fun setUpObservers() {
        soldiersViewModel.listOfPersonalSoldiers.observe(viewLifecycleOwner, Observer {
            if (it != null && it.size!=0) {
                noSoldiersFoundTV.isVisible = false
                when (numberSelected) {
                    1 -> {
                        all_soldiers_RV.adapter = SoldiersAllSoldiersAdapter(it, callbacks, callbacksRV)
                    }
                    2 -> {
                        all_soldiers_RV.adapter = SoldiersCommandersAdapter(soldiersViewModel.listOfUserCommanders, callbacks, callbacksRV)
                    }
                    3 -> {
                        all_soldiers_RV.adapter = SoldiersPowerListAdapter(soldiersViewModel.listOfSoldiersWithPower, callbacks, callbacksRV)
                    }
                    4 -> {
                        var allArmyDays = activitiesViewModel.listOfArmyDays.value
                        if(allArmyDays==null){
                            allArmyDays = emptyList()
                        }
                        all_soldiers_RV.adapter = SoldiersOperationalListAdapter(it, callbacks, callbacksRV,allArmyDays as List<ArmyDay>)
                    }
                }

            }else{
                noSoldiersFoundTV.isVisible = true
            }

        })
        soldiersViewModel.soldiersDeleted.observe(viewLifecycleOwner, Observer {
            if(it){
                all_soldiers_RV.invalidate()
                soldiersViewModel.soldiersDeleted.postValue(false)
            }
        })
    }

    fun onBackPressed(){
        when (numberSelected) {
            1 -> {
                all_soldiers_RV.adapter = SoldiersAllSoldiersAdapter(soldiersViewModel.listOfPersonalSoldiers.value!!, callbacks, callbacksRV)
            }
            2 -> {
                all_soldiers_RV.adapter = SoldiersCommandersAdapter(soldiersViewModel.listOfUserCommanders, callbacks, callbacksRV)
            }
            3 -> {
                all_soldiers_RV.adapter = SoldiersPowerListAdapter(soldiersViewModel.listOfSoldiersWithPower, callbacks, callbacksRV)
            }
            4 -> {
                var allArmyDays = activitiesViewModel.listOfArmyDays.value
                if(allArmyDays==null){
                    allArmyDays = emptyList()
                }
                all_soldiers_RV.adapter = SoldiersOperationalListAdapter(soldiersViewModel.listOfPersonalSoldiers.value!!, callbacks, callbacksRV,allArmyDays as List<ArmyDay>)
            }
        }
    }





    companion object{
        fun newInstance(callbacks: MainSoldiersFragment.SoldierCallbacks, numberSelected:Int, callbacksRV: MainSoldiersFragment.SoldierSelectedFromRV): RVSoldiersFragment {
            return RVSoldiersFragment(callbacks,numberSelected,callbacksRV)
        }

    }

}