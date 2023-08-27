package com.jacoblip.andriod.armycontrol.views.soldiers

import android.content.Context
import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jacoblip.andriod.armycontrol.R
import com.jacoblip.andriod.armycontrol.data.models.ArmyActivity
import com.jacoblip.andriod.armycontrol.data.models.Soldier
import com.jacoblip.andriod.armycontrol.data.sevices.ActivitiesViewModel
import com.jacoblip.andriod.armycontrol.data.sevices.SoldiersViewModel
import com.jacoblip.andriod.armycontrol.databinding.SFragmentSignedItemsBinding
import com.jacoblip.andriod.armycontrol.utilities.Util
import com.jacoblip.andriod.armycontrol.views.adapters.*

class SignedItemsFragment(): Fragment() {

    lateinit var soldiersViewModel: SoldiersViewModel
    lateinit var binding: SFragmentSignedItemsBinding
    var signedItemsList = mutableListOf<String>()
    var gunAmount = 0
    var biAmount = 0
    var nvAmount = 0
    var radioAmount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        soldiersViewModel = ViewModelProvider(requireActivity()).get(SoldiersViewModel::class.java)
        soldiersViewModel.currentFragment = this
        binding = SFragmentSignedItemsBinding.inflate(LayoutInflater.from(requireContext()))
        val view = binding.root
        binding.signedItemsRV.layoutManager = LinearLayoutManager(requireContext())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()
        setFragment()
    }

    private fun setUpObservers(){
        soldiersViewModel.listOfPersonalSoldiers.observe(viewLifecycleOwner, Observer {
            if(!it.isNullOrEmpty()){
                for(soldier in it){
                    if(soldier.hasGun){
                        var str = "${soldier.name} - נשק - ${soldier.numGun}"
                        signedItemsList.add(str)
                        gunAmount++
                    }
                    if(soldier.hasBi){
                        var str = "${soldier.name} - משקפת - ${soldier.numBi}"
                        signedItemsList.add(str)
                        biAmount++
                    }
                    if(soldier.hasNV){
                        var str = "${soldier.name} - אמרל - ${soldier.numNV}"
                        signedItemsList.add(str)
                        nvAmount++
                    }
                    if(soldier.hasRadio){
                        var str = "${soldier.name} - קשר - ${soldier.numRadio}"
                        signedItemsList.add(str)
                        radioAmount++
                    }
                }
                binding.gunAmountCardTV.text = gunAmount.toString()
                binding.biAmountCardTV.text = biAmount.toString()
                binding.nvAmountCardTV.text = nvAmount.toString()
                binding.radioAmountCardTV.text = radioAmount.toString()
                binding.signedItemsRV.adapter = SignedItemsAdapter(signedItemsList)
            }
        })
    }

    private fun setFragment(){
        binding.signedItemsSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText!=""){
                    var newList = signedItemsList.filter { it.contains(newText.toString(),true) }
                    binding.signedItemsRV.adapter = SignedItemsAdapter(newList.toMutableList())
                }else{
                    binding.signedItemsRV.adapter = SignedItemsAdapter(signedItemsList)
                }
                return true
            }
        })
    }


    companion object{
        fun newInstance(): SignedItemsFragment {
            return SignedItemsFragment()
        }
    }
}