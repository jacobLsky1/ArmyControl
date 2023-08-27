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
import com.jacoblip.andriod.armycontrol.data.models.Soldier


const val REQUEST_CODE_SIGN_IN = 94763
@RequiresApi(Build.VERSION_CODES.O)
class AuthFragment(context: Context,var preferences: SharedPreferences):Fragment() {

    private lateinit var authUserEmailLayout: TextInputLayout
    private lateinit var authUserEmailPasswordLayout: TextInputLayout
    private lateinit var authUserEmail: TextInputEditText
    private lateinit var authUserEmailPassword: TextInputEditText
    private lateinit var authContinueButton: Button
    private lateinit var authProgressBar: ProgressBar
    private lateinit var authCheckMarkIconGoogle: ImageView
    private lateinit var authCheckMarkIconEmail: ImageView
    private lateinit var authSigninGoogleButton: CardView
    private lateinit var authSigninEmailButton: CardView
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var signingInByGoogle:Boolean=false
    private var signingInByEmail:Boolean=false
    private var authedByGoogle:Boolean=false
    private var authedByEmail:Boolean=false
    private val Req_Code:Int=123
    lateinit var firebaseAuth: FirebaseAuth
    var isAuthed:MutableLiveData<Boolean> = MutableLiveData(false)


    interface ContinueCallbacks {
        fun onContinue()
    }

    private var continueCallbacks: ContinueCallbacks? = null

    //the callback functions
    override fun onAttach(context: Context) {
        super.onAttach(context)
        continueCallbacks = context as ContinueCallbacks?
    }

    override fun onDetach() {
        super.onDetach()
        continueCallbacks = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.m_fragment_auth,container,false)
        view.apply {
            authUserEmailLayout = findViewById(R.id.auth_userEmailLayout)
            authUserEmailPasswordLayout = findViewById(R.id.auth_userEmailPasswordLayout)
            authUserEmail = findViewById(R.id.auth_userEmail)
            authUserEmailPassword = findViewById(R.id.auth_userEmailPassword)
            authContinueButton = findViewById(R.id.auth_continueButton)
            authProgressBar = findViewById(R.id.auth_progressBar)
            authCheckMarkIconGoogle = findViewById(R.id.auth_checkMarkIconGoogle)
            authCheckMarkIconEmail = findViewById(R.id.auth_checkMarkIconEmail)
            authSigninGoogleButton = findViewById(R.id.auth_signinGoogleButton)
            authSigninEmailButton = findViewById(R.id.auth_signinEmailButton)
            firebaseAuth= FirebaseAuth.getInstance()
            val pass = preferences.getString("ArmyControlVerified","")
            isAuthed.postValue(pass != "")

            isAuthed.observe(viewLifecycleOwner, Observer {
                if (it) {
                    if(authedByGoogle){
                        authCheckMarkIconGoogle.isVisible = true
                    }
                    if(authedByEmail){
                        authCheckMarkIconEmail.isVisible = true
                    }
                    authContinueButton.isEnabled = true
                    authUserEmailLayout.visibility = View.GONE
                    authUserEmailPasswordLayout.visibility = View.GONE
                    authSigninEmailButton.isClickable = false
                    authSigninGoogleButton.isClickable = false
                    authContinueButton.text = "המשך"
                }
            })

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
// getting the value of gso inside the GoogleSigninClient
            mGoogleSignInClient= GoogleSignIn.getClient(requireActivity(),gso)
// initialize the firebaseAuth variable

            authSigninEmailButton.setOnClickListener {
                authContinueButton.isEnabled = true
                signingInByGoogle=false
                signingInByEmail=true
                authUserEmailLayout.visibility = View.VISIBLE
                authUserEmailPasswordLayout.visibility = View.VISIBLE
                authSigninEmailButton.visibility = View.GONE
            }

            authSigninGoogleButton.setOnClickListener {
                authContinueButton.isEnabled = false
                signingInByGoogle=true
                signingInByEmail=false
                authUserEmailLayout.visibility = View.GONE
                authUserEmailPasswordLayout.visibility = View.GONE
                authSigninEmailButton.visibility = View.VISIBLE

                signInGoogle()
            }

            authContinueButton.setOnClickListener {
                   if(isAuthed.value!!){
                       continueCallbacks!!.onContinue()
                   }else{
                       signUp()
                   }
            }
        }
        return view
    }

    private  fun signInGoogle(){

        val signInIntent:Intent=mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent,Req_Code)
    }
    // onActivityResult() function : this is where we provide the task and data for the Google Account
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==Req_Code){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }
    // handleResult() function -  this is where we update the UI after Google signin takes place
    private fun handleResult(completedTask: Task<GoogleSignInAccount>){
        //try {
            val account: GoogleSignInAccount? =completedTask.getResult(ApiException::class.java)
            if (account != null) {
                UpdateUI(account)
            }else{
                Toast.makeText(
                    requireContext(),
                    "account null",
                    Toast.LENGTH_SHORT
                ).show()
            }
//        } catch (e:ApiException){
//            Toast.makeText(
//                requireContext(),
//                e.stackTrace.toString(),
//                Toast.LENGTH_LONG
//            ).show()
//            authContinueButton.isEnabled = false
//
//            throw RuntimeException(e.message)
//        }
    }
    // UpdateUI() function - this is where we specify what UI updation are needed after google signin has taken place.
    fun UpdateUI(account: GoogleSignInAccount){
        val credential= GoogleAuthProvider.getCredential(account.idToken,null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {task->
            if(task.isSuccessful) {
                authedByGoogle = true
                isAuthed.postValue(true)
                preferences.edit().putString("googleAuth","true").apply()
                preferences.edit().putString("ArmyControlVerified", "verified").apply()
            }else{
                authContinueButton.isEnabled = true
            }
        }
    }





    fun signUp(){
        authProgressBar.visibility = View.VISIBLE
        val userEmail = authUserEmail.text.toString().trim()
        val userEmailPassword = authUserEmailPassword.text.toString().trim()
        var error = 0


        if(isAuthed.value==false) {
            if ( TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userEmailPassword)
            ) {
                error = 1
            }
        }

        when (error) {
            1 -> {
                if(TextUtils.isEmpty(userEmail) )
                    authUserEmail.error = "נא למלא את השדא"
                if(TextUtils.isEmpty(userEmailPassword))
                    authUserEmailPassword.error = "נא למלא את השדא"
                authProgressBar.visibility = View.GONE
            }
        }

        if(error!=1) {
            authFireBase(userEmail, userEmailPassword)
        }
    }

    private fun authFireBase(userEmail:String, userEmailPassword:String){

        view?.let {
            showMessage(it, "Authenticating...")
        }

            firebaseAuth.createUserWithEmailAndPassword(userEmail,userEmailPassword).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    preferences.edit().putString("ArmyControlVerified", "verified").apply()
                    authedByEmail = true
                    isAuthed.postValue(true)
                    authProgressBar.visibility = View.GONE
                } else {
                    view?.let {
                        showMessage(it, "an Error has occerd, try auth with google")
                        authContinueButton.isEnabled = true
                        authUserEmailLayout.visibility = View.GONE
                        authUserEmailPasswordLayout.visibility = View.GONE
                    }
                    authProgressBar.visibility = View.GONE
                }
            }


    }

    fun showMessage(view:View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

    companion object{
        fun newInstance(context: Context,preferences: SharedPreferences): AuthFragment {
            return AuthFragment(context,preferences)
        }
    }
}
