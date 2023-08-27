package com.jacoblip.andriod.armycontrol.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.data.sevices.SoldiersViewModel
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.views.activities.MainActivitiesFragment
import com.jacoblip.andriod.armycontrol.views.soldiers.MainSoldiersFragment

class MainFragment(commandPath: String):Fragment() {

    lateinit var bottomNavigationView:BottomNavigationView
    lateinit var addFAB:FloatingActionButton
    lateinit var addSoldierFAB: ExtendedFloatingActionButton
    lateinit var addActivityFAB:ExtendedFloatingActionButton
    var soldiersFragment:Fragment = MainSoldiersFragment.newInstance(commandPath)
    var activiteiesFragment:Fragment = MainActivitiesFragment.newInstance(commandPath)
    var fragmentReady = false
    var fabClicked:Boolean = false
    lateinit var soldiersViewModel:SoldiersViewModel

    private val rotateOpenAnim:Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim) }
    private val rotateCloseAnim:Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim) }


    interface AddActivityCallBacks{
        fun addActivity()
    }
    private var addActivityCallback:AddActivityCallBacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        addActivityCallback = context as AddActivityCallBacks?
    }

    override fun onDetach() {
        super.onDetach()
        addActivityCallback = null
    }


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        soldiersViewModel = ViewModelProvider(requireActivity()).get(SoldiersViewModel::class.java)
       val view = inflater.inflate(R.layout.m_fragment_main, container, false)
        view.apply {
            addFAB = findViewById(R.id.fab)
            addSoldierFAB = findViewById(R.id.fab_add_soldier)
            addActivityFAB = findViewById(R.id.fab_add_activity)
            bottomNavigationView = findViewById(R.id.bottomNavigationMenu)
            bottomNavigationView.background = null
            bottomNavigationView.menu.getItem(1).isEnabled = false
            addSoldierFAB.shrink()
            addActivityFAB.shrink()



            bottomNavigationView.setOnNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.personnelTab -> {
                        if(fragmentReady) {
                            Util.inActivitiesFragment = false
                            childFragmentManager
                                .beginTransaction()
                                .replace(R.id.secondary_fragment_container, soldiersFragment)
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                    R.id.timeLineTab -> {
                        if(fragmentReady) {
                            Util.inActivitiesFragment = true
                            childFragmentManager
                                .beginTransaction()
                                .replace(R.id.secondary_fragment_container, activiteiesFragment)
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                }
                true
            }
           // currentFragment = soldiersFragment
        }
        setUpObservers()
        setFragment()
        return view
    }

    private fun setFragment(){

        if(Util.inActivitiesFragment) {
           bottomNavigationView.selectedItemId = R.id.timeLineTab

            Util.inActivitiesFragment=true
        }else{
            bottomNavigationView.selectedItemId = R.id.personnelTab
        }

    }
   private fun setUpObservers(){

       soldiersViewModel.gotSoldiers.observe(viewLifecycleOwner, Observer {
           addFAB.isEnabled = it
           fragmentReady = it
           bottomNavigationView.selectedItemId = R.id.personnelTab
       })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickEvents()
    }


    private fun setClickEvents(){
        addFAB.setOnClickListener {
            onAddFabClicked()
        }
        addSoldierFAB.setOnClickListener {
            if(Util.userCommandPath=="1") {
            createSoldierDialog()
            }else{
                Toast.makeText(requireContext(), "רק מפקד פלוגה יכול להוסיף חייל", Toast.LENGTH_LONG).show()
            }
            onAddFabClicked()
        }
        addActivityFAB.setOnClickListener {
            if(Util.userCommandPath.length<=3) {
                addActivityCallback?.addActivity()
                onAddFabClicked()
            }else{
                Toast.makeText(requireContext(), "רק מפ או ממ יכול להוסיף פעילות", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun onAddFabClicked(){
        setAnimation(fabClicked)
        fabClicked = !fabClicked
    }

   private fun createSoldierDialog() {
       val inflater = layoutInflater
       val dialogView = inflater.inflate(R.layout.s_fragment_add_soldier, null)
       var nameInputText = dialogView.findViewById(R.id.addSoldierName) as TextInputEditText
       var idNumberInputText = dialogView.findViewById(R.id.addSoldierIdNumber) as TextInputEditText
       var phoneNumberInputText = dialogView.findViewById(R.id.addSoldierPhone) as TextInputEditText
       var civilianJobInputText = dialogView.findViewById(R.id.addSoldierCivilianJob) as TextInputEditText
       var positionSpinner = dialogView.findViewById(R.id.spinnerPosition) as Spinner
       var armyJobSpinner = dialogView.findViewById(R.id.spinnerArmyJob) as Spinner
       var progressBar = dialogView.findViewById(R.id.addSoldierProgressBar) as ProgressBar
       var addSoldierButton = dialogView.findViewById(R.id.addSoldierButton) as Button
       var addSoldierCancelButton = dialogView.findViewById(R.id.addSoldierCancelButton) as Button
       val alertDialog = AlertDialog.Builder(requireContext())
       alertDialog.setView(dialogView).setCancelable(false)

       setUpSpinnerAdapters(positionSpinner, armyJobSpinner)

       val dialog = alertDialog.create()
       dialog.show()

       addSoldierCancelButton.setOnClickListener {
           dialog.dismiss()
       }
       addSoldierButton.setOnClickListener {

               var inputCorrect = true
               var name = nameInputText.text.toString().trim()
               var idNumber = idNumberInputText.text.toString().trim()
               var phone = phoneNumberInputText.text.toString().trim()
               var job = civilianJobInputText.text.toString().trim()
               var position = positionSpinner.selectedItem.toString()
               var armyJob = armyJobSpinner.selectedItem.toString()

               if (TextUtils.isEmpty(name) || TextUtils.isEmpty(idNumber) || TextUtils.isEmpty(phone)) {
                   inputCorrect = false
                   if (TextUtils.isEmpty(name)) {
                       nameInputText.error = "נא למלא את השדא"
                   }
                   if (TextUtils.isEmpty(idNumber)) {
                       idNumberInputText.error = "נא למלא את השדא"
                   }
                   if (TextUtils.isEmpty(phone)) {
                       phoneNumberInputText.error = "נא למלא את השדא"
                   }
                   vibrateAlert()
               }

               if (inputCorrect) {
                   var armyJob = Util.getArmyJobPosition(armyJob)
                   var soldierPosition = Util.getSoldierArmyPosition(position)
                   var isCommander = (armyJob != "")
                   var isLieutenant = if (isCommander) {
                       armyJob[0] == '-'
                   } else false

                   var soldier = Soldier(
                       name,
                       idNumber,
                       "",
                       "",
                       false,
                       "",
                       listOf(),
                       phone,
                       job,
                       armyJob,
                       soldierPosition,
                       "",
                       isCommander,
                       isLieutenant,
                       listOf(),
                       listOf()
                   )

                   soldiersViewModel.addSoldier(soldier)
                   dialog.dismiss()
               }

       }
   }

    private fun  setUpSpinnerAdapters(sp1: Spinner, sp2: Spinner){
        val armyCommanderPositions = Util.getListOfCommanderPositions()
        val armySoldierPositions = Util.getListOfArmyPositions()
        var positionSpinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, armySoldierPositions)
        var armyJobSpinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, armyCommanderPositions)
        sp1.adapter = positionSpinnerAdapter
        sp2.adapter = armyJobSpinnerAdapter
    }


    private fun setVisibility(fabClicked: Boolean){
        if(!fabClicked){

            addSoldierFAB.setAlpha(0f);
            addSoldierFAB.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator) {
                            super.onAnimationStart(animation)
                            addSoldierFAB.isVisible = true
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            addSoldierFAB.extend()
                        }
                    });

            addActivityFAB.setAlpha(0f);
            addActivityFAB.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .setListener(object : AnimatorListenerAdapter() {

                        override fun onAnimationStart(animation: Animator) {
                            super.onAnimationStart(animation)
                            addActivityFAB.isVisible = true
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            addActivityFAB.extend()
                        }
                    });
        }else{

            addSoldierFAB.setAlpha(1f)
            addSoldierFAB.animate()
                    .alpha(0f)
                    .setDuration(400)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator) {
                            super.onAnimationStart(animation)
                            addSoldierFAB.shrink()
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            addSoldierFAB.isVisible = false
                        }
                    });

            addActivityFAB.setAlpha(1f)
            addActivityFAB.animate()
                    .alpha(0f)
                    .setDuration(400)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator) {
                            super.onAnimationStart(animation)
                            addActivityFAB.shrink()
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            addActivityFAB.isVisible = false
                        }
                    });
        }
    }

    private fun setAnimation(fabClicked: Boolean){

        if(!fabClicked){
            addFAB.startAnimation(rotateOpenAnim)
            setVisibility(fabClicked)


            /*
            addSoldierFAB.animate().y(addSoldierY - 300F).setDuration(300).withStartAction { setVisibility(fabClicked) }.withEndAction { makeTVsVisible() }.start()
            addActivityFAB.animate().y(addSoldierY - 150F).setDuration(300).withStartAction { setVisibility(fabClicked) }.withEndAction { makeTVsVisible() }.start()
             */
        }else{
            addFAB.startAnimation(rotateCloseAnim)
            setVisibility(fabClicked)
            /*
            addSoldierFAB.animate().y(addSoldierY).setDuration(300).withEndAction { setVisibility(fabClicked) }.withStartAction {  makeTVsInvisible() }.start()
            addActivityFAB.animate().y(addSoldierY).setDuration(300).withEndAction { setVisibility(fabClicked) }.withStartAction {  makeTVsInvisible() }.start()
             */
        }
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

    companion object{
        fun newInstance(commandPath: String):MainFragment{
            return MainFragment(commandPath)
        }
    }
}