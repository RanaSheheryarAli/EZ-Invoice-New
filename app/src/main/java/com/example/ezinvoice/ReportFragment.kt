package com.example.ezinvoice

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ezinvoice.adapters.ReportAdapterClient
import com.example.ezinvoice.adaptors.ReportAdatperItems
import com.example.ezinvoice.apis.ReportApi
import com.example.ezinvoice.databinding.FragmentReportBinding
import com.example.ezinvoice.models.Show_Report_Clients
import com.example.ezinvoice.models.Show_Report_Items
import com.example.ezinvoice.network.RetrofitClient
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.launch
import com.github.mikephil.charting.components.XAxis

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var clientAdapter: ReportAdapterClient
    private lateinit var itemAdapter: ReportAdatperItems

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)

        val businessId = requireContext().getSharedPreferences("UserPrefs", AppCompatActivity.MODE_PRIVATE)
            .getString("business_id", "") ?: ""

        setupSpinners()
        setupRecyclerViews()

        loadTotalStats(businessId)
        loadGraphData(businessId)
        loadTopClients(businessId)
        loadTopItems(businessId)

        return binding.root
    }

    private fun setupSpinners() {
        val priceFilterData = listOf("PKR", "Under 1000", "1000 - 5000", "5000 - 10000", "Above 10000")
        val dateFilterData = listOf("Dates", "Today", "This Week", "This Month", "Custom Date")

        binding.datePriceSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priceFilterData).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.dateFilterSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dateFilterData).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.datePriceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Handle price filter selection if needed
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.dateFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Handle date filter selection if needed
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupRecyclerViews() {
        binding.clientrecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.itemrecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadTotalStats(businessId: String) {
        lifecycleScope.launch {
            val api = RetrofitClient.createService(ReportApi::class.java)
            val response = api.getTotalStats(businessId)
            if (response.isSuccessful) {
                val stats = response.body()
                binding.tvTotalInvoices.text = "${stats?.get("totalInvoices") ?: "0"}"
                binding.tvTotalSales.text = "Rs ${stats?.get("totalSales") ?: "0"}"
            }
        }
    }


    private fun loadGraphData(businessId: String) {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.createService(ReportApi::class.java)
                val response = api.getSalesGraph(businessId)
                if (response.isSuccessful) {
                    val graphData = response.body() as? Map<String, Double>
                    val entries = graphData?.entries?.mapIndexed { index, entry ->
                        Entry(index.toFloat(), entry.value.toFloat())
                    } ?: emptyList()
                    updateLineChart(entries)

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    private fun updateLineChart(entries: List<Entry>) {
        val dataSet = LineDataSet(entries, "Sales Trends").apply {
            color = ContextCompat.getColor(requireContext(), R.color.buttoncolor)
            valueTextColor = Color.BLACK
            lineWidth = 2f
            circleRadius = 4f
            setDrawFilled(true)
            fillColor = ContextCompat.getColor(requireContext(), R.color.buttoncolor)
            fillAlpha = 50
        }

        binding.lineChart.apply {
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = Color.BLACK
            xAxis.granularity = 1f
            axisRight.isEnabled = false
            axisLeft.textColor = Color.BLACK
            data = LineData(dataSet)
            description.text = "Sales Graph"
            description.textColor = Color.BLACK
            animateXY(1000, 1000)
            invalidate()
        }
    }

    private fun loadTopClients(businessId: String) {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.createService(ReportApi::class.java)
                val response = api.getTopClients(businessId)
                if (response.isSuccessful) {
                    val clients = response.body() ?: emptyList()
                    clientAdapter = ReportAdapterClient(clients)
                    binding.clientrecyclerView.adapter = clientAdapter
                    clientAdapter = ReportAdapterClient(clients)
                    binding.clientrecyclerView.adapter = clientAdapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun loadTopItems(businessId: String) {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.createService(ReportApi::class.java)
                val response = api.getTopSellingItems(businessId)
                if (response.isSuccessful) {
                    val items = response.body()?.filterNotNull()?.map {
                        Show_Report_Items(
                            productId = it.productId ?: "Unknown",
                            productName = it.productName ?: "Unknown",
                            quantitySold = it.quantitySold ?: 0,
                            totalSales = it.totalSales ?: 0
                        )
                    } ?: emptyList()
                    itemAdapter = ReportAdatperItems(items)
                    binding.itemrecyclerView.adapter = itemAdapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
