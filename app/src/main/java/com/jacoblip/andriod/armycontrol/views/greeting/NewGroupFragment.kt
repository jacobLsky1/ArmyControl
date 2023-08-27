package com.jacoblip.andriod.armycontrol.views.greeting

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.utilities.ArmyData
import com.jacoblip.andriod.armycontrol.utilities.Util
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class NewGroupFragment(var preferences: SharedPreferences):Fragment() {

    lateinit var codeET: TextInputEditText
    lateinit var codeETLayout: TextInputLayout
    lateinit var mailCodeET: TextInputEditText
    lateinit var mailCodeETLayout: TextInputLayout
    lateinit var userIDnumberET: TextInputEditText
    lateinit var idNumberETLayout: TextInputLayout
    lateinit var initButton: Button
    lateinit var showGroupPassword: TextView
    lateinit var progressBar: ProgressBar
    lateinit var instructionText: TextView
    lateinit var groupSpinner: Spinner
    lateinit var firebaseAuth: FirebaseAuth

    var groupinit = false
    var soldoiersinit = false
    var codesinit = false
    var listOfEmails : List<String?>? = null
    var groupNotInit:MutableLiveData<Boolean> = MutableLiveData(preferences.getString("groupNotInit","") == "")
    private val emailsRef = FirebaseDatabase.getInstance().getReference("emailsCodeRequest")
    var email = ""
    var codeStringForGroup = ""

    interface Callbacks {
        fun onDataReady()
    }
    interface CodeCallback {
        fun onCodeReady(num:String)
    }

    interface SoldierCodeCallback {
        fun onSoldierCodeReady(num:String)
    }

    private var callbacks: Callbacks? = null
    private var codeCallbacks: CodeCallback? = null
    private var soldierCodeCallbacks: SoldierCodeCallback? = null

    //the callback functions
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
        codeCallbacks = context as CodeCallback?
        soldierCodeCallbacks = context as SoldierCodeCallback?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
        codeCallbacks = null
        soldierCodeCallbacks = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.m_fragment_new_group_init,container,false)
        view.apply {
            groupSpinner = findViewById(R.id.groupSpinner)
            codeETLayout = findViewById(R.id.newGroupETLayout)
            mailCodeETLayout = findViewById(R.id.mailCodeETLayout)
            mailCodeET = findViewById(R.id.mailCodeEditText)
            idNumberETLayout = findViewById(R.id.idNumberETLayout)
            userIDnumberET = findViewById(R.id.userIDnumberET)
            codeET = findViewById(R.id.initGroupEditText)
            initButton = findViewById(R.id.initNewGroupButton)
            progressBar = findViewById(R.id.progressBar2)
            instructionText = findViewById(R.id.instructionText)
            showGroupPassword = findViewById(R.id.forgotGroupCode)
            firebaseAuth= FirebaseAuth.getInstance()

            showGroupPassword.setOnClickListener {
                val code = preferences.getString("valid","")
                Toast.makeText(requireContext(), code, Toast.LENGTH_SHORT).show()
            }
            val groupAdapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_dropdown_item,
                arrayListOf(
                "פלוגה א","פלוגה ב","פלוגה ג","פלוגה ד"))
            groupSpinner.adapter = groupAdapter

            val menuListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val t: GenericTypeIndicator<List<String?>?> = object : GenericTypeIndicator<List<String?>?>() {}
                    listOfEmails = dataSnapshot.getValue(t)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }
            emailsRef.addListenerForSingleValueEvent(menuListener)
        }
        groupNotInit.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it){
                mailCodeET.isVisible = true
                mailCodeETLayout.isVisible = true
                idNumberETLayout.isVisible = false
                userIDnumberET.isVisible = false
                codeETLayout.isVisible = false
                codeET.isVisible = false
                initButton.text = "קבל קוד"
                instructionText.text = "יש לבחור שם לפלוגה"
            }else{
                mailCodeET.isVisible = false
                mailCodeETLayout.isVisible = false
                groupSpinner.isVisible = false
                codeETLayout.isVisible = true
                codeET.isVisible = true
                idNumberETLayout.isVisible = true
                userIDnumberET.isVisible = true
                initButton.text = "צור פלוגה"
                instructionText.text = "יש להכניס את סיסמת ההפעלה של הפלוגה"
            }
        })

        initButton.setOnClickListener {
            if(groupNotInit.value!!) {
                CheckMail()
            }else{
                initGroup()
            }

        }

        return view
    }

    private fun CheckMail(){
        progressBar.isVisible = true
        var userEmail = mailCodeET.text.toString()
        var currentUser = firebaseAuth.currentUser?.email
        var provider = userEmail.subSequence(userEmail.indexOf('@'),userEmail.length)
        var mailName = userEmail.subSequence(0,userEmail.indexOf('@'))
        userEmail = "$mailName$provider"
        if(userEmail != currentUser){
            progressBar.isVisible = false
            Toast.makeText(requireContext(), "יש לאמת כתובת מייל", Toast.LENGTH_LONG).show()
        }else{
            if(listOfEmails!!.contains(userEmail)){
                progressBar.isVisible = false
                Toast.makeText(requireContext(), "מייל קיים במערכת, יש לפנות למפתח לקבלת קוד נוסף", Toast.LENGTH_LONG).show()
            }else{
                email = userEmail
                val emails = listOfEmails!!.toMutableList()
                emails.add(userEmail)
                emailsRef.setValue(emails).addOnFailureListener {
                    Log.i("fail","fail")
                }
                sendMail()
            }
        }
    }

    private fun sendMail(){
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val currentDateandTime: String = sdf.format(Date())
        var code = Random.nextInt(100000,999999)
        var bool = checkCode(code)
        while (bool){
            code = Random.nextInt(100000,999999)
            bool = checkCode(code)
        }
        val codeString = "$code ${groupSpinner.selectedItem} $currentDateandTime $email"
        codeStringForGroup = codeString
        val codeRef = FirebaseDatabase.getInstance().getReference("groupCodes").child(code.toString())
        codeRef.setValue(codeString).addOnFailureListener {
            Log.i("fail","fail")
        }
        preferences.edit().putString("valid",code.toString()).apply()
        preferences.edit().putString("groupName",groupSpinner.selectedItem.toString()).apply()
        codeCallbacks?.onCodeReady(code.toString())
        groupNotInit.postValue(false)
        progressBar.isVisible = false
    }

    private fun checkCode(code:Int):Boolean{
        var exists = false
        val postRef = FirebaseDatabase.getInstance().getReference("groups").child(code.toString());

        postRef.addListenerForSingleValueEvent(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                exists = snapshot.exists()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.i("fail","fail")
            }
        })
        return exists
    }

    private fun initGroup(){
        progressBar.isVisible = true
        val code = codeET.text.toString().trim()
        val id = userIDnumberET.text.toString().trim()
        if(code.isEmpty()){
            codeET.error = "נא למלא את השדא"
            progressBar.isVisible = false
            return
        }
        if(id.isEmpty()){
            userIDnumberET.error = "נא למלא את השדא"
            progressBar.isVisible = false
            return
        }

        val dbRef = FirebaseDatabase.getInstance().getReference("groupCodes").child(code)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var data = dataSnapshot!!.value
                if(data!=null) {
                    var reverse = id.reversed()
                    ArmyData.initcode = "$id $reverse 1"
                    dbRef.setValue(null)
                    var dataArr = data.toString().split(' ')
                    var group = dataArr[1] + " " + dataArr[2]
                    var dataForGroup = ArmyData.armyData0
                    var dataCodesForGroup = ArmyData.initcode
                    var listOfSoldiersForGroup = ArmyData.listOfSoldiers

                    when (group) {
                        "פלוגה א" -> {
                            dataForGroup = ArmyData.armyData0
                            dataCodesForGroup = ArmyData.initcode
                        }
                        "פלוגה ב" -> {
                            dataForGroup = ArmyData.armyData1
                            dataCodesForGroup = ArmyData.initcode
                        }
                        "פלוגה ג" -> {
                            dataForGroup = ArmyData.armyData2
                            dataCodesForGroup = ArmyData.initcode
                        }
                        "פלוגה ד" -> {
                            dataForGroup = ArmyData.armyData3
                            dataCodesForGroup = ArmyData.initcode
                        }
                    }
                    val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                    val currentDateandTime: String = sdf.format(Date())
                    dataForGroup.createdAt = currentDateandTime
                    dataForGroup.createdBy = email
                    val groupRef = FirebaseDatabase.getInstance().getReference("groups").child(code).child(group)
                    val codeGroupInitStringRef = FirebaseDatabase.getInstance().getReference("groups").child(code).child("initString")
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

                    codeGroupInitStringRef.setValue(codeStringForGroup)

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
                    soldierCodeCallbacks!!.onSoldierCodeReady(reverse.toString())
                }else{
                    codeET.error = "קוד פלוגתי שגוי"
                    progressBar.visibility = View.GONE
                }

            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.i("fail","fail")
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
        fun newInstance(preferences: SharedPreferences): NewGroupFragment {
            return NewGroupFragment(preferences)
        }
    }
}