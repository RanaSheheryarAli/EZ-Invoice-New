package com.example.ezinvoice

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.ezinvoice.databinding.FragmentHomeBinding
import com.example.ezinvoice.databinding.FragmentItemsBinding


class HomeFragment : Fragment() {

    lateinit var databinding: FragmentHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        databinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        // Inflate the layout for this fragment
        return databinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { super.onViewCreated(view, savedInstanceState)

        databinding.containerProduct.setOnClickListener{
            val intent = Intent(requireContext(), Add_Items::class.java)
            startActivity(intent)
        }

        databinding.containerInvoice.setOnClickListener{
            val intent = Intent(requireContext(), Sale::class.java)
            startActivity(intent)
        }
        databinding.containerReports.setOnClickListener {

        }

        databinding.containerInventory.setOnClickListener{
            val intent = Intent(requireContext(), Add_Items::class.java)
            startActivity(intent)
        }

        databinding.containerClient.setOnClickListener{
            val intent = Intent(requireContext(), Client::class.java)
            startActivity(intent)
        }


    }

}