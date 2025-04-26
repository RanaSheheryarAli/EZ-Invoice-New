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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ezinvoice.adaptors.ItemAdapter
import com.example.ezinvoice.databinding.FragmentItemsBinding
import com.example.ezinvoice.viewmodels.ItemsFragmentViewmodel


class ItemsFragment : Fragment() {

    lateinit var databinding: FragmentItemsBinding
    lateinit var viewmodel: ItemsFragmentViewmodel
    lateinit var itemAdapter: ItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databinding = DataBindingUtil.inflate(inflater, R.layout.fragment_items, container, false)
        return databinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel = ViewModelProvider(this)[ItemsFragmentViewmodel::class.java]
        itemAdapter = ItemAdapter()

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
}
