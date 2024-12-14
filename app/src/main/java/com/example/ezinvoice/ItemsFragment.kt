package com.example.ezinvoice

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ezinvoice.databinding.FragmentItemsBinding


class ItemsFragment : Fragment() {

    lateinit var databinding:FragmentItemsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        databinding = DataBindingUtil.inflate(inflater, R.layout.fragment_items, container, false)
        // Inflate the layout for this fragment
        return databinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { super.onViewCreated(view, savedInstanceState)

        databinding.btnAddnew.setOnClickListener{
            val intent = Intent(requireContext(), Add_Items::class.java)
            startActivity(intent)
        }

    }

    }