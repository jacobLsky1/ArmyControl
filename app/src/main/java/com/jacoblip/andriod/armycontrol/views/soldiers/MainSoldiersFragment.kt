package com.jacoblip.andriod.armycontrol.views.soldiers

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.ArmyDay
import com.jacoblip.andriod.armycontrol.data.models.Group
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.data.sevices.ActivitiesViewModel
import com.jacoblip.andriod.armycontrol.data.sevices.SoldiersViewModel
import com.jacoblip.andriod.armycontrol.utilities.LinearLayoutHelper
import com.jacoblip.andriod.armycontrol.views.adapters.SoldiersByDateAdapter
import com.mikhaellopez.circularprogressbar.CircularProgressBar


class MainSoldiersFragment(var commandPath: String):Fragment() {

    private lateinit var soildersByDateRV:RecyclerView
    private var listOfArmyDays:List<ArmyDay> = listOf()
    lateinit var groupNameTV:TextView
    lateinit var searchView:SearchView
    lateinit var searchList:ListView
    lateinit var commandersButton: Button
    lateinit var allSoldiersButton:Button
    lateinit var operationalListButton:Button
    lateinit var powerListButton:Button
    lateinit var amountOfSoldiersPresentTV:TextView
    lateinit var amountOfCommandersTV:TextView
    lateinit var noArmyDaysYetTV :TextView
    lateinit var soldiersProgressBar: CircularProgressBar
    lateinit var soldiersViewModel: SoldiersViewModel
    lateinit var activityViewModel:ActivitiesViewModel
    lateinit var noItemsFoundIV:ImageView
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




    @RequiresApi(Build.VERSION_CODES.N)
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
            amountOfSoldiersPresentTV = findViewById(R.id.amountOfSoldiersPresent)
            amountOfCommandersTV = findViewById(R.id.commandersTVSoldiersFragment)
            noArmyDaysYetTV = findViewById(R.id.noArmyDaysYetTV)
            searchList = findViewById(R.id.activitiesSearchListView)
            noItemsFoundIV = findViewById(R.id.noItemsFoundImage)
            soldiersProgressBar = findViewById(R.id.soldiersProgressBar)
            searchView.queryHint = "חפש חייל";
        }

        setUpObservers()
        setSearchView()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {

            soildersByDateRV.layoutManager = LinearLayoutHelper(requireContext())
            val itemSnapHelper: SnapHelper = LinearSnapHelper()
            itemSnapHelper.attachToRecyclerView(soildersByDateRV)
            //soildersByDateRV.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            soildersByDateRV.adapter = SoldiersByDateAdapter(listOfArmyDays,listOfAllSoldiers)

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

    private fun setSearchView(){
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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateUI(){
        soildersByDateRV.adapter = SoldiersByDateAdapter(listOfArmyDays,listOfAllSoldiers)
        groupNameTV.text = group?.groupName
        val numSoldiers = soldiersViewModel.amountOfSoldiersPresent.toFloat()
        amountOfSoldiersPresentTV.text = "דו''ח 1:\n ${numSoldiers.toInt()}/${group?.amountOfSoldiers}"
        amountOfCommandersTV.text = "${soldiersViewModel.listOfUserCommanders.size} מפקדים"
        setSearchView()
        var amountOfSoldiers = group?.amountOfSoldiers?.toFloat()
        if(amountOfSoldiers!=null){
            var num = (numSoldiers/amountOfSoldiers)*100
            soldiersProgressBar.setProgressWithAnimation(num);
        }

    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun setUpObservers(){

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
                if(it.isEmpty()){
                    noArmyDaysYetTV.visibility = View.VISIBLE
                    noItemsFoundIV.visibility = View.VISIBLE
                    soildersByDateRV.visibility = View.GONE
                }else {
                    noArmyDaysYetTV.visibility = View.GONE
                    noItemsFoundIV.visibility = View.GONE
                    soildersByDateRV.visibility = View.VISIBLE
                    listOfArmyDays = it as List<ArmyDay>
                    updateUI()
                }
            }
        })
    }

    companion object{
        fun newInstance(commandPath: String): MainSoldiersFragment {
            return MainSoldiersFragment(commandPath)
        }
    }
}