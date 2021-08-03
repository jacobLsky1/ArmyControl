package com.jacoblip.andriod.armycontrol.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jacoblip.andriod.armycontrol.MainActivity
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.utilities.ArmyData
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class SignUpFragment(context: Context,var preferences: SharedPreferences,val firstLogIn:Boolean):Fragment() {

    lateinit var userEmail: TextInputEditText
    lateinit var userEmailPassword: TextInputEditText
    lateinit var userIDnumber: TextInputEditText
    lateinit var userPassword: TextInputEditText
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
        val view = inflater.inflate(R.layout.fragment_sign_up,container,false)
        view.apply {
            submitButton = findViewById(R.id.submitButton)
            userEmail = findViewById(R.id.userEmail)
            userEmailPassword = findViewById(R.id.userEmailPassword)
            userIDnumber = findViewById(R.id.userIDnumber)
            userPassword = findViewById(R.id.userPassword)
            progressBar = findViewById(R.id.progressBar)

            if(!firstLogIn){
                userEmail.visibility = View.GONE
                userEmailPassword.visibility = View.GONE
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
        val userEmail = ""
        val userEmailPassword = ""
        var error = 0
        if (TextUtils.isEmpty(userID) || TextUtils.isEmpty(password)) {
            error = 1
        }

        when (error) {
            1 -> {
                if(TextUtils.isEmpty(userID) )
                    userIDnumber.error = "must not be empty"
                if(TextUtils.isEmpty(password))
                    userPassword.error = "must not be empty"
                progressBar.visibility = View.GONE
            }
        }

        if(error!=1){
            view?.let {getPassword(it,userEmail,userEmailPassword,userID,password) }
        }
    }

    fun signUp(){

        val userEmail = userEmail.text.toString().trim()
        val userEmailPassword = userEmailPassword.text.toString().trim()
        val userID = userIDnumber.text.toString().trim()
        val password = userPassword.text.toString().trim()
        var error = 0



        if (TextUtils.isEmpty(userID) || TextUtils.isEmpty(password)|| TextUtils.isEmpty(userEmail)|| TextUtils.isEmpty(userEmailPassword)) {
            error = 1
        }

        when (error) {
            1 -> {
                if(TextUtils.isEmpty(userID) )
                   userIDnumber.error = "must not be empty"
                if(TextUtils.isEmpty(password))
                    userPassword.error = "must not be empty"
                if(TextUtils.isEmpty(userEmail) )
                    userIDnumber.error = "must not be empty"
                if(TextUtils.isEmpty(userEmailPassword))
                    userPassword.error = "must not be empty"

                progressBar.visibility = View.GONE
            }
        }

        if(error!=1){
            view?.let { getPassword(it,userEmail,userEmailPassword,userID,password) }
        }
    }

    fun getPassword(view: View,userEmail:String,userEmailPassword:String, userID:String, passwordInput: String){
        var ref = FirebaseDatabase.getInstance().getReference("KeyPasswords").child(userID)

        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var data = dataSnapshot!!.value
                var passwordData = data.toString().split(' ')
                password = passwordData[0]
                commandPath = passwordData[1]
                if(password==passwordInput) {
                    if(firstLogIn){
                        signUpFireBase(view,userEmail,userEmailPassword,userID,password)
                    }else{
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


    fun signUpFireBase(view: View,userEmail:String,userEmailPassword:String, userID:String, passwordInput: String){


            showMessage(view, "Authenticating...")

            firebaseAuth.createUserWithEmailAndPassword(userEmail, userEmailPassword).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    preferences.edit().putString("ArmyControlVerified", "verified").apply()
                    Log.i("Firebase", firebaseAuth.currentUser.uid)
                    val userID = firebaseAuth.currentUser.uid
                    initData()
                    starArmyActivity(commandPath)

                } else {
                    showMessage(view, "Error: ${task.exception?.message}")
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



    fun initData(){
        val soldierDbRef = FirebaseDatabase.getInstance().getReference("חיילים")
        soldierDbRef.setValue(ArmyData.listOfSoldiers)


        val dbRef = FirebaseDatabase.getInstance().getReference("פלוגה")
        dbRef.child("ב").setValue(ArmyData.armyData)
                        .addOnCompleteListener {task ->
                            if(task.isSuccessful) {
                              //  Toast.makeText(requireContext(), "new User formed", Toast.LENGTH_LONG).show()
                                Log.i("signup","success")
                            }else{
                                view?.let { showMessage(it,"Error: ${task.exception?.message}") }
                                progressBar.visibility = View.GONE
                            }
                        }

    }

    fun showMessage(view:View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show()
    }

    companion object{
        fun newInstance(context: Context,preferences: SharedPreferences,firstSignIn:Boolean):SignUpFragment{
            return SignUpFragment(context,preferences,firstSignIn)
        }
    }
}
