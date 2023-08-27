package com.jacoblip.andriod.armycontrol.views.soldiers

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
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

    lateinit var gunSwitch:Switch
    lateinit var biSwitch:Switch
    lateinit var nvSwitch:Switch
    lateinit var radioSwitch:Switch

    lateinit var gunEditText: EditText
    lateinit var biEditText: EditText
    lateinit var nvEditText: EditText
    lateinit var radioEditText: EditText

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

    @RequiresApi(Build.VERSION_CODES.O)
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
                val jobList = arrayOf("חייל")
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

            gunSwitch = findViewById(R.id.hasGunSwitch)
            biSwitch = findViewById(R.id.hasBiSwitch)
            nvSwitch = findViewById(R.id.hasNVSwitch)
            radioSwitch = findViewById(R.id.hasRadioSwitch)
            gunEditText = findViewById(R.id.editTextTextGunNumber)
            biEditText = findViewById(R.id.editTextTextBiNumber)
            nvEditText = findViewById(R.id.editTextTextNVNumber)
            radioEditText = findViewById(R.id.editTextTextRadioNumber)


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
        usageTV.text = "תפקידים:${soldier.pakal}"
        isCommanderSwitch.isChecked = soldier.isCommander
        whyNotArrivingET.setText(soldier.whyNotArriving, TextView.BufferType.EDITABLE);

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

        gunSwitch.setOnCheckedChangeListener{  b, isChecked ->
            gunEditText.isVisible = isChecked
        }
        biSwitch.setOnCheckedChangeListener{  b, isChecked ->
            biEditText.isVisible = isChecked
        }
        nvSwitch.setOnCheckedChangeListener{  b, isChecked ->
            nvEditText.isVisible = isChecked
        }
        radioSwitch.setOnCheckedChangeListener{  b, isChecked ->
            radioEditText.isVisible = isChecked
        }
        if(soldier.hasGun){
            gunSwitch.isChecked = true
            gunEditText.setText(soldier.numGun)
        }else{
            gunSwitch.isChecked = false
        }
        if(soldier.hasBi){
            biSwitch.isChecked = true
            biEditText.setText(soldier.numBi)
        }else{
            biSwitch.isChecked = false
        }
        if(soldier.hasNV){
            nvSwitch.isChecked = true
            nvEditText.setText(soldier.numNV)
        }else{
            nvSwitch.isChecked = false
        }
        if(soldier.hasRadio){
            radioSwitch.isChecked = true
            radioEditText.setText(soldier.numRadio)
        }else{
            radioSwitch.isChecked = false
        }

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
        usageTV.text = "תפקידים:${usages}"
    }

   @RequiresApi(Build.VERSION_CODES.O)
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
            soldierIdNumberET.error = "שדה חובה"
            return false
        }
        if(soldierNameET.text.isEmpty()){
            soldierNameET.error = "שדה חובה"
            return false
        }
        if(soldierPhoneNumberET.text.isEmpty()){
            soldierPhoneNumberET.error = "שדה חובה"
            return false
        }
        if(gunSwitch.isChecked && gunEditText.text.isEmpty()){
            gunEditText.error = "שדה חובה אם חתום נשק"
            return false
        }
        if(biSwitch.isChecked && biEditText.text.isEmpty()){
            biEditText.error = "שדה חובה אם חתום משקפת"
            return false
        }
        if(nvSwitch.isChecked && nvEditText.text.isEmpty()){
            nvEditText.error = "שדה חובה אם חתום אמרל"
            return false
        }
        if(radioSwitch.isChecked && radioEditText.text.isEmpty()){
            radioEditText.error ="שדה חובה אם חתום קשר"
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
        val hasArrived = soldier.hasArrived
        val whyNotArv = whyNotArrivingET.text.toString()
        val hasGun = gunSwitch.isChecked
        val hasBi = biSwitch.isChecked
        val hasNV = nvSwitch.isChecked
        val hasRadio = radioSwitch.isChecked
       val gunNum = if(gunSwitch.isChecked)gunEditText.text.toString() else ""
       val biNum = if(biSwitch.isChecked)biEditText.text.toString() else ""
       val nvNum = if(nvSwitch.isChecked)nvEditText.text.toString() else ""
       val radioNum = if(radioSwitch.isChecked)radioEditText.text.toString() else ""
        val isLieutenant = if (isCommander) {
            armyJob[0] == '-'
        } else false

        if (job.isEmpty()) job = "לא עודכן"


        return Soldier(name, idNum, age, medData, hasArrived, whyNotArv, listOfDatesOfService, phone, job, armyJob, soldierPosition,entrycode, isCommander, isLieutenant, listOfUsages.toList(), listOfSoldierActivities,hasGun,hasBi,hasNV,hasRadio,gunNum,biNum,nvNum,radioNum)
    }


    companion object{
        fun newInstance(soldier:Soldier): EditSoldierFragment {
            return EditSoldierFragment(soldier)
        }
    }
}