package com.example.exchangerates.ui.gallery

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.exchangerates.R
import com.example.exchangerates.adapters.CurrencyItemAdapter
import com.example.exchangerates.databinding.FragmentGalleryBinding
import com.example.exchangerates.models.Currency
import com.example.exchangerates.ui.home.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: HomeViewModel
    private var _binding: FragmentGalleryBinding? = null
    lateinit var adapter: CurrencyItemAdapter
    lateinit var sharedPreferences: SharedPreferences
    lateinit var listCurrency: List<Currency>

    private val binding get() = _binding!!
    private val TAG = "GalleryFragment"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        galleryViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        adapter = CurrencyItemAdapter(calcClick, requireContext())
        binding.rv.adapter = adapter
        galleryViewModel.getCurrencies(requireContext())
            .observe(viewLifecycleOwner) { l ->
                listCurrency = l
                adapter.submitList(l)
            }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)

        val menuItem: MenuItem = menu.findItem(R.id.action_search)
        val searchView = menuItem.actionView as SearchView
        val txtSearch =
            searchView.findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText
        txtSearch.setHintTextColor(Color.parseColor("#808080"))
        txtSearch.setTextColor(Color.BLACK)
        searchView.queryHint = "Qidirish..."
        searchView.setOnQueryTextListener(listener)
        super.onCreateOptionsMenu(menu, inflater)
    }

    var calcClick = object : CurrencyItemAdapter.ItemClickListener {
        override fun calcButtonClickListener(currency: Currency) {
            sharedPreferences = requireActivity().getSharedPreferences("curr", MODE_PRIVATE)
            sharedPreferences.edit().putString("code", currency.code).apply()
            val bottomNavigationView =
                requireActivity().findViewById(R.id.bottom_bar) as BottomNavigationView
            requireActivity()
            bottomNavigationView.selectedItemId = R.id.nav_slideshow
        }

    }

    val listener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            val l = ArrayList<Currency>()
            listCurrency.forEach { c ->
                if (c.title!!.lowercase().contains(newText.toString().lowercase())) l.add(c)
            }
            adapter.submitList(l)
            return true
        }

    }
}