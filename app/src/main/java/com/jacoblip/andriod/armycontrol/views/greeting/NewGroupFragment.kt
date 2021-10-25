package com.jacoblip.andriod.armycontrol.views.greeting

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.utilities.ArmyData
import com.jacoblip.andriod.armycontrol.utilities.Util

class NewGroupFragment:Fragment() {

    lateinit var codeET: TextInputEditText
    lateinit var initButton: Button
    lateinit var progressBar: ProgressBar

    var groupinit = false
    var soldoiersinit = false
    var codesinit = false

    interface Callbacks {
        fun onDataReady()
    }

    private var callbacks: Callbacks? = null

    //the callback functions
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.m_fragment_new_group_init,container,false)
        view.apply {
            codeET = findViewById(R.id.initGroupEditText)
            initButton = findViewById(R.id.initNewGroupButton)
            progressBar = findViewById(R.id.progressBar2)
        }

        initButton.setOnClickListener {
            initGroup()
            progressBar.isVisible = true
        }

        return view
    }

    private fun initGroup(){
        val code = codeET.text.toString().trim()
        if(code.isEmpty()){
            codeET.error = "נא למלא את השדא"
            progressBar.isVisible = false
            return
        }
        val dbRef = FirebaseDatabase.getInstance().getReference(code)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var data = dataSnapshot!!.value
                if(data!=null) {
                    dbRef.setValue(null)
                    val groupsCodesRef = FirebaseDatabase.getInstance().getReference("groupCodes").child(code)
                    groupsCodesRef.setValue(data.toString())
                    var dataArr = data.toString().split(' ')
                    var group = dataArr[1] + " " + dataArr[2]
                    var dataForGroup = ArmyData.armyData0
                    var dataCodesForGroup = ArmyData.code0
                    var listOfSoldiersForGroup = ArmyData.listOfSoldiers

                    when (group) {
                        "פלוגה א" -> {
                            dataForGroup = ArmyData.armyData0
                            dataCodesForGroup = ArmyData.code0
                        }
                        "פלוגה ב" -> {
                            dataForGroup = ArmyData.armyData1
                            dataCodesForGroup = ArmyData.code1
                        }
                        "פלוגה ג" -> {
                            dataForGroup = ArmyData.armyData2
                            dataCodesForGroup = ArmyData.code2
                        }
                        "פלוגה ד" -> {
                            dataForGroup = ArmyData.armyData3
                            dataCodesForGroup = ArmyData.code3
                        }
                    }
                    val groupRef = FirebaseDatabase.getInstance().getReference(group)
                    Util.groupRef = groupRef
                    var codeArr = dataCodesForGroup.split(' ')
                    val key = codeArr[0]
                    val value = codeArr[1]+" "+codeArr[2]

                    groupRef.child("build").setValue(dataForGroup).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            groupinit = true
                            checkDataReady()
                            Log.i("init codes", "success")
                        } else {
                            view?.let { showMessage(it, "Error: ${task.exception?.message}") }
                            progressBar.visibility = View.GONE
                        }
                    }

                    groupRef.child("soldiers").setValue(listOfSoldiersForGroup).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            soldoiersinit = true
                            checkDataReady()
                            Log.i("init codes", "success")
                        } else {
                            view?.let { showMessage(it, "Error: ${task.exception?.message}") }
                            progressBar.visibility = View.GONE
                        }
                    }

                    groupRef.child("entry codes").child(key).setValue(value).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            codesinit = true
                            checkDataReady()
                            Log.i("init codes", "success")
                        } else {
                            view?.let { showMessage(it, "Error: ${task.exception?.message}") }
                            progressBar.visibility = View.GONE
                        }
                    }
                }else{
                    codeET.error = "קוד פלוגתי שגוי"
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        dbRef.addListenerForSingleValueEvent(menuListener)

    }

    fun checkDataReady(){
        if(codesinit&&groupinit&&soldoiersinit){
            callbacks!!.onDataReady()
        }
    }

    fun showMessage(view:View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

    companion object{
        fun newInstance(): NewGroupFragment {
            return NewGroupFragment()
        }
    }
}