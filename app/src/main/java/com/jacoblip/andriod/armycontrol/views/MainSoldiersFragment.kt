package com.jacoblip.andriod.armycontrol.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.ArmyDay
import com.jacoblip.andriod.armycontrol.data.models.Group
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.data.sevices.MainViewModel
import com.jacoblip.andriod.armycontrol.views.adapters.SoldiersByDateAdapter

class MainSoldiersFragment(var commandPath:String):Fragment() {

    private lateinit var soildersByDateRV:RecyclerView
    private var listOfArmyDays:List<ArmyDay> = listOf()
    lateinit var groupNameTV:TextView
    lateinit var commandersButton: TextView
    lateinit var allSoldiersButton:TextView
    lateinit var operationalListButton:TextView
    lateinit var powerListButton:TextView
    lateinit var viewModel: MainViewModel
    var group:Group? = null
    var listOfAllSoldiers:List<Soldier> = listOf()


    interface ButtonCallbacks {
        fun onButtonSelectedSelected(numberOfFragment:Int,callbacks: SoldierCallbacks)
    }
    interface SoldierCallbacks {
        fun onSoldierSelectedSelected(soldier: Soldier, callbacks: SoldierCallbacks, soldierSelectedCallbacks: RVSoldiersFragment.SoldierSelectedFromRV?)
    }


    private var soldierCallbacks: SoldierCallbacks? = null
    private var buttonCallbacks: ButtonCallbacks? = null



    //the callback functions
    override fun onAttach(context: Context) {
        super.onAttach(context)
        buttonCallbacks = context as ButtonCallbacks?
        soldierCallbacks = context as SoldierCallbacks?
    }

    override fun onDetach() {
        super.onDetach()
        buttonCallbacks = null
        soldierCallbacks = null
    }




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.currentFragment = this
        val view = inflater.inflate(R.layout.fragment_soldiers,container,false)
        view.apply {
            groupNameTV = findViewById(R.id.groupNameTV)
            soildersByDateRV = findViewById(R.id.numberOfSoldiersByDatesRV)
            commandersButton = findViewById(R.id.commandersButton)
            allSoldiersButton = findViewById(R.id.allSoldiersButton)
            powerListButton = findViewById(R.id.powerListButton)
            operationalListButton =    findViewById(R.id.operationalButton)
        }

        setUpObservers()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            soildersByDateRV.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            // TODO: 7/5/2021 get soldiers from view model
            soildersByDateRV.adapter = SoldiersByDateAdapter(listOfArmyDays)

            allSoldiersButton.setOnClickListener { buttonCallbacks!!.onButtonSelectedSelected(1,soldierCallbacks!!) }
            commandersButton.setOnClickListener { buttonCallbacks!!.onButtonSelectedSelected(2,soldierCallbacks!!) }
            powerListButton.setOnClickListener { buttonCallbacks!!.onButtonSelectedSelected(3,soldierCallbacks!!) }
            operationalListButton.setOnClickListener { buttonCallbacks!!.onButtonSelectedSelected(4,soldierCallbacks!!) }
        }
        if (commandPath != "") {
            viewModel.getBiggestGroup()

        }
    }

    fun updateUI(){
        groupNameTV.text = group?.groupName
        allSoldiersButton.text = "0/${listOfAllSoldiers.size}"
    }

    fun setUpObservers(){

        viewModel.biggestGroup.observe(viewLifecycleOwner, Observer {
            if(it!=null){
                viewModel.getGroupOfSoldiers(commandPath)
            }
        })
        viewModel.userGroup.observe(viewLifecycleOwner, Observer { it ->
            if(it!=null){
                group = it
                viewModel.getAllSoldiersFromGroup(it)
                updateUI()
            }
        })
        viewModel.allSoldiers.observe(viewLifecycleOwner, Observer { it ->
            if(it!=null){
                listOfAllSoldiers = it
                updateUI()
            }
        })
    }

    companion object{
        fun newInstance(commandPath: String):MainSoldiersFragment{
            return MainSoldiersFragment(commandPath)
        }
    }
}