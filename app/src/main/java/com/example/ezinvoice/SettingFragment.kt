package com.example.ezinvoice

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ezinvoice.databinding.FragmentReportBinding
import com.example.ezinvoice.databinding.FragmentSettingBinding


class SettingFragment : Fragment() {

    private lateinit var databinding:FragmentSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        databinding = FragmentSettingBinding.inflate(inflater, container, false)


        databinding.tvSignature.setOnClickListener{
            val intent= Intent(requireContext(),Signature::class.java)
            startActivity(intent)
        }


        return databinding.root
    }





}