package com.jacoblip.andriod.armycontrol.views.soldiers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.ArmyActivity
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.data.sevices.ActivitiesViewModel
import com.jacoblip.andriod.armycontrol.data.sevices.SoldiersViewModel
import com.jacoblip.andriod.armycontrol.utilities.Util
import java.util.*

class EditSoldierFragment(val soldier: Soldier):Fragment() {

    lateinit var soldiersViewModel: SoldiersViewModel
    lateinit var activitiesViewModel:ActivitiesViewModel
    var oldSoldier = soldier
    var isCommander:MutableLiveData<Boolean> = MutableLiveData(soldier.isCommander)
    var listOfUsages:MutableList<String> = mutableListOf()

    lateinit var saveChangesButton: Button
    lateinit var soldierNameET:EditText
    lateinit var soldierIdNumberET:EditText
    lateinit var soldierPhoneNumberET:EditText
    lateinit var soldierJobET:EditText
    lateinit var soldierAgeET:EditText
    lateinit var soldierMedData:EditText
    lateinit var armyJobSpinner: Spinner
    lateinit var positionSpinner: Spinner
    lateinit var isCommanderSwitch: Switch
    lateinit var hasSoldierArrivedSwitch: Switch
    lateinit var soldierHasArrivedTV:TextView
    lateinit var whyNotArrivingET:EditText
    lateinit var usageTV :TextView
    lateinit var addEntryPermSwitch: Switch
    lateinit var entryPermCode:EditText
    lateinit var entryPermCodeTV:TextView

    lateinit var checkBox1:CheckBox
    lateinit var checkBox2:CheckBox
    lateinit var checkBox3:CheckBox
    lateinit var checkBox4:CheckBox
    lateinit var checkBox5:CheckBox
    lateinit var checkBox6:CheckBox
    lateinit var checkBox7:CheckBox
    lateinit var checkBox8:CheckBox
    lateinit var checkBox9:CheckBox
    lateinit var checkBox10:CheckBox
    lateinit var checkBox11:CheckBox
    lateinit var checkBox12:CheckBox

    lateinit var listOfCheckBoxs:List<CheckBox>

