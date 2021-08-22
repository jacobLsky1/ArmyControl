package com.jacoblip.andriod.armycontrol.views.soldiers

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
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
import com.jacoblip.andriod.armycontrol.data.sevices.ActivitiesViewModel
import com.jacoblip.andriod.armycontrol.data.sevices.SoldiersViewModel
import com.jacoblip.andriod.armycontrol.views.adapters.SoldiersByDateAdapter


class MainSoldiersFragment(var commandPath: String):Fragment() {

    private lateinit var soildersByDateRV:RecyclerView
    private var listOfArmyDays:List<ArmyDay> = listOf()
    lateinit var groupNameTV:TextView
    lateinit var searchView:SearchView
    lateinit var searchList:ListView
    lateinit var commandersButton: TextView
    lateinit var allSoldiersButton:TextView
    lateinit var operationalListButton:TextView
    lateinit var powerListButton:TextView
    lateinit var soldiersViewModel: SoldiersViewModel
    lateinit var activityViewModel:ActivitiesViewModel
    var group:Group? = null
    var listOfAllSoldiers:List<Soldier> = listOf()


    interface ButtonCallbacks {
        fun onButtonSelectedSelected(numberOfFragment: Int, soldierCallbacks: SoldierCallbacks, soldierRVCallbacks: SoldierSelectedFromRV)
    }
    interface SoldierCallbacks {
        fun onSoldierSelectedSelected(
            soldier: Soldier,
            callbacks: SoldierCallbacks,
            soldierSelectedCallbacks: SoldierSelectedFromRV?
        )
    }

    interface SoldierSelectedFromRV {
        fun onSoldierSelectedFromRV(soldier: Soldier,addSoldier:Boolean)
    }

    private var soldierSelectedCallbacks: SoldierSelectedFromRV? = null
    private var soldierCallbacks: SoldierCallbacks? = null
    private var buttonCallbacks: ButtonCallbacks? = null



    //the callback functions
    override fun onAttach(context: Context) {
        super.onAttach(context)
        buttonCallbacks = context as ButtonCallbacks?
        soldierCallbacks = context as SoldierCallbacks?
        soldierSelectedCallbacks = context as SoldierSelectedFromRV
    }

    override fun onDetach() {
        super.onDetach()
        buttonCallbacks = null
        soldierCallbacks = null
        soldierSelectedCallbacks = null
    }




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        soldiersViewModel = ViewModelProvider(requireActivity()).get(SoldiersViewModel::class.java)
        activityViewModel = ViewModelProvider(requireActivity()).get(ActivitiesViewModel::class.java)
        soldiersViewModel.currentFragment = this
        val view = inflater.inflate(R.layout.s_fragment_main_soldiers, container, false)
        view.apply {
            groupNameTV = findViewById(R.id.groupNameTV)
            soildersByDateRV = findViewById(R.id.numberOfSoldiersByDatesRV)
            commandersButton = findViewById(R.id.commandersButton)
            allSoldiersButton = findViewById(R.id.allSoldiersButton)
            powerListButton = findViewById(R.id.powerListButton)
            operationalListButton =    findViewById(R.id.operationalButton)
            searchView = findViewById(R.id.soldierSearchView)
            searchList = findViewById(R.id.activitiesSearchListView)
            searchView.queryHint = "חפש חייל";
        }

        setUpObservers()
        setSearchView()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            soildersByDateRV.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            // TODO: 7/5/2021 get soldiers from view model
            soildersByDateRV.adapter = SoldiersByDateAdapter(listOfArmyDays,listOfAllSoldiers.size)

            allSoldiersButton.setOnClickListener { buttonCallbacks!!.onButtonSelectedSelected(
                1,
                soldierCallbacks!!,
                soldierSelectedCallbacks!!
            ) }
            commandersButton.setOnClickListener { buttonCallbacks!!.onButtonSelectedSelected(
                2,
                soldierCallbacks!!,
                soldierSelectedCallbacks!!
            ) }
            powerListButton.setOnClickListener { buttonCallbacks!!.onButtonSelectedSelected(
                3,
                soldierCallbacks!!,
                soldierSelectedCallbacks!!
            ) }
            operationalListButton.setOnClickListener { buttonCallbacks!!.onButtonSelectedSelected(
                4,
                soldierCallbacks!!,
                soldierSelectedCallbacks!!
            ) }
        }

    }

    fun setSearchView(){
        var listOfNames:MutableList<String> = mutableListOf()
        var soldierHashMap:HashMap<String, Soldier> = hashMapOf()
        for (soldier in listOfAllSoldiers){
            listOfNames.add(soldier.name)
            soldierHashMap.put(soldier.name, soldier)
        }
        var adapter :ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            listOfNames
        )
        searchList.adapter = adapter

        searchList.setOnItemClickListener { parent, view, position, id ->
            val string = adapter.getItem(position).toString()
            goToSoldier(soldierHashMap,string)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {

                goToSoldier(soldierHashMap,text!!)

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val text = newText!!

                if (TextUtils.isEmpty(text)) {
                    searchList.visibility = View.GONE
                } else {
                    searchList.visibility = View.VISIBLE
                }
                adapter.filter.filter(newText)
                return true
            }
        })
    }

    fun goToSoldier(hashMap:HashMap<String,Soldier>,string: String){

        searchView.setQuery("", false)
        searchView.isIconified = true
        searchList.visibility = View.GONE

        if(string.isNotEmpty())
        soldierCallbacks?.onSoldierSelectedSelected(hashMap[string!!]!!,soldierCallbacks!!,soldierSelectedCallbacks)

    }

    fun updateUI(){
        soildersByDateRV.adapter = SoldiersByDateAdapter(listOfArmyDays,listOfAllSoldiers.size)
        groupNameTV.text = group?.groupName
        allSoldiersButton.text = "0/${group?.amountOfSoldiers}"
        setSearchView()
    }

    fun setUpObservers(){

        soldiersViewModel.userGroup.observe(viewLifecycleOwner, Observer { it ->
            if (it != null) {
                group = it
                updateUI()
            }
        })
        soldiersViewModel.listOfPersonalSoldiers.observe(viewLifecycleOwner, Observer { it ->
            if (it != null) {
                listOfAllSoldiers = it
                updateUI()
            }
        })

        activityViewModel.listOfArmyDays.observe(viewLifecycleOwner, Observer {
            if(it!=null){
                listOfArmyDays = it as List<ArmyDay>
                updateUI()
            }
        })
    }

    companion object{
        fun newInstance(commandPath: String): MainSoldiersFragment {
            return MainSoldiersFragment(commandPath)
        }
    }
}