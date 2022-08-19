package com.example.exchangerates.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.exchangerates.adapters.CardItemAdapter
import com.example.exchangerates.adapters.CurrencyHistoryItemAdapter
import com.example.exchangerates.databinding.FragmentHomeBinding
import com.example.exchangerates.room.AppDatabase
import com.example.exchangerates.room.CurrencyDao
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    lateinit var viewPagerAdapter: CardItemAdapter
    lateinit var database: CurrencyDao
    lateinit var mediator: TabLayoutMediator
    lateinit var historyAdapter: CurrencyHistoryItemAdapter

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewPagerAdapter = CardItemAdapter(requireContext())
        database = AppDatabase.getInstance(requireContext()).currencyDao()

        historyAdapter = CurrencyHistoryItemAdapter()
        binding.rv.adapter = historyAdapter

        binding.rvCard.adapter = viewPagerAdapter

        homeViewModel.getCurrencies(requireContext())
            .observe(viewLifecycleOwner) { l ->
                viewPagerAdapter.submitList(l)
                TabLayoutMediator(
                    binding.tabLayout,
                    binding.rvCard
                ) { tab, position ->
                    tab.text = l[position].code
                }.attach()
                binding.cardTabLayout.attachToPager(binding.rvCard)
            }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val history = database.getByCode(tab?.text.toString())
                if (history.first().nbu_buy_price == null) {
                    historyAdapter.submitList(null)
                    binding.warning.visibility = View.VISIBLE
                } else {
                    binding.warning.visibility = View.GONE
                    historyAdapter.submitList(history)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}