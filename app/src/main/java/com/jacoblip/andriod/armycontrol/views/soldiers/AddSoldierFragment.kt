package com.jacoblip.andriod.armycontrol.views.soldiers

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.data.sevices.SoldiersViewModel
import com.jacoblip.andriod.armycontrol.utilities.Util

class AddSoldierFragment(var commandPath: String):Fragment() {

    lateinit var soldiersViewModel: SoldiersViewModel
    lateinit var nameInputText: TextInputEditText
    lateinit var idNumberInputText: TextInputEditText
    lateinit var phoneNumberInputText: TextInputEditText
    lateinit var civilianJobInputText: TextInputEditText
    lateinit var positionSpinner: Spinner
    lateinit var armyJobSpinner: Spinner
    lateinit var progressBar: ProgressBar
    lateinit var addSoldierButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_soldier,container,false)
        soldiersViewModel = ViewModelProvider(requireActivity()).get(SoldiersViewModel::class.java)
        soldiersViewModel.currentFragment = this
        view.apply {
            nameInputText = findViewById(R.id.addSoldierName)
            idNumberInputText = findViewById(R.id.addSoldierIdNumber)
            phoneNumberInputText = findViewById(R.id.addSoldierPhone)
            civilianJobInputText = findViewById(R.id.addSoldierCivilianJob)
            positionSpinner = findViewById(R.id.spinnerPosition)
            armyJobSpinner = findViewById(R.id.spinnerArmyJob)
            progressBar = findViewById(R.id.addSoldierProgressBar)
            addSoldierButton = findViewById(R.id.addSoldierButton)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapters()
        addSoldierButton.setOnClickListener {
            addSoldier()
        }
    }

    fun setUpAdapters(){
        val armyCommanderPositions = Util.getListOfCommanderPositions(commandPath)
        val armySoldierPositions = Util.getListOfArmyPositions(commandPath)
        var positionSpinnerAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item,armySoldierPositions)
        var armyJobSpinnerAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item,armyCommanderPositions)
        positionSpinner.adapter = positionSpinnerAdapter
        armyJobSpinner.adapter = armyJobSpinnerAdapter
    }

    fun addSoldier(){
        var inputCorrect = true
        var name = nameInputText.text.toString().trim()
        var idNumber = idNumberInputText.text.toString().trim()
        var phone = phoneNumberInputText.text.toString().trim()
        var job = civilianJobInputText.text.toString().trim()
        var position = positionSpinner.selectedItem.toString()
        var armyJob = armyJobSpinner.selectedItem.toString()

        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(idNumber)||TextUtils.isEmpty(phone)){
            inputCorrect = false
            if (TextUtils.isEmpty(name)){
                nameInputText.error = "נא למלא את השדא"
            }
            if (TextUtils.isEmpty(idNumber)){
                idNumberInputText.error = "נא למלא את השדא"
            }
            if (TextUtils.isEmpty(phone)){
                phoneNumberInputText.error = "נא למלא את השדא"
            }
        }

        if(inputCorrect){
            var armyJob = Util.getArmyJobPosition(armyJob)
            var soldierPosition = Util.getSoldierArmyPosition(position)
            var isCommander = (armyJob!="")
            var isLieutenant = if(isCommander){
                armyJob[0]=='-'
            }else false

            var soldier = Soldier(name,idNumber,"","", false,"",
                    listOf(),phone,job,armyJob,soldierPosition, isCommander,isLieutenant,listOf(),listOf() )

            soldiersViewModel.addSoldier(soldier)
            requireActivity().supportFragmentManager.popBackStack()
        }

    }


    companion object{
        fun newInstance(commandPath:String): AddSoldierFragment {
            return AddSoldierFragment(commandPath)
        }
    }
}