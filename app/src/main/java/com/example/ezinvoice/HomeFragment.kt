package com.example.ezinvoice

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ezinvoice.adaptors.InvoiceAdapter
import com.example.ezinvoice.databinding.FragmentHomeBinding
import com.example.ezinvoice.viewmodels.HomeFragmentViewmodel
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeFragmentViewmodel
    private lateinit var adapter: InvoiceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val businessId = Scan_Barcode.SharedPrefManager.getBusinessId(requireContext()) // replace with your actual method
        adapter = InvoiceAdapter(emptyList())
        binding.recentinvoiceRCV.layoutManager = LinearLayoutManager(requireContext())
        binding.recentinvoiceRCV.adapter = adapter

        viewModel = ViewModelProvider(this)[HomeFragmentViewmodel::class.java]
        viewModel.fetchInvoices(businessId)

        viewModel.invoices.observe(viewLifecycleOwner) { invoices ->
            adapter.updateData(invoices)
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->

            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }

        // Navigation listeners
        binding.containerReports.setOnClickListener {
            val navController = findNavController()

            // Programmatically select the second position (ReportFragment) in the BottomNavigationView
            val bottomNavigationView: BottomNavigationView = activity?.findViewById(R.id.bottomnavigation)!!
            bottomNavigationView.selectedItemId = R.id.reportFragment

            // Navigate to the second fragment (ReportFragment)
            navController.navigate(R.id.reportFragment)
        }
        binding.containerProduct.setOnClickListener {
            startActivity(Intent(requireContext(), Add_Items::class.java))
        }
        binding.containerEstimate.setOnClickListener {
            startActivity(Intent(requireContext(), Sale::class.java))
        }
        binding.containerProduct.setOnClickListener {
            val navController = findNavController()

            // Programmatically select the second position (ReportFragment) in the BottomNavigationView
            val bottomNavigationView: BottomNavigationView = activity?.findViewById(R.id.bottomnavigation)!!
            bottomNavigationView.selectedItemId = R.id.itemsFragment

            // Navigate to the second fragment (ReportFragment)
            navController.navigate(R.id.itemsFragment)
//            findNavController().navigate(R.id.action_homeFragment_to_itemsFragment)
        }
        binding.containerClient.setOnClickListener {
            startActivity(Intent(requireContext(), Customer::class.java))
        }
        binding.containerInventory.setOnClickListener {
            startActivity(Intent(requireContext(), Add_Items::class.java))
        }
    }
}
