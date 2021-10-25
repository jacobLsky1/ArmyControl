package com.jacoblip.andriod.armycontrol.views.greeting

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jacoblip.andriod.armycontrol.MainActivity
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.EntryRequest
import com.jacoblip.andriod.armycontrol.utilities.ArmyData
import com.jacoblip.andriod.armycontrol.utilities.Util

@RequiresApi(Build.VERSION_CODES.O)
class SignUpFragment(context: Context,var preferences: SharedPreferences,val firstLogIn:Boolean):Fragment() {

    lateinit var userEmailLayout: TextInputLayout
    lateinit var userEmailPasswordLayout: TextInputLayout
    lateinit var userEmail: TextInputEditText
    lateinit var userEmailPassword: TextInputEditText
    lateinit var userIDnumber: TextInputEditText
    lateinit var userPassword: TextInputEditText
    lateinit var userGroupID: TextInputEditText
    lateinit var submitButton: Button
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var progressBar: ProgressBar
    var password:String = ""
    var commandPath:String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.m_fragment_sign_up,container,false)
        view.apply {
            submitButton = findViewById(R.id.submitButton)
            userEmail = findViewById(R.id.userEmail)
            userEmailPassword = findViewById(R.id.userEmailPassword)
            userIDnumber = findViewById(R.id.userIDnumber)
            userPassword = findViewById(R.id.userPassword)
            progressBar = findViewById(R.id.progressBar)
            userEmailLayout = findViewById(R.id.userEmailLayout)
            userEmailPasswordLayout = findViewById(R.id.userEmailPasswordLayout)
            userGroupID = findViewById(R.id.userGroup)

            if(!firstLogIn){
                userEmailLayout.visibility = View.GONE
                userEmailPasswordLayout.visibility = View.GONE
            }

            firebaseAuth = FirebaseAuth.getInstance()

            submitButton.setOnClickListener {
                progressBar.visibility = View.VISIBLE
                if(firstLogIn) {
                    signUp()
                }
                else{
                    logIn()
                }
            }
        }
        return view
    }

    fun logIn(){
        val userID = userIDnumber.text.toString().trim()
        val password = userPassword.text.toString().trim()
        val  groupID = userGroupID.text.toString().trim()
        val userEmail = ""
        val userEmailPassword = ""
        var error = 0
        if (TextUtils.isEmpty(userID) || TextUtils.isEmpty(password)||TextUtils.isEmpty(groupID)) {
            error = 1
        }

        when (error) {
            1 -> {
                if(TextUtils.isEmpty(groupID) )
                    userGroupID.error = "נא למלא את השדא"
                if(TextUtils.isEmpty(userID) )
                    userIDnumber.error = "נא למלא את השדא"
                if(TextUtils.isEmpty(password))
                    userPassword.error = "נא למלא את השדא"
                progressBar.visibility = View.GONE
            }
        }

        if(error!=1){
            view?.let {
                var entryRequest = EntryRequest(it,userEmail,userEmailPassword,userID,password,groupID)
                getGroup(entryRequest)
            }
        }
    }



    fun signUp(){

        val groupID = userGroupID.text.toString().trim()
        val userEmail = userEmail.text.toString().trim()
        val userEmailPassword = userEmailPassword.text.toString().trim()
        val userID = userIDnumber.text.toString().trim()
        val password = userPassword.text.toString().trim()
        var error = 0



        if (TextUtils.isEmpty(userID) || TextUtils.isEmpty(password)|| TextUtils.isEmpty(userEmail)|| TextUtils.isEmpty(userEmailPassword)||TextUtils.isEmpty(groupID)) {
            error = 1
        }

        when (error) {
            1 -> {
                if(TextUtils.isEmpty(userID) )
                   userIDnumber.error = "נא למלא את השדא"
                if(TextUtils.isEmpty(password))
                    userPassword.error = "נא למלא את השדא"
                if(TextUtils.isEmpty(userEmail) )
                    userIDnumber.error = "נא למלא את השדא"
                if(TextUtils.isEmpty(userEmailPassword))
                    userPassword.error = "נא למלא את השדא"
                if(TextUtils.isEmpty(groupID) )
                    userGroupID.error = "נא למלא את השדא"

                progressBar.visibility = View.GONE
            }
        }

        if(error!=1){
            view?.let {
                var entryRequest = EntryRequest(it,userEmail,userEmailPassword,userID,password,groupID)
                getGroup(entryRequest)
            }
        }
    }

    fun getGroup(entryRequest: EntryRequest){
        var fbRef = FirebaseDatabase.getInstance().getReference("groupCodes").child(entryRequest.groupId)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var data = dataSnapshot!!.value
                if(data!=null){
                    var dataArr = data.toString().split(' ')
                    val group = dataArr[1]+" "+dataArr[2]
                    preferences.edit().putString("ArmyControlGroup",group).apply()
                    val groupRef = FirebaseDatabase.getInstance().getReference(group)
                    getPassword(entryRequest,groupRef)
                }else{
                    userGroupID.error = "אין מסגרת התואמת את הקוד"
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        fbRef.addListenerForSingleValueEvent(menuListener)
    }



    fun getPassword(entryRequest: EntryRequest,groupRef:DatabaseReference){
        var ref = groupRef.child("entry codes").child(entryRequest.userId)

        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var data = dataSnapshot!!.value
                var passwordData = data.toString().split(' ')
                password = passwordData[0]
                commandPath = passwordData[1]
                if(password==entryRequest.userCode) {
                    if(firstLogIn){
                        signUpFireBase(entryRequest)
                    }else{
                        preferences.edit().putString("ArmyControlLoggedIn", commandPath).apply()
                        starArmyActivity(commandPath)
                    }
                }else{
                  userPassword.error = "סיסמת מפקד שגויה"
                    progressBar.visibility = View.GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        ref.addListenerForSingleValueEvent(menuListener)
    }


    fun signUpFireBase(entryRequest: EntryRequest){


            showMessage(entryRequest.view!!, "Authenticating...")

            firebaseAuth.createUserWithEmailAndPassword(entryRequest.email, entryRequest.emailPassword).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    preferences.edit().putString("ArmyControlVerified", "verified").apply()
                    preferences.edit().putString("ArmyControlLoggedIn", commandPath).apply()
                    Log.i("Firebase", firebaseAuth.currentUser.uid)
                    starArmyActivity(commandPath)

                } else {
                    showMessage(entryRequest.view!!, "Error: ${task.exception?.message}")
                    progressBar.visibility = View.GONE
                }
            }


    }

    fun starArmyActivity(commandPath:String){
        var intent = Intent(requireContext(), MainActivity::class.java)
        intent.putExtra("commandPath",commandPath)

        startActivity(intent)
        activity?.finish()
    }


    fun showMessage(view:View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

    companion object{
        fun newInstance(context: Context,preferences: SharedPreferences,firstSignIn:Boolean): SignUpFragment {
            return SignUpFragment(context,preferences,firstSignIn)
        }
    }
}
