package com.jacoblip.andriod.armycontrol.views.activities

import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.callback.SingleDayPickCallback
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.ArmyActivity
import com.jacoblip.andriod.armycontrol.data.models.ArmyDay
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.data.sevices.ActivitiesViewModel
import com.jacoblip.andriod.armycontrol.data.sevices.SoldiersViewModel
import com.jacoblip.andriod.armycontrol.utilities.AddingSoldierHelper
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.views.adapters.AddSoldierToActivityAdapter
import com.jacoblip.andriod.armycontrol.views.adapters.AddSoldierToDayAdapter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
class AddOrEditNewActivityFragment(var armyActivity: ArmyActivity?,var commandPath:String,val listOfSoldiers:kotlin.collections.List<Soldier>?):Fragment() {

    lateinit var activitesViewModel: ActivitiesViewModel
    lateinit var soldiersViewModel:SoldiersViewModel
    lateinit var activityTypeSpinner:Spinner
    lateinit var activityNameET:EditText
    lateinit var startTimeButton: Button
    lateinit var endTimeButton:Button
    lateinit var dateButton: Button
    lateinit var startTimeTV:TextView
    lateinit var endTimeTV:TextView
    lateinit var dateTV:TextView
    lateinit var activityLocationET:EditText
    lateinit var addSoldiersButton: Button
    lateinit var participantsRV:RecyclerView
    lateinit var saveActivityButton:Button
    lateinit var activityCompletedSwitch: Switch
    lateinit var activityCompletedTV:TextView
    var armyDay: ArmyDay? = null
    var soldiersForActivity = mutableListOf<Soldier>()

    var dateOfActivity:String = ""
    var activityStartTime:String =""
    var activityEndTime:String =""


    override fun onDetach() {
        super.onDetach()
        Util.soldiersToAddToActivityLD.postValue(null)
    }




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.a_fragment_add_new_activity, container, false)
        activitesViewModel = ViewModelProvider(requireActivity()).get(ActivitiesViewModel::class.java)
        soldiersViewModel = ViewModelProvider(requireActivity()).get(SoldiersViewModel::class.java)
        AddingSoldierHelper.soldiersAlreadyExistingNotFromGroup = mutableListOf()
        soldiersViewModel.currentFragment = this
        if(listOfSoldiers!=null){
            soldiersForActivity.addAll(listOfSoldiers)
        }

        if(armyActivity!=null){
            dateOfActivity = armyActivity!!.date
            activityStartTime = armyActivity!!.startTime
            activityEndTime = armyActivity!!.endTime
        }

        setUpViews(view)
        setUpObservers()
        return view
    }

    private fun setUpViews(view: View){
        view.apply {

            activityTypeSpinner = findViewById(R.id.typeOfActivitySpinner)
            activityNameET = findViewById(R.id.activityNameET)
            dateButton = findViewById(R.id.activityDateButton)
            startTimeButton = findViewById(R.id.activityStartTimeButton)
            endTimeButton = findViewById(R.id.activityEndTimeButton)
            startTimeTV = findViewById(R.id.activityStartTimeTV)
            endTimeTV = findViewById(R.id.activityEndTimeTV)
            dateTV = findViewById(R.id.activityDateTV)
            activityLocationET = findViewById(R.id.activityLocationET)
            addSoldiersButton = findViewById(R.id.addSomeSoldiersButton)
            participantsRV = findViewById(R.id.whoIsAttendingRV)
            saveActivityButton = findViewById(R.id.saveActivityButton)
            activityCompletedSwitch = findViewById(R.id.activityCompletedSwitch)
            activityCompletedTV = findViewById(R.id.activityCompletedTV)

            if(Util.userCommandPath.length>3){
                addSoldiersButton.isEnabled = false
            }

            participantsRV.layoutManager = StaggeredGridLayoutManager(
                3,
                StaggeredGridLayoutManager.VERTICAL
            )
            var adapter :ArrayAdapter<String> = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                Util.activityTypes
            )
            activityTypeSpinner.adapter = adapter
            armyDay = Util.currentDate.value
        }

        setUpUI(armyActivity != null)
    }

    private fun setUpObservers(){

        Util.soldiersToAddToActivityLD.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                var allSoldiers = soldiersViewModel.listOfAllSoldiers
                if(!allSoldiers.isNullOrEmpty()) {
                    for (soldier in allSoldiers!!) {
                        if (it.contains(soldier!!.idNumber)) {
                            if (Util.userCommandPath.length != 1) {
                                if (soldier.positionMap.length > 1) {
                                    if (soldier.positionMap[2] == Util.userCommandPath[2]) {
                                        soldiersForActivity.add(soldier)
                                    } else {
                                        AddingSoldierHelper.soldiersAlreadyExistingNotFromGroup.add(
                                            soldier.idNumber
                                        )
                                    }
                                } else {
                                    AddingSoldierHelper.soldiersAlreadyExistingNotFromGroup.add(
                                        soldier.idNumber
                                    )
                                }
                            } else {
                                soldiersForActivity.add(soldier)
                            }
                        }
                    }
                }
                participantsRV.adapter = AddSoldierToActivityAdapter(soldiersForActivity)
            }
        })
    }

    private fun setUpUI(hasActivity: Boolean){

        if(hasActivity){
            var position = Util.activityTypes.indexOf(armyActivity!!.type)
            activityTypeSpinner.setSelection(position)
            activityNameET.setText(armyActivity!!.name, TextView.BufferType.EDITABLE)
            dateTV.text = dateOfActivity
            startTimeTV.text = activityStartTime
            endTimeTV.text = activityEndTime
            activityLocationET.setText(armyActivity!!.location, TextView.BufferType.EDITABLE);
            Util.soldiersToAddToActivityLD.postValue(armyActivity?.attendees)
            activityCompletedSwitch.isChecked = armyActivity!!.completed
            if(armyActivity!!.completed){
                activityCompletedTV.text = "כן"
            }else{
                activityCompletedTV.text = "לא"
            }
        }else{
            dateTV.text = armyDay?.date ?: "הכנס תאריך"
            startTimeTV.text = "הכנס שעת התחלה"
            endTimeTV.text = "הכנס שעת סיום"
            activityCompletedTV.text = "לא"
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityCompletedSwitch.setOnCheckedChangeListener { compoundButton, isChecked ->
            if(isChecked){
                activityCompletedTV.text = "כן"
            }else{
                activityCompletedTV.text = "לא"
            }
        }

        saveActivityButton.setOnClickListener {
            var type = activityTypeSpinner.selectedItem.toString()
            var name = activityNameET.text.toString()
            var date = dateTV.text.toString()
            var startTime = startTimeTV.text.toString()
            var endTime = endTimeTV.text.toString()
            var location = activityLocationET.text.toString()
            var isCompleted = activityCompletedSwitch.isChecked
            if(type.isEmpty()||name.isEmpty()||date.isEmpty()||startTime.isEmpty()||endTime.isEmpty()){
                alertUserError(name, date, startTime, endTime)
            }else {
                var soldiers =  Util.soldiersToAddToActivityLD.value?: listOf()
                var list:MutableList<String> = mutableListOf()
                val allSoldiers = soldiersViewModel.listOfAllSoldiers
                if(!allSoldiers.isNullOrEmpty()) {
                    for (soldier in allSoldiers!!) {
                        if (soldiers.contains(soldier!!.idNumber)) {
                            list.add(soldier!!.idNumber)
                        }
                    }
                }
                list.addAll(AddingSoldierHelper.soldiersAlreadyExistingNotFromGroup)
                val newArmyActivity = ArmyActivity(type, name, date, startTime, endTime, location, list, isCompleted)
                    activitesViewModel.saveActivity(armyActivity, newArmyActivity,list)
                    soldiersViewModel.addActivityToSoldiers(armyActivity,newArmyActivity)



                requireActivity().supportFragmentManager.popBackStack()
                //soldiersViewModel.wasInActivityEdit.postValue(true)
                Util.soldiersToAddToActivityLD.postValue(null)
            }
        }

        dateButton.setOnClickListener {
            val callback = SingleDayPickCallback { day ->
                var nowDate = LocalDate.of(
                    LocalDate.now().year,
                    LocalDate.now().month,
                    LocalDate.now().dayOfMonth
                )
                var pickedDate = LocalDate.of(day.year, day.month, day.dayOfMonth)
                pickedDate = pickedDate.plusMonths(1)
                if(commandPath!="1"){
                    val date = pickedDate.toString()
                    val days = activitesViewModel.listOfArmyDays.value
                    var newDay = true
                    if (days != null) {
                        for(day in days){
                            if(day!!.date==date) {
                                newDay = false
                                break
                            }
                        }
                    }
                    if(newDay){
                        Toast.makeText(
                                requireContext(),
                                "אין לך אפשרות להגדיר יום שירות חדש",
                                Toast.LENGTH_LONG
                        ).show()
                        return@SingleDayPickCallback
                    }
                }
                if(pickedDate.isBefore(nowDate)){
                    Toast.makeText(
                        requireContext(),
                        "אין אפשרות לבחור תאריך שעבר",
                        Toast.LENGTH_LONG
                    ).show()
                    return@SingleDayPickCallback
                }
                dateTV.text = pickedDate.toString()
                dateOfActivity = pickedDate.toString()
            }
            val today = CivilCalendar()
            val datePicker = PrimeDatePicker.dialogWith(today)
                    .pickSingleDay(callback)
                    .build()
            datePicker.show(childFragmentManager, "SOME_TAG")
        }

        startTimeButton.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                startTimeTV.text = SimpleDateFormat("HH:mm").format(cal.time)
                activityStartTime = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(
                requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(
                    Calendar.MINUTE
                ), true
            ).show()
        }

        endTimeButton.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                endTimeTV.text = SimpleDateFormat("HH:mm").format(cal.time)
                activityEndTime = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(
                requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(
                    Calendar.MINUTE
                ), true
            ).show()
        }

        addSoldiersButton.setOnClickListener {
            var date = dateTV.text.toString()
            if(date!="הכנס תאריך")
            getSoldiersAttending()
            else
                Toast.makeText(
                    requireContext(),
                    "נא הכנס תאריך לפני הוספת חיילים",
                    Toast.LENGTH_LONG
                ).show()
        }
    }
    private fun alertUserError(n: String, d: String, s: String, e: String){
        if(n.isEmpty()){
            activityNameET.error = "נא למלא את השדא"
        }
        if(d.isEmpty()){
            dateTV.text = "נא למלא את השדא"
        }
        if(s.isEmpty()){
            startTimeTV.text = "נא למלא את השדא"
        }
        if(e.isEmpty()){
            endTimeTV.text = "נא למלא את השדא"
        }
        vibrateAlert()
    }

    private fun vibrateAlert(){
        val v = requireContext().getSystemService(Context.VIBRATOR_SERVICE)as Vibrator
// Vibrate for 500 milliseconds
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v!!.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            //deprecated in API 26
            v!!.vibrate(500)
        }
    }

    private fun getSoldiersById(listOfIds:List<String>):List<Soldier>{
        var soldiers = soldiersViewModel.listOfPersonalSoldiers.value
        var list = mutableListOf<Soldier>()
        for (soldier in soldiers!!){
            if(listOfIds.contains(soldier.idNumber))
                list.add(soldier)
        }
        return list
    }


    private fun getSoldiersAttending(){
        var i = 0
        var allSoldiers : List<Soldier?>? = null
        if(armyDay==null){
            allSoldiers = soldiersViewModel.listOfPersonalSoldiers.value?: listOf()
        }else{
            allSoldiers = getSoldiersById(armyDay!!.listOfSoldiers)
        }
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.a_add_soldiers_for_day, null)
        var dialogDateTV = dialogView.findViewById(R.id.dayTV) as TextView
        val continueButton = dialogView.findViewById(R.id.commitButton) as Button
        var soldiersRV = dialogView.findViewById(R.id.soldiersToAddRV) as ListView
        var signAllCB = dialogView.findViewById(R.id.signEveryOneCB) as CheckBox
        var signNoOneCB =     dialogView.findViewById(R.id.signNoOneCB) as CheckBox
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
        var name = activityNameET.text.toString()
        if(name.isEmpty()) name = "פעילות"
        dialogDateTV.text = "${dateTV.text} - $name"

        signAllCB.isChecked = false
        if(soldiersForActivity.isNotEmpty()){
            AddingSoldierHelper.soldiersToAdd = getIDS(soldiersForActivity).toMutableList()
        }else {
            AddingSoldierHelper.soldiersToAdd = mutableListOf()
        }
        soldiersRV.adapter  = AddSoldierToDayAdapter(
                requireContext(),
                allSoldiers as List<Soldier>,
                false,
                true,
                Util.soldiersToAddToActivityLD.value
        )


        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setView(dialogView).setCancelable(true)

        val dialog = alertDialog.create()
        dialog.show()

        signAllCB.setOnCheckedChangeListener { checkBox, isChecked ->

            if(isChecked) {
                signNoOneCB.isChecked = false
                AddingSoldierHelper.soldiersToAdd = mutableListOf()
                AddingSoldierHelper.soldiersToAdd.addAll(getIDS(allSoldiers).toMutableList())
                soldiersRV.adapter  = AddSoldierToDayAdapter(
                    requireContext(),
                    allSoldiers ,
                    true,
                    true,
                        null
                )
            }
        }
        signNoOneCB.setOnCheckedChangeListener { checkBox, isChecked ->
            if(isChecked) {
                signAllCB.isChecked = false
                AddingSoldierHelper.soldiersToAdd = mutableListOf()
                soldiersRV.adapter  = AddSoldierToDayAdapter(
                    requireContext(),
                    allSoldiers ,
                    false,
                    true,
                        null
                )
            }
        }

        continueButton.setOnClickListener {

            var soldiers = AddingSoldierHelper.soldiersToAdd
            var soldieridNumbers = mutableListOf<String>()

            for(soldier in soldiers)
                soldieridNumbers.add(soldier)

            Util.soldiersToAddToActivityLD.postValue(soldieridNumbers)
            soldiersForActivity = mutableListOf()

            dialog.dismiss()
        }

        cancelButton.visibility = View.GONE
    }


    fun getIDS(list:List<Soldier>):List<String>{
        var ids = mutableListOf<String>()
        for(soldier in list){
            ids.add(soldier.idNumber)
        }
        return ids
    }


    companion object{
        fun newInstance(armyActivity: ArmyActivity?,commandPath:String,listOfSoldiers:kotlin.collections.List<Soldier>?):AddOrEditNewActivityFragment{
            return AddOrEditNewActivityFragment(armyActivity,commandPath,listOfSoldiers)
        }
    }
}