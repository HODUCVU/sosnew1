package com.example.appsos.fragmentOfLearn

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.appsos.R
import com.example.appsos.adapter.AdapterTutorial
import com.example.appsos.dataObject.listTutorial
import com.example.appsos.databinding.FragmentTutorialBinding


class Tutorial : Fragment() {

    private val namePrefer = "FirstAccess"
    private var checkFirstAccess : Boolean? = null

    private var _binding : FragmentTutorialBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterTutorial: AdapterTutorial
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTutorialBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            adapterTutorial = AdapterTutorial(listTutorial)
            recycleTutorial.adapter = adapterTutorial
            recycleTutorial.setHasFixedSize(true)
            adapterTutorial.notifyDataSetChanged()

            btnGotTutorial.setOnClickListener {
                intentFragment()
            }
        }
    }
    private fun getStatus() {
        val preference = this.requireActivity().getSharedPreferences(namePrefer, Context.MODE_PRIVATE)
        checkFirstAccess = preference.getBoolean("firstAccess2",false)
    }
    private fun intentFragment() {
        getStatus()
        if(checkFirstAccess == true) {
            findNavController().navigate(R.id.action_tutorial_to_FirstFragment)
        } else {
            findNavController().navigate(R.id.action_tutorial_to_SecondFragment)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}