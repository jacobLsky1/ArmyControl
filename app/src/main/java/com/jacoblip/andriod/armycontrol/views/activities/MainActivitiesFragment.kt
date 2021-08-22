package com.jacoblip.andriod.armycontrol.views.activities

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aminography.primecalendar.PrimeCalendar
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.callback.RangeDaysPickCallback
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.ArmyDay
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.data.sevices.ActivitiesViewModel
import com.jacoblip.andriod.armycontrol.data.sevices.SoldiersViewModel
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.views.adapters.AddSoldierToDayAdapter
import com.jacoblip.andriod.armycontrol.views.adapters.SoldiersByDateAdapter
import java.time.LocalDate
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
class MainActivitiesFragment(var commandPath: String):Fragment() {

    lateinit var activitiesViewModel:ActivitiesViewModel
    lateinit var soldiersViewModel:SoldiersViewModel
    lateinit var searchView: SearchView
    lateinit var listView:ListView
    lateinit var addArmyDayButton: Button
    lateinit var groupNameTV:TextView
    lateinit var timeTV:TextClock
    lateinit var dateTV:TextView
    lateinit var allArmyDaysRV:RecyclerView
    lateinit var allActivitiesRV:RecyclerView
    lateinit var deleteAllActivitiesButton:Button


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.a_fragment_main_activities, container, false)

        activitiesViewModel = ViewModelProvider(requireActivity()).get(ActivitiesViewModel::class.java)
        soldiersViewModel = ViewModelProvider(requireActivity()).get(SoldiersViewModel::class.java)

        setUpViews(view)
        setUpObservers()

        return view
    }

    private fun setUpViews(view: View){
        view.apply {
            searchView = findViewById(R.id.activitiesSearchView)
            listView = findViewById(R.id.activitiesSearchListView)
            groupNameTV = findViewById(R.id.activiteisGroupNameTV)
            addArmyDayButton = findViewById(R.id.addArmyDayButton)
            timeTV = findViewById(R.id.textClock)
            allArmyDaysRV = findViewById(R.id.allArmyDaysRV)
            allActivitiesRV = findViewById(R.id.allActivitiesInDayRV)
            deleteAllActivitiesButton = findViewById(R.id.deleteAllActivitesButton)

            allArmyDaysRV.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            allActivitiesRV.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setUpObservers(){
        activitiesViewModel.listOfArmyDays.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it!=null)
            allArmyDaysRV.adapter = SoldiersByDateAdapter(it as List<ArmyDay>,soldiersViewModel.listOfAllSoldiers!!.size)
        })
        Util.currentDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addArmyDayButton.setOnClickListener {
             var valid = checkIfHasClearance()
            if(valid)
             getNewArmyDays()
        }

        deleteAllActivitiesButton.setOnClickListener {
            var valid = checkIfHasClearance()
            if(valid){
                val inflater = layoutInflater
                val dialogView = inflater.inflate(R.layout.a_alert_for_deleting_activitys, null)
                val yesButton = dialogView.findViewById(R.id.yesButton) as Button
                val noButton = dialogView.findViewById(R.id.noButton) as Button

                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setView(dialogView).setCancelable(false)

                val dialog = alertDialog.create()
                dialog.show()

                noButton.setOnClickListener {
                    dialog.dismiss()
                }
                yesButton.setOnClickListener {
                    dialog.dismiss()
                    activitiesViewModel.deleteAllActivities()
                }
            }
        }
    }


    private fun checkIfHasClearance():Boolean{
        if(Util.userCommandPath=="1"){
            return true
        }else{
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.m_alert_user_no_clearance, null)
            val okButton = dialogView.findViewById(R.id.okButton) as Button

            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setView(dialogView).setCancelable(false)

            val dialog = alertDialog.create()
            dialog.show()

            okButton.setOnClickListener {
                dialog.dismiss()
            }
        }
        return false
    }

    private fun getNewArmyDays(){
        val callback = RangeDaysPickCallback { start: PrimeCalendar, end: PrimeCalendar ->
            var nowDate = LocalDate.of(
                LocalDate.now().year,
                LocalDate.now().month,
                LocalDate.now().dayOfMonth
            )
            var startDate = LocalDate.of(start.year, start.month, start.dayOfMonth)
            var endDate = LocalDate.of(end.year, end.month, end.dayOfMonth)
            startDate = startDate.plusMonths(1)
            endDate = endDate.plusMonths(1)
            if(startDate.isBefore(nowDate)){
                Toast.makeText(requireContext(), "אין אפשרות לבחור תאריך שעבר", Toast.LENGTH_LONG).show()
                return@RangeDaysPickCallback
            }
            var listOfServiceDays: MutableList<LocalDate> = mutableListOf(startDate)
            var bool = true
            if (!startDate.equals(endDate)) {
                var date = startDate.plusDays(1)
                while (bool) {
                    if (date.equals(endDate)) {
                        bool = false
                    } else {
                        listOfServiceDays.add(date)
                        date = date.plusDays(1)
                    }
                }
                listOfServiceDays.add(endDate)
            }
            if(listOfServiceDays.size>0) {
                getSoldiersForDays(listOfServiceDays)
            }
        }
        val today = CivilCalendar()
        val datePicker = PrimeDatePicker.dialogWith(today)
            .pickRangeDays(callback)
            .build()
        datePicker.show(childFragmentManager, "SOME_TAG")
    }

    private fun getSoldiersForDays(listOfDays: List<LocalDate>){
        var i = 0
        var allSoldiers = soldiersViewModel.listOfAllSoldiers
        var listOfArmyDays :MutableList<ArmyDay> = mutableListOf()
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.a_add_soldiers_for_day, null)
        var dateTV = dialogView.findViewById(R.id.dayTV) as TextView
        val continueButton = dialogView.findViewById(R.id.commitButton) as Button
        var soldiersRV = dialogView.findViewById(R.id.soldiersToAddRV) as RecyclerView
        var signAllCB = dialogView.findViewById(R.id.signEveryOneCB) as CheckBox
        var signNoOneCB =     dialogView.findViewById(R.id.signNoOneCB) as CheckBox
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
        soldiersRV.layoutManager = LinearLayoutManager(requireContext())
        dateTV.text = listOfDays[i].toString()


        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setView(dialogView).setCancelable(false)

        val dialog = alertDialog.create()
        dialog.show()

        signAllCB.setOnCheckedChangeListener { checkBox, isChecked ->
            Util.soldiersToAdd = mutableListOf()
            if(isChecked) {
                signNoOneCB.isChecked = false
                soldiersRV.adapter  = AddSoldierToDayAdapter(allSoldiers as List<Soldier>, true)
            }
        }
        signNoOneCB.setOnCheckedChangeListener { checkBox, isChecked ->
            Util.soldiersToAdd = mutableListOf()
            if(isChecked) {
                signAllCB.isChecked = false
                soldiersRV.adapter = AddSoldierToDayAdapter(allSoldiers as List<Soldier>, false)
            }
        }

        continueButton.setOnClickListener {
            var listOfSoldiersToAdd = Util.soldiersToAdd
            Util.soldiersToAdd = mutableListOf()
            val armyDay = ArmyDay(listOfDays[i].toString(), listOf(), listOfSoldiersToAdd.toList())
            listOfArmyDays.add(armyDay)

            i++

            if(i==listOfDays.size){
                activitiesViewModel.addArmyDays(listOfArmyDays)
                dialog.dismiss()
            }else{
                soldiersRV.adapter = AddSoldierToDayAdapter(listOf(), false)
                signAllCB.isChecked = false
                signNoOneCB.isChecked = false
                dateTV.text = listOfDays[i].toString()
            }
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

    }

    companion object{
        fun newInstance(commandPath: String): MainActivitiesFragment {
            return MainActivitiesFragment(commandPath)
        }
    }
}