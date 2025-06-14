package com.example.ezinvoice

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ezinvoice.adaptors.ItemAdapter
import com.example.ezinvoice.apis.productapi
import com.example.ezinvoice.databinding.FragmentItemsBinding
import com.example.ezinvoice.models.OnProductActionListener
import com.example.ezinvoice.network.RetrofitClient
import com.example.ezinvoice.viewmodels.ItemsFragmentViewmodel
import kotlinx.coroutines.launch

class ItemsFragment : Fragment(), OnProductActionListener {

    lateinit var databinding: FragmentItemsBinding
    lateinit var viewmodel: ItemsFragmentViewmodel
    lateinit var itemAdapter: ItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        databinding = DataBindingUtil.inflate(inflater, R.layout.fragment_items, container, false)
        return databinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel = ViewModelProvider(this)[ItemsFragmentViewmodel::class.java]
        itemAdapter = ItemAdapter(this) // passing listener

        databinding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    viewmodel.fetchProducts()
                } else {
                    viewmodel.searchProducts(query)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        databinding.scanIcon.setOnClickListener {
            val intent = Intent(requireContext(), Scan_Barcode::class.java)
            startActivity(intent)
        }

        databinding.showitemRCV.layoutManager = LinearLayoutManager(requireContext())
        databinding.showitemRCV.adapter = itemAdapter

        viewmodel.fetchProducts()

        viewmodel.productList.observe(viewLifecycleOwner) { products ->
            viewmodel.issuccessfull.observe(viewLifecycleOwner) { success ->
                databinding.txNoItemShow.visibility = View.GONE
                databinding.containerShowItems.visibility = View.VISIBLE
                itemAdapter.setItems(products)
            }
        }

        viewmodel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                itemAdapter.setItems(emptyList())
                databinding.txNoItemShow.visibility = View.VISIBLE
                databinding.containerShowItems.visibility = View.GONE
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        databinding.btnAddnew.setOnClickListener {
            val intent = Intent(requireContext(), Add_Items::class.java)
            startActivity(intent)
        }
    }

    override fun onUpdateClicked(productId: String) {
        val intent = Intent(requireContext(), Add_Items::class.java)
        intent.putExtra("product_id", productId)
        intent.putExtra("isUpdate", true)
        startActivity(intent)
    }

    override fun onDeleteClicked(productId: String) {
        deleteProduct(productId)
    }

    fun deleteProduct(productId: String) {
        val api = RetrofitClient.createService(productapi::class.java)
        viewmodel.viewModelScope.launch {
            try {
                val response = api.deleteProduct(productId)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Product Deleted", Toast.LENGTH_SHORT).show()
                    viewmodel.fetchProducts()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    val message = errorMsg ?: "Delete Failed"
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
