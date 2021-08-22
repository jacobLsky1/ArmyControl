package com.jacoblip.andriod.armycontrol.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.sevices.SoldiersViewModel
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.views.activities.MainActivitiesFragment
import com.jacoblip.andriod.armycontrol.views.soldiers.MainSoldiersFragment

class MainFragment(var commandPath:String):Fragment() {

    lateinit var bottomNavigationView:BottomNavigationView
    lateinit var addFAB:FloatingActionButton
    lateinit var addSoldierFAB:FloatingActionButton
    lateinit var addActivityFAB:FloatingActionButton
    var soldiersFragment:Fragment = MainSoldiersFragment.newInstance(commandPath)
    var activiteiesFragment:Fragment = MainActivitiesFragment.newInstance(commandPath)
    var fragmentReady:MutableLiveData<Boolean> = MutableLiveData()
    var fabClicked:Boolean = false
    var addSoldierY : Float = 0F
    var addActivityY : Float = 0F
    lateinit var addSoldierTV:TextView
    lateinit var addActivityTV:TextView
    lateinit var soldiersViewModel:SoldiersViewModel

    private val rotateOpenAnim:Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_open_anim) }
    private val rotateCloseAnim:Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_close_anim) }

    interface AddSoldierCallBacks{
        fun addSoldier()
    }
    private var addSoldierCallbacks:AddSoldierCallBacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        addSoldierCallbacks = context as AddSoldierCallBacks?
    }

    override fun onDetach() {
        super.onDetach()
        addSoldierCallbacks = null
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        soldiersViewModel = ViewModelProvider(requireActivity()).get(SoldiersViewModel::class.java)
       val view = inflater.inflate(R.layout.m_fragment_main,container,false)
        view.apply {
            addFAB = findViewById(R.id.fab)
            addSoldierFAB = findViewById(R.id.fab_add_soldier)
            addActivityFAB = findViewById(R.id.fab_add_activity)
            addSoldierTV = findViewById(R.id.fab_add_soldierTV)
            addActivityTV = findViewById(R.id.fab_add_activityTV)
            bottomNavigationView = findViewById(R.id.bottomNavigationMenu)
            bottomNavigationView.background = null
            bottomNavigationView.menu.getItem(1).isEnabled = false

            bottomNavigationView.setOnNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.personnelTab->{
                        setFragment(soldiersFragment)
                    }
                    R.id.timeLineTab->{
                        setFragment(activiteiesFragment)
                    }
                }
                true
            }
        }
        setUpObservers()
        setFragment(soldiersFragment)
        return view
    }

    fun setFragment(fragment: Fragment){

        childFragmentManager
            .beginTransaction()
            .replace(R.id.secondary_fragment_container,fragment)
            .commit()
    }
    fun setUpObservers(){
        fragmentReady.observe(viewLifecycleOwner, Observer {
            if(it){
                getYs()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickEvents()
        fragmentReady.postValue(true)
    }

    fun getYs(){
        if(Util.addFABY==0F) {
            if (addFAB.y == 0F) {
                fragmentReady.postValue(true)
            } else {
                Util.addFABY = addFAB.y
            }
        }else{
            addSoldierY = Util.addFABY
            addActivityY = Util.addFABY
            Log.i("MainFragment",addActivityY.toString())
            Log.i("MainFragment",addSoldierY.toString())
        }
    }

    private fun setClickEvents(){
        addFAB.setOnClickListener {
            onAddFabClicked()
        }
        addSoldierFAB.setOnClickListener {
            addSoldierCallbacks?.addSoldier()
            onAddFabClicked()
        }
        addActivityFAB.setOnClickListener {
            Toast.makeText(requireContext(),"add Activity",Toast.LENGTH_SHORT).show()
            onAddFabClicked()
        }
    }

    private fun onAddFabClicked(){
        setAnimation(fabClicked)
        fabClicked = !fabClicked
    }

    fun makeTVsVisible(){
        addSoldierTV.visibility = View.INVISIBLE
        addActivityTV.visibility = View.INVISIBLE
        addSoldierTV.y = addSoldierFAB.y+20
        addSoldierTV.x = addSoldierFAB.x+addSoldierFAB.width+25
        addActivityTV.y = addActivityFAB.y+20
        addActivityTV.x = addActivityFAB.x+addActivityFAB.width+25
        addSoldierTV.visibility = View.VISIBLE
        addActivityTV.visibility = View.VISIBLE
    }

    fun makeTVsInvisible(){
        addSoldierTV.visibility = View.GONE
        addActivityTV.visibility = View.GONE
    }

    private fun setVisibility(fabClicked:Boolean){
        if(!fabClicked){
            addActivityFAB.visibility = View.VISIBLE
            addSoldierFAB.visibility = View.VISIBLE
        }else{
            addActivityFAB.visibility = View.INVISIBLE
            addSoldierFAB.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(fabClicked:Boolean){

        if(!fabClicked){
            addSoldierFAB.animate().y(addSoldierY-300F).setDuration(300).withStartAction { setVisibility(fabClicked) }.withEndAction { makeTVsVisible() }.start()
            addActivityFAB.animate().y(addSoldierY-150F).setDuration(300).withStartAction { setVisibility(fabClicked) }.withEndAction { makeTVsVisible() }.start()
            addFAB.startAnimation(rotateOpenAnim)
        }else{
            addSoldierFAB.animate().y(addSoldierY).setDuration(300).withEndAction { setVisibility(fabClicked) }.withStartAction {  makeTVsInvisible() }.start()
            addActivityFAB.animate().y(addSoldierY).setDuration(300).withEndAction { setVisibility(fabClicked) }.withStartAction {  makeTVsInvisible() }.start()
            addFAB.startAnimation(rotateCloseAnim)
        }
    }

    companion object{
        fun newInstance(commandPath: String):MainFragment{
            return MainFragment(commandPath)
        }
    }
}