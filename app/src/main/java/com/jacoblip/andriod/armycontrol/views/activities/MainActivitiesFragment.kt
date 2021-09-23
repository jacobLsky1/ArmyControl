package com.jacoblip.andriod.armycontrol.views.activities

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.aminography.primecalendar.PrimeCalendar
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.callback.RangeDaysPickCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.ArmyActivity
import com.jacoblip.andriod.armycontrol.data.models.ArmyDay
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.data.sevices.ActivitiesViewModel
import com.jacoblip.andriod.armycontrol.data.sevices.SoldiersViewModel
import com.jacoblip.andriod.armycontrol.utilities.AddingSoldierHelper
import com.jacoblip.andriod.armycontrol.utilities.LinearLayoutHelper
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.views.adapters.*
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
    lateinit var selectedDateTV: TextView
    lateinit var pickedDayActivitiesTV:TextView
    lateinit var timeTV:TextClock
    lateinit var allArmyDaysRV:RecyclerView
    lateinit var allActivitiesRV:RecyclerView
    lateinit var deleteAllActivitiesButton:Button
    lateinit var addActivityFAB: FloatingActionButton
    lateinit var noDaysYetTV: TextView
    lateinit var noArmyDayImageIV:ImageView
    var allArmyDays :List<ArmyDay?>? = null



    interface OnActivityPressedCallBacks{
        fun onActivityPressed(activity: ArmyActivity?)
    }
    private var activityCallbacks:OnActivityPressedCallBacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as OnActivityPressedCallBacks
    }

    override fun onDetach() {
        super.onDetach()
        activityCallbacks = null
    }



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
            selectedDateTV = findViewById(R.id.selectedDateTV)
            allArmyDaysRV = findViewById(R.id.allArmyDaysRV)
            allActivitiesRV = findViewById(R.id.allActivitiesInDayRV)
            pickedDayActivitiesTV = findViewById(R.id.pickedDayActivitiesTV)
            deleteAllActivitiesButton = findViewById(R.id.deleteAllActivitesButton)
            addActivityFAB = findViewById(R.id.addActivityFAB)
            noDaysYetTV = findViewById(R.id.noDaysYetTV)
            noArmyDayImageIV = findViewById(R.id.noArmyDaysImage)
            allActivitiesRV.layoutManager = LinearLayoutManager(requireContext())
            allArmyDaysRV.layoutManager = LinearLayoutHelper(requireContext())
            val itemSnapHelper: SnapHelper = LinearSnapHelper()
            itemSnapHelper.attachToRecyclerView(allArmyDaysRV)
            searchView.queryHint = "חפש פעילות";
        }
    }

    private fun setUpObservers(){
        activitiesViewModel.listOfArmyDays.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                if(it.isEmpty()){
                    noDaysYetTV.visibility = View.VISIBLE
                    noArmyDayImageIV.visibility = View.VISIBLE
                    allArmyDaysRV.visibility = View.GONE
                    selectedDateTV.visibility = View.GONE
                    pickedDayActivitiesTV.visibility = View.GONE
                    allActivitiesRV.visibility = View.GONE
                    addActivityFAB.visibility = View.GONE
                    deleteAllActivitiesButton.isEnabled = false

                }else {
                    noDaysYetTV.visibility = View.GONE
                    noArmyDayImageIV.visibility = View.GONE
                    allArmyDaysRV.visibility = View.VISIBLE
                    selectedDateTV.visibility = View.VISIBLE
                    pickedDayActivitiesTV.visibility = View.VISIBLE
                    allActivitiesRV.visibility = View.VISIBLE
                    addActivityFAB.visibility = View.VISIBLE
                    deleteAllActivitiesButton.isEnabled = true
                    allArmyDaysRV.adapter = SoldiersByDateAdapter(
                            it as List<ArmyDay>,
                            soldiersViewModel.listOfPersonalSoldiers.value!!
                    )
                    allArmyDays = it
                }
            }
        })
        Util.currentDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                var localDate = LocalDate.parse(it.date)
                selectedDateTV.text =
                    "${Util.getDayOfWeek(localDate.dayOfWeek.toString())} - ${it.date}"
                //allArmyDaysRV.scrollToPosition(allArmyDays!!.indexOf(it))
                allActivitiesRV.adapter = ArmyActivityAdapter(it.activities,soldiersViewModel.listOfPersonalSoldiers.value!!, activityCallbacks!!)
                (allActivitiesRV.adapter as ArmyActivityAdapter).notifyDataSetChanged()
            } else {
                selectedDateTV.text = "בחר/הוסף יום שירות"
                allActivitiesRV.adapter = ArmyActivityAdapter(listOf(), listOf(),null)
                (allActivitiesRV.adapter as ArmyActivityAdapter).notifyDataSetChanged()
            }

        })
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allActivitiesRV.adapter = ArmyActivityAdapter(listOf(), listOf(), null)
        (allActivitiesRV.adapter as ArmyActivityAdapter).notifyDataSetChanged()

        addArmyDayButton.setOnClickListener {
             var valid = checkIfHasClearance()
            if(valid)
             getNewArmyDays()
        }

        addActivityFAB.setOnClickListener {
            activityCallbacks?.onActivityPressed(null)
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
                    activitiesViewModel.deleteAllActivities(soldiersViewModel.listOfAllSoldiers)
                }
            }
        }
        setSearchView()
    }

    fun setSearchView(){
        var listOfNames:MutableList<String> = mutableListOf()
        var armyDays = activitiesViewModel.listOfArmyDays.value?: mutableListOf()
        var activityHashMap:HashMap<String, ArmyActivity> = hashMapOf()
        for(day in armyDays)
        for (actvity in day!!.activities){
            var title = "${actvity.date} - ${actvity.startTime} - ${actvity.name}"
            listOfNames.add(title)
            activityHashMap.put(title, actvity)
        }
        var adapter :ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            listOfNames
        )
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val string = adapter.getItem(position).toString()
            goToSoldier(activityHashMap, string)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {

                goToSoldier(activityHashMap, text!!)

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val text = newText!!

                if (TextUtils.isEmpty(text)) {
                    listView.visibility = View.GONE
                } else {
                    listView.visibility = View.VISIBLE
                }
                adapter.filter.filter(newText)
                return true
            }
        })
    }

    fun goToSoldier(hashMap: HashMap<String, ArmyActivity>, string: String){

        searchView.setQuery("", false)
        searchView.isIconified = true
        listView.visibility = View.GONE

        if(string.isNotEmpty())
            activityCallbacks?.onActivityPressed(hashMap[string]!!)

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
        var soldiersRV = dialogView.findViewById(R.id.soldiersToAddRV) as ListView
        var signAllCB = dialogView.findViewById(R.id.signEveryOneCB) as CheckBox
        var signNoOneCB =     dialogView.findViewById(R.id.signNoOneCB) as CheckBox
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
        dateTV.text = "${Util.getDayOfWeek(listOfDays[i].dayOfWeek.toString())} - ${listOfDays[i]}"


        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setView(dialogView).setCancelable(false)

        val dialog = alertDialog.create()
        dialog.show()

        signNoOneCB.isChecked = true
        AddingSoldierHelper.soldiersToAdd = mutableListOf()
        soldiersRV.adapter  = AddSoldierToDayAdapter(
                requireContext(),
                allSoldiers as List<Soldier>,
                false,
                false
        )

        signAllCB.setOnCheckedChangeListener { checkBox, isChecked ->
            if(isChecked) {
                signNoOneCB.isChecked = false
                AddingSoldierHelper.soldiersToAdd.addAll(allSoldiers as List<Soldier>)
                soldiersRV.adapter  = AddSoldierToDayAdapter(
                    requireContext(),
                    allSoldiers as List<Soldier>,
                    true,
                    false
                )
            }
        }
        signNoOneCB.setOnCheckedChangeListener { checkBox, isChecked ->
            if(isChecked) {
                signAllCB.isChecked = false
                AddingSoldierHelper.soldiersToAdd = mutableListOf()
                soldiersRV.adapter  = AddSoldierToDayAdapter(
                    requireContext(),
                    allSoldiers as List<Soldier>,
                    false,
                    false
                )
            }
        }

        continueButton.setOnClickListener {
            var listOfSoldiersToAdd = getArmySoldiersFromAdapter()
            val armyDay = ArmyDay(listOfDays[i].toString(), listOf(), listOfSoldiersToAdd.toList())
            listOfArmyDays.add(armyDay)

            i++

            if(i==listOfDays.size){
                activitiesViewModel.addArmyDays(listOfArmyDays)
                dialog.dismiss()
            }else{
                soldiersRV.adapter  = AddSoldierToDayAdapter(
                    requireContext(),
                    listOf(),
                    false,
                    false
                )
                signAllCB.isChecked = false
                signNoOneCB.isChecked = false
                dateTV.text = listOfDays[i].toString()
            }
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

    }

    fun getArmySoldiersFromAdapter():List<String>{
        var list = AddingSoldierHelper.soldiersToAdd
        var listOfIDs = mutableListOf<String>()
        for(soldier in list){
            listOfIDs.add(soldier.idNumber)
        }
        AddingSoldierHelper.soldiersToAdd = mutableListOf()
        return listOfIDs
    }

    companion object{
        fun newInstance(commandPath: String): MainActivitiesFragment {
            return MainActivitiesFragment(commandPath)
        }
    }
}