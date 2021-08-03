package com.jacoblip.andriod.armycontrol.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.data.sevices.MainViewModel
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.views.adapters.SoldiersAllSoldiersAdapter
import com.jacoblip.andriod.armycontrol.views.adapters.SoldiersCommandersAdapter
import com.jacoblip.andriod.armycontrol.views.adapters.SoldiersPowerListAdapter

class RVSoldiersFragment(var callbacks: MainSoldiersFragment.SoldierCallbacks,var numberSelected: Int): Fragment(){

    lateinit var all_soldiers_RV: RecyclerView
    lateinit var viewModel: MainViewModel

    interface SoldierSelectedFromRV {
        fun onSoldierSelected(soldier: Soldier,addSoldier:Boolean)
    }
    var soldierSelectedCallbacks:SoldierSelectedFromRV? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        soldierSelectedCallbacks = context as SoldierSelectedFromRV
    }

    override fun onDetach() {
        super.onDetach()
        soldierSelectedCallbacks = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.currentFragment = this
        val view= inflater.inflate(R.layout.fragment_all_soilders,container,false)
        view.apply {
            all_soldiers_RV = findViewById(R.id.allSoldiers_RV)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        all_soldiers_RV.layoutManager = LinearLayoutManager(requireContext())

        setUpObservers()
    }

    fun setUpObservers() {
        viewModel.listOfPersonalSoldiers.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                when (numberSelected) {
                    1 -> {
                        all_soldiers_RV.adapter = SoldiersAllSoldiersAdapter(it, callbacks, soldierSelectedCallbacks)
                    }
                    2 -> {
                        all_soldiers_RV.adapter = SoldiersCommandersAdapter(viewModel.listOfUserCommanders, callbacks, soldierSelectedCallbacks)
                    }
                    3 -> {
                        all_soldiers_RV.adapter = SoldiersPowerListAdapter(viewModel.listOfSoldiersWithPower, callbacks, soldierSelectedCallbacks)
                    }
                    4 -> {
                        all_soldiers_RV.adapter = SoldiersAllSoldiersAdapter(it, callbacks, soldierSelectedCallbacks)
                    }
                }

            }
        })
    }

    fun onBackPressed(){
        when (numberSelected) {
            1 -> {
                all_soldiers_RV.adapter = SoldiersAllSoldiersAdapter(viewModel.listOfPersonalSoldiers.value!!, callbacks, soldierSelectedCallbacks)
            }
            2 -> {
                all_soldiers_RV.adapter = SoldiersCommandersAdapter(viewModel.listOfUserCommanders, callbacks, soldierSelectedCallbacks)
            }
            3 -> {
                all_soldiers_RV.adapter = SoldiersPowerListAdapter(viewModel.listOfSoldiersWithPower, callbacks, soldierSelectedCallbacks)
            }
            4 -> {
                all_soldiers_RV.adapter = SoldiersAllSoldiersAdapter(viewModel.listOfPersonalSoldiers.value!!, callbacks, soldierSelectedCallbacks)
            }
        }
    }




    companion object{
        fun newInstance(callbacks: MainSoldiersFragment.SoldierCallbacks,numberSelected:Int):RVSoldiersFragment{
            return RVSoldiersFragment(callbacks,numberSelected)
        }

    }

}