package com.example.ezinvoice

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ezinvoice.adapters.ReportAdapterClient
import com.example.ezinvoice.adaptors.ReportAdatperItems
import com.example.ezinvoice.models.Show_Report_Clients
import com.example.ezinvoice.models.Show_Report_Items
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class ReportFragment : Fragment() {

    private lateinit var clientrecyclerView: RecyclerView
    private lateinit var itemrecyclerView: RecyclerView
    private lateinit var clientadapter: ReportAdapterClient
    private lateinit var itemAdapter: ReportAdatperItems
    private lateinit var clientList: List<Show_Report_Clients>
    private lateinit var itemList: List<Show_Report_Items>
    private lateinit var lineChart: LineChart

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_report, container, false)

        // Find the Spinners by ID
        val datePriceSpinner: Spinner = view.findViewById(R.id.datePriceSpinner)
        val filterDateSpinner: Spinner = view.findViewById(R.id.dateFilterSpinner)

        // Data for Price Filter Spinner
        val priceFilterData = listOf("PKR", "Under 1000", "1000 - 5000", "5000 - 10000", "Above 10000")

        // Data for Date Filter Spinner
        val dateFilterData = listOf("Dates", "Today", "This Week", "This Month", "Custom Date")

        // Create and set ArrayAdapters for both Spinners
        val priceAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priceFilterData)
        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        datePriceSpinner.adapter = priceAdapter

        val dateAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dateFilterData)
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterDateSpinner.adapter = dateAdapter

        // Handle Price Filter Spinner Selection
        datePriceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedPrice = parent.getItemAtPosition(position).toString()
                // Handle the selected price filter here
                // Example: Log or filter data based on price
                println("Selected Price Filter: $selectedPrice")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        // Handle Date Filter Spinner Selection
        filterDateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedDate = parent.getItemAtPosition(position).toString()
                // Handle the selected date filter here
                // Example: Log or filter data based on date
                println("Selected Date Filter: $selectedDate")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }



        // Find the LineChart by its ID
        lineChart = view.findViewById(R.id.lineChart)

        // Call the function to display the graph
        setupLineChart()

        // Initialize Both RecyclerView
        clientrecyclerView = view.findViewById(R.id.clientrecyclerView)
        clientrecyclerView.layoutManager = LinearLayoutManager(context)

        itemrecyclerView = view.findViewById(R.id.iterecyclerView)
        itemrecyclerView.layoutManager = LinearLayoutManager(context)

        // Create dummy data for the RecyclerView
        clientList = listOf(
            Show_Report_Clients("Client 01", 5, "Rs 10,000"),
            Show_Report_Clients("Client 02", 3, "Rs 7,500"),
            Show_Report_Clients("Client 03", 8, "Rs 15,200"),
            Show_Report_Clients("Client 04", 6, "Rs 12,100"),
            Show_Report_Clients("Client 05", 9, "Rs 17,300")
        )
        itemList = listOf(
            Show_Report_Items("Item 01", 5, "Rs 10,000"),
            Show_Report_Items("Item 02", 3, "Rs 7,500"),
            Show_Report_Items("Item 03", 8, "Rs 15,200"),
            Show_Report_Items("Item 04", 6, "Rs 12,100"),
            Show_Report_Items("Item 05", 9, "Rs 17,300")
        )

        // Set up client adapter
        clientadapter = ReportAdapterClient(clientList)
        clientrecyclerView.adapter = clientadapter

        // Set up item adapter
        itemAdapter = ReportAdatperItems(itemList)
        itemrecyclerView.adapter = itemAdapter

        return view
    }


    private fun setupLineChart() {
        // Dummy data for demonstration
        val entries = ArrayList<Entry>()
        entries.add(Entry(1f, 1000f)) // Day 1, Value 1000
        entries.add(Entry(2f, 1500f)) // Day 2, Value 1500
        entries.add(Entry(3f, 2000f)) // Day 3, Value 2000
        entries.add(Entry(4f, 1800f)) // Day 4, Value 1800
        entries.add(Entry(5f, 2500f)) // Day 5, Value 2500

        // Create a dataset for the line chart

        val btncolor:Color=

        val dataSet = LineDataSet(entries, "Invoice Billing Stats")
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.buttoncolor)
        dataSet.valueTextColor = Color.BLACK
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setDrawFilled(true) // Optional: fills the area under the line
        dataSet.fillColor = ContextCompat.getColor(requireContext(),R.color.buttoncolor) // Set fill color
        dataSet.fillAlpha = 50

        // Style the X-axis and Y-axis
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.BLACK
        xAxis.granularity = 1f // Interval between X-axis points

        lineChart.axisRight.isEnabled = false // Disable right Y-axis
        lineChart.axisLeft.textColor = Color.BLACK

        // Set data and refresh the chart
        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.description.text = "Invoice Billing Stats"
        lineChart.description.textColor = Color.BLACK
        lineChart.animateXY(1000, 1000) // Add animation
        lineChart.invalidate()
    }
}
