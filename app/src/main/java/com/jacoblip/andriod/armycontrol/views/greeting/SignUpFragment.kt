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
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.jacoblip.andriod.armycontrol.MainActivity
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.EntryRequest


@RequiresApi(Build.VERSION_CODES.O)
class SignUpFragment(context: Context,var preferences: SharedPreferences,val firstLogIn:Boolean):Fragment() {

    lateinit var userIDnumber: TextInputEditText
    lateinit var userPassword: TextInputEditText
    lateinit var userGroupID: TextInputEditText
    lateinit var submitButton: Button
    lateinit var progressBar: ProgressBar
    var password:String = ""
    var commandPath:String = ""
    lateinit var firebaseAuth: FirebaseAuth



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
            userIDnumber = findViewById(R.id.userIDnumber)
            userPassword = findViewById(R.id.userPassword)
            progressBar = findViewById(R.id.progressBar)
            userGroupID = findViewById(R.id.userGroup)

            submitButton.setOnClickListener {
                submitButton.isEnabled = false
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
                submitButton.isEnabled = true
            }
        }

        if(error!=1){
            view?.let {
                var entryRequest = EntryRequest(it,userID,password,groupID)
                getGroup(entryRequest)
            }
        }
    }



    fun signUp(){

        val groupID = userGroupID.text.toString().trim()
        val userID = userIDnumber.text.toString().trim()
        val password = userPassword.text.toString().trim()
        var error = 0



            if (TextUtils.isEmpty(userID) || TextUtils.isEmpty(password)  || TextUtils.isEmpty(groupID)
            ) {
                error = 1
            }


        when (error) {
            1 -> {
                if(TextUtils.isEmpty(userID) )
                   userIDnumber.error = "נא למלא את השדא"
                if(TextUtils.isEmpty(password))
                    userPassword.error = "נא למלא את השדא"
                if(TextUtils.isEmpty(groupID) )
                    userGroupID.error = "נא למלא את השדא"
                submitButton.isEnabled = true
                progressBar.visibility = View.GONE
            }
        }

        if(error!=1){
            view?.let {
                var entryRequest = EntryRequest(it,userID,password,groupID)
                getGroup(entryRequest)
            }
        }
    }

    fun getGroup(entryRequest: EntryRequest){
        var fbRef = FirebaseDatabase.getInstance().getReference("groups").child(entryRequest.groupId).child("initString")
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var data = dataSnapshot!!.value
                if(data!=null){
                    var dataArr = data.toString().split(' ')
                    val group = dataArr[1]+" "+dataArr[2]
                    preferences.edit().putString("ArmyControlGroup",group).apply()
                    getPassword(entryRequest)
                }else{
                    userGroupID.error = "אין מסגרת התואמת את הקוד"
                    progressBar.visibility = View.GONE
                    submitButton.isEnabled = true
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        fbRef.addListenerForSingleValueEvent(menuListener)
    }



    fun getPassword(entryRequest: EntryRequest){
        var name = preferences.getString("groupName","")
        var ref = FirebaseDatabase.getInstance().getReference("groups").child(entryRequest.groupId).child(name!!).child("entry codes")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var data = dataSnapshot.value.toString()
                data = data.replace('=',' ')
                var passwordData1 = data.filter { it.isLetterOrDigit() || it ==' '}
                var passwordData =passwordData1.split(' ')

                try {
                    password = passwordData[passwordData.indexOf(entryRequest.userId)+1]
                    commandPath = passwordData[passwordData.indexOf(entryRequest.userId) + 2]
                    if (password == entryRequest.userCode) {
                        if (commandPath != "1") {
                            var path = commandPath
                            if (path.length == 2) {
                                var str = "${path[0]}.${path[1]}"
                                commandPath = str
                            }
                            if (path.length == 3) {
                                var str = "${path[0]}.${path[1]}.${path[2]}"
                                commandPath = str
                            }
                        }
                        preferences.edit().putString("ArmyControlLoggedIn", commandPath).apply()
                        starArmyActivity(commandPath,entryRequest.groupId)
                    } else {
                        userPassword.error = "סיסמת מפקד שגויה"
                        progressBar.visibility = View.GONE
                        submitButton.isEnabled = true
                    }
                }catch (e :ArrayIndexOutOfBoundsException){
                    userIDnumber.error = "נתונים לא תקינים"
                    userPassword.error = "נתונים לא תקינים"
                    progressBar.visibility = View.GONE
                    submitButton.isEnabled = true
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        })

    }

    fun starArmyActivity(commandPath:String,groupCode:String){
        preferences.edit().putString("valid",groupCode).apply()
        var intent = Intent(requireContext(), MainActivity::class.java)
        intent.putExtra("commandPath",commandPath)
        startActivity(intent)
        activity?.finish()
    }

    companion object{
        fun newInstance(context: Context,preferences: SharedPreferences,firstSignIn:Boolean): SignUpFragment {
            return SignUpFragment(context,preferences,firstSignIn)
        }
    }
}