    var listOfDatesOfService :List<Date> = soldier.listOfDatesInService
    var listOfSoldierActivities :List<ArmyActivity> = soldier.Activates


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        soldiersViewModel = ViewModelProvider(requireActivity()).get(SoldiersViewModel::class.java)
        activitiesViewModel = ViewModelProvider(requireActivity()).get(ActivitiesViewModel::class.java)
        soldiersViewModel.currentFragment = this
        val view= inflater.inflate(R.layout.s_fragment_edit_soldier,container,false)
        connectViews(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setClickEvents()

        isCommander.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it){
                val jobList = Util.getListOfCommanderPositions()
                val posList = Util.getListOfArmyPositions()
                val jobAdapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_dropdown_item,jobList)
                val posAdapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_dropdown_item,posList)
                armyJobSpinner.adapter  =jobAdapter
                positionSpinner.adapter = posAdapter
                armyJobSpinner.setSelection(jobList.indexOf(Util.getArmyJobByCode(soldier.armyJobMap)))
                positionSpinner.setSelection(posList.indexOf(Util.getPositionByCode(soldier.positionMap)))
            }else{
                val jobList = arrayOf("????????")
                val posList = Util.getListOfArmyPositions()
                val jobAdapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_dropdown_item, jobList)
                val posAdapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_dropdown_item,posList)
                armyJobSpinner.adapter  =jobAdapter
                positionSpinner.adapter = posAdapter
                positionSpinner.setSelection(posList.indexOf(Util.getPositionByCode(soldier.positionMap)))

            }
        })

    }

    private fun connectViews(view: View){
        view.apply {
            saveChangesButton = findViewById(R.id.saveChangesButton)
            soldierNameET = findViewById(R.id.editTextSoldierName)
            soldierIdNumberET = findViewById(R.id.editTextSoldierIdNumber)
            soldierPhoneNumberET = findViewById(R.id.editTextSoldierPhone)
            soldierJobET = findViewById(R.id.editTextCivilianJob)
            soldierAgeET = findViewById(R.id.editTextSoldierAge)
            soldierMedData = findViewById(R.id.editTextMedData)
            armyJobSpinner = findViewById(R.id.editSoldierSpinnerJob)
            positionSpinner = findViewById(R.id.editSoldierSpinner2)
            isCommanderSwitch = findViewById(R.id.SoldierIsCommanderSwitch)
            hasSoldierArrivedSwitch = findViewById(R.id.hasArrivedSwitch)
            soldierHasArrivedTV = findViewById(R.id.soldierHasArrivedTV)
            whyNotArrivingET = findViewById(R.id.editTextWhyNotArriving)
            addEntryPermSwitch = findViewById(R.id.addPermSwitch)
            entryPermCode = findViewById(R.id.editSoldierPermissionET)
            entryPermCodeTV = findViewById(R.id.editSoldierPermissionTV)
            usageTV= findViewById(R.id.soldierUsageTV)
            checkBox1 = findViewById(R.id.checkBox1)
            checkBox2 = findViewById(R.id.checkBox2)
            checkBox3 = findViewById(R.id.checkBox3)
            checkBox4 = findViewById(R.id.checkBox4)
            checkBox5 = findViewById(R.id.checkBox5)
            checkBox6 = findViewById(R.id.checkBox6)
            checkBox7 = findViewById(R.id.checkBox7)
            checkBox8 = findViewById(R.id.checkBox8)
            checkBox9 = findViewById(R.id.checkBox9)
            checkBox10 = findViewById(R.id.checkBox10)
            checkBox11 = findViewById(R.id.checkBox11)
            checkBox12 = findViewById(R.id.checkBox12)
            listOfCheckBoxs = listOf<CheckBox>(checkBox1,checkBox2,checkBox3,checkBox4,checkBox5,checkBox6,checkBox7,checkBox8,checkBox9,checkBox10,checkBox11,checkBox12)

            listOfUsages = soldier.pakal.toMutableList()
        }

    }

    fun setUpViews(){
        soldierNameET.setText(soldier.name)
        soldierIdNumberET.setText(soldier.idNumber)
        soldierPhoneNumberET.setText(soldier.phoneNumber)
        soldierJobET.setText(soldier.civilianJob)
        soldierAgeET.setText(soldier.age)
        soldierMedData.setText(soldier.medicalProblems)
        whyNotArrivingET.setText(soldier.whyNotArriving)
        usageTV.text = "??????????????:${soldier.pakal}"
        isCommanderSwitch.isChecked = soldier.isCommander
        whyNotArrivingET.setText(soldier.whyNotArriving, TextView.BufferType.EDITABLE);
        hasSoldierArrivedSwitch.isChecked = soldier.hasArrived
        soldierHasArrivedTV.text = if(hasSoldierArrivedSwitch.isChecked)"????" else "????"
        hasSoldierArrivedSwitch.setOnCheckedChangeListener{ b,isChecked->
            if(isChecked){
                soldierHasArrivedTV.text = "????"
            }else{
                soldierHasArrivedTV.text = "????"
            }
        }

        if(Util.userCommandPath=="1"){
            addEntryPermSwitch.isVisible = true
        }

        addEntryPermSwitch.setOnCheckedChangeListener { b, isChecked ->
            if(isChecked){
                entryPermCode.isVisible = true
                entryPermCodeTV.isVisible = true
                entryPermCode.setText(soldier.entryCode)
            }else{
                entryPermCode.isVisible = false
                entryPermCodeTV.isVisible = false
                entryPermCode.setText("")
            }
        }

        addEntryPermSwitch.isChecked = soldier.entryCode!=""

        setUpCheckBoxs()


    }

    private fun setUpCheckBoxs(){
        val armyUsages = Util.armyJobs
        for(i in listOfCheckBoxs.indices){
            listOfCheckBoxs[i].text = armyUsages[i]

            if(listOfUsages.contains(listOfCheckBoxs[i].text))
                listOfCheckBoxs[i].isChecked = true

            listOfCheckBoxs[i].setOnCheckedChangeListener { checkBox, isChecked ->
                updateUsages(checkBox?.text.toString(), isChecked)
            }
        }
    }

    private fun updateUsages(usage:String,add:Boolean){
        if(add){
            listOfUsages.add(usage)
        }else{
            listOfUsages.remove(usage)
        }
        editUsageTV(listOfUsages.toList())
    }

    private fun editUsageTV(usages:List<String>){
        usageTV.text = "??????????????:${usages}"
    }

   private fun setClickEvents(){
        saveChangesButton.setOnClickListener {
            val isValid = checkIfValid()
            if (isValid) {
                val newSoldier = makeNewSoldier()
                soldiersViewModel.saveSoldier(newSoldier,oldSoldier)
                if(newSoldier.idNumber!=oldSoldier.idNumber){
                    activitiesViewModel.updateSoldierIDForDays(oldSoldier.idNumber,newSoldier.idNumber)
                }
                soldiersViewModel.swapOutNewSoldier(newSoldier)
                requireActivity().supportFragmentManager.popBackStack()
            }else{
                return@setOnClickListener
            }
        }

        isCommanderSwitch.setOnCheckedChangeListener{_,isChecked -> isCommander.postValue(isChecked) }

    }

    private fun checkIfValid():Boolean{
        if(soldierIdNumberET.text.isEmpty()){
            soldierIdNumberET.error = "?????? ????????"
            return false
        }
        if(soldierNameET.text.isEmpty()){
            soldierNameET.error = "?????? ????????"
            return false
        }
        if(soldierPhoneNumberET.text.isEmpty()){
            soldierPhoneNumberET.error = "?????? ????????"
            return false
        }
        return true
    }

   private fun makeNewSoldier():Soldier {
        val name = soldierNameET.text.toString()
        val idNum = soldierIdNumberET.text.toString()
        val phone = soldierPhoneNumberET.text.toString()
        var job = soldierJobET.text.toString()
        val age = soldierAgeET.text.toString()
        val medData = soldierMedData.text.toString()
        val isCommander = isCommanderSwitch.isChecked
        val entrycode = entryPermCode.text.toString()
        val jobMap = armyJobSpinner.selectedItem.toString()
        val posMap = positionSpinner.selectedItem.toString()
        val armyJob = Util.getArmyJobPosition(jobMap)
        val soldierPosition = Util.getSoldierArmyPosition(posMap)
        val hasArrived = hasSoldierArrivedSwitch.isChecked
        val whyNotArv = whyNotArrivingET.text.toString()
        val isLieutenant = if (isCommander) {
            armyJob[0] == '-'
        } else false

        if (job.isEmpty()) job = "???? ??????????"


        return Soldier(name, idNum, age, medData, hasArrived, whyNotArv, listOfDatesOfService, phone, job, armyJob, soldierPosition,entrycode, isCommander, isLieutenant, listOfUsages.toList(), listOfSoldierActivities)
    }


    companion object{
        fun newInstance(soldier:Soldier): EditSoldierFragment {
            return EditSoldierFragment(soldier)
        }
    }
}