package com.example.ezinvoice

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
import androidx.recyclerview.widget.RecyclerView
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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var clientAdapter: ReportAdapterClient
    private lateinit var itemAdapter: ReportAdatperItems

    private var allClients = listOf<Show_Report_Clients>()
    private var allItems = listOf<Show_Report_Items>()
    private var clientPage = 1
    private var itemPage = 1
    private val pageSize = 5

    private lateinit var businessId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)

        businessId =
            requireContext().getSharedPreferences("UserPrefs", AppCompatActivity.MODE_PRIVATE)
                .getString("business_id", "") ?: ""

        setupSpinners()
        setupRecyclerViews()

        // Default load for Today
        val (start, end) = getDateRangeForFilter("Today")
        loadTotalStats(businessId, start, end)

        loadTopClients(businessId)
        loadTopItems(businessId)

        return binding.root
    }

    private fun setupSpinners() {
        val priceFilterData = listOf("RS")
        val dateFilterData = listOf("Dates", "Today", "This Week", "This Month")

        binding.datePriceSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            priceFilterData
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.dateFilterSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            dateFilterData
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.dateFilterSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selected = parent.getItemAtPosition(position).toString()
                    if (selected != "Dates") {
                        val (startDate, endDate) = getDateRangeForFilter(selected)
                        loadTotalStats(businessId, startDate, endDate)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
    }

    private fun getDateRangeForFilter(filter: String): Pair<String, String> {
        val today = java.util.Calendar.getInstance()
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())

        return when (filter) {
            "Today" -> {
                val date = dateFormat.format(today.time)
                Pair(date, date)
            }

            "This Week" -> {
                val end = dateFormat.format(today.time)
                today.add(java.util.Calendar.DAY_OF_YEAR, -6)
                val start = dateFormat.format(today.time)
                Pair(start, end)
            }

            "This Month" -> {
                val end = dateFormat.format(today.time)
                today.set(java.util.Calendar.DAY_OF_MONTH, 1)
                val start = dateFormat.format(today.time)
                Pair(start, end)
            }

            else -> {
                val date = dateFormat.format(today.time)
                Pair(date, date)
            }
        }
    }

    private fun loadTotalStats(businessId: String, startDate: String, endDate: String) {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.createService(ReportApi::class.java)
                val response = api.getReportSummary(businessId, startDate, endDate)

                if (response.isSuccessful) {
                    val stats = response.body()
                    binding.tvTotalInvoices.text = stats?.totalInvoices.toString()
                    binding.tvTotalSales.text = "Rs ${stats?.totalSales ?: 0.0}"
                    binding.tvTotalExpence.text = "Rs ${stats?.totalExpense ?: 0.0}"
                    val profitOrLoss = stats?.profitOrLoss ?: 0.0
                    binding.tvTotalProfitloss.text = "Rs $profitOrLoss"

                    when {
                        profitOrLoss > 0 -> {
                            binding.tvTotalProfitloss.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.green_color
                                )
                            ) // profit
                        }

                        profitOrLoss < 0 -> {
                            binding.tvTotalProfitloss.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.red_color
                                )
                            ) // loss
                        }

                        else -> {
                            binding.tvTotalProfitloss.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.imp_text_color
                                )
                            ) // default
                        }
                    }

                    val entries = stats?.salesGraph?.mapIndexed { index, entry ->
                        Entry(index.toFloat(), entry.amount.toFloat())
                    } ?: emptyList()

                    val labels = stats?.salesGraph?.map { it.date } ?: emptyList()

                    updateLineChart(entries, labels)

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateLineChart(entries: List<Entry>, labels: List<String>) {
        val dataSet = LineDataSet(entries, "Sales Trends").apply {
            color = ContextCompat.getColor(requireContext(), R.color.buttoncolor)
            valueTextColor = Color.BLACK
            lineWidth = 2f
            circleRadius = 4f
            setDrawFilled(true)
            fillColor = ContextCompat.getColor(requireContext(), R.color.buttoncolor)
            fillAlpha = 50
        }

        val lineData = LineData(dataSet)

        binding.lineChart.apply {
            data = lineData
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = Color.BLACK
            xAxis.granularity = 1f
            axisRight.isEnabled = false
            axisLeft.textColor = Color.BLACK
            description.text = "Sales Graph"
            description.textColor = Color.BLACK
            animateXY(1000, 1000)

            // ✅ Set date labels for X-axis
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.labelRotationAngle = -45f // optional: slant text for clarity

            invalidate()
        }
    }


    private fun setupRecyclerViews() {
        binding.clientrecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.itemrecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.clientrecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisible = layoutManager.findLastCompletelyVisibleItemPosition()
                if (::clientAdapter.isInitialized && lastVisible == clientAdapter.itemCount - 1 &&
                    clientPage * pageSize < allClients.size
                ) {
                    clientPage++
                    val nextClients = allClients.take(clientPage * pageSize)
                    clientAdapter.updateData(nextClients)
                }
            }
        })

        binding.itemrecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisible = layoutManager.findLastCompletelyVisibleItemPosition()
                if (::itemAdapter.isInitialized && lastVisible == itemAdapter.itemCount - 1 &&
                    itemPage * pageSize < allItems.size
                ) {
                    itemPage++
                    val nextItems = allItems.take(itemPage * pageSize)
                    itemAdapter.updateData(nextItems)
                }
            }
        })
    }

    private fun loadTopClients(businessId: String) {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.createService(ReportApi::class.java)
                val response = api.getTopClients(businessId)
                if (response.isSuccessful) {
                    allClients = response.body() ?: emptyList()
                    clientAdapter = ReportAdapterClient(allClients.take(pageSize).toMutableList())
                    binding.clientrecyclerView.adapter = clientAdapter
                    clientPage = 1
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
                    allItems = response.body()?.filterNotNull()?.map {
                        Show_Report_Items(
                            productId = it.productId ?: "Unknown",
                            productName = it.productName ?: "Unknown",
                            quantitySold = it.quantitySold ?: 0,
                            totalSales = it.totalSales ?: 0
                        )
                    } ?: emptyList()
                    itemAdapter = ReportAdatperItems(allItems.take(pageSize).toMutableList())
                    binding.itemrecyclerView.adapter = itemAdapter
                    itemPage = 1
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
