package com.example.ezinvoice

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ezinvoice.adaptors.InvoiceAdapter
import com.example.ezinvoice.databinding.FragmentHomeBinding
import com.example.ezinvoice.databinding.FragmentItemsBinding
import com.example.ezinvoice.models.Invoice


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



        val recyclerView: RecyclerView = databinding.recentinvoiceRCV  // your RecyclerView ID
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val invoices = listOf(
            Invoice("001", "2025-03-01", "$200"),
            Invoice("002", "2025-04-02", "$150"),
            Invoice("003", "2025-04-03", "$2500"),
            Invoice("004", "2025-04-02", "$150"),
            Invoice("005", "2025-04-03", "$2500"),
            Invoice("006", "2025-05-03", "$5500")
        )

        val adapter = InvoiceAdapter(invoices)
        recyclerView.adapter = adapter
    }

}