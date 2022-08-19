package com.example.exchangerates.ui.slideshow

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.exchangerates.R
import com.example.exchangerates.adapters.CountrySpinnerItemAdapter
import com.example.exchangerates.databinding.FragmentSlideshowBinding
import com.example.exchangerates.models.Currencies
import com.example.exchangerates.models.Currency
import com.example.exchangerates.models.Flag
import com.example.exchangerates.models.ImageCodeDecode
import com.example.exchangerates.room.AppDatabase
import com.example.exchangerates.room.CurrencyDao
import com.example.exchangerates.ui.home.HomeViewModel
import java.lang.NullPointerException

class SlideshowFragment : Fragment() {

    private lateinit var slideshowViewModel: HomeViewModel
    private var _binding: FragmentSlideshowBinding? = null
    lateinit var adapter1: CountrySpinnerItemAdapter
    lateinit var adapter2: CountrySpinnerItemAdapter
    lateinit var database: CurrencyDao
    lateinit var sharedPreferences: SharedPreferences
    private var isClick: Boolean = false
    private var firstLastCurrency: Currency? = null
    private var secondLastCurrency: Currency? = null
    lateinit var map: HashMap<Currency, Int>
    //todo: o'rtadagi almashtiruvchi buttonni taxlash
    //todo: 1-spinnerda tanlanganni ikkinchidan olish 2-spinnerda olinganni birinchidan olish
    //todo: basada ma'lumot yo'q bo'lsa internetni tekshirish va dialog chiqarish

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        slideshowViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext()).currencyDao()
        map = HashMap()
        sharedPreferences = requireActivity().getSharedPreferences("curr", Context.MODE_PRIVATE)
        val code = sharedPreferences.getString("code", "")
        sharedPreferences.edit().remove("code").apply()
        binding.symbol.viewTreeObserver.addOnGlobalLayoutListener(symbolListener)
        val currency = Currency("UZS", 1.0, "", null, null, "O'zbeksiton so'mi")
        currency.id = 0
        if (database.getFlagById("UZS") == null) database.insertFlag(
            Flag(
                "UZS",
                ImageCodeDecode.code(BitmapFactory.decodeResource(resources, R.drawable.ic_flag))
            )
        )

        binding.exchangeBtn.setOnClickListener {
            isClick = true
            val first = binding.firstCountrySpinner.selectedItemPosition
            val second = binding.secondCountrySpinner.selectedItemPosition
            val temp = adapter1.l
            adapter1.l = adapter2.l
            adapter2.l = temp
            adapter1.notifyDataSetChanged()
            adapter2.notifyDataSetChanged()
            binding.firstCountrySpinner.setSelection(second)
            binding.secondCountrySpinner.setSelection(first)
            firstLastCurrency = adapter1.l[second]
            secondLastCurrency = adapter2.l[first]
        }
        slideshowViewModel.getCurrencies(requireContext()).observe(viewLifecycleOwner) { l ->
            map[currency] = 0
            for (i in 1..l.size) map[l[i - 1]] = i
            val c = searchCode(l, code!!)
            adapter1 = CountrySpinnerItemAdapter(ArrayList(l), requireContext())
            val l2 = ArrayList(l)
            l2.removeAt(c)
            l2.add(0, currency)
            adapter2 = CountrySpinnerItemAdapter(l2, requireContext())
            secondLastCurrency = adapter2.l[0]
            binding.firstCountrySpinner.adapter = adapter1
            binding.secondCountrySpinner.adapter = adapter2
            binding.firstCountrySpinner.setSelection(c)
            firstLastCurrency = adapter1.l[c]
            binding.buyPrice.text = adapter1.l[c].nbu_buy_price.toString() + " UZS"
            binding.salePrice.text = adapter1.l[c].nbu_cell_price.toString() + " UZS"

            binding.valueEt.addTextChangedListener(etListener1)

            binding.symbol.text = Currencies.map[adapter1.l[c].code]

            binding.result.text = "0.0 ${Currencies.map[adapter2.l[0].code]}"

            binding.secondCountrySpinner.onItemSelectedListener = secondSpinnerListener

            binding.firstCountrySpinner.onItemSelectedListener = firstSpinnerListener
        }
        return binding.root
    }

    private val firstSpinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            binding.buyPrice.text =
                if (adapter1.l[p2].nbu_buy_price != null) adapter1.l[p2].nbu_buy_price.toString() + " UZS" else "mavjud emas"
            binding.salePrice.text =
                if (adapter1.l[p2].nbu_cell_price != null) adapter1.l[p2].nbu_cell_price.toString() + " UZS" else "mavjud emas"
            binding.symbol.text = Currencies.map[adapter1.l[p2].code]
            binding.result.text = calculate(
                validate(binding.valueEt.text.toString()),
                adapter1.l[p2].cb_price!!,
                adapter2.l[binding.secondCountrySpinner.selectedItemPosition].cb_price!!,
                adapter2.l[binding.secondCountrySpinner.selectedItemPosition].code
            )
            if (!isClick) {
                deleteAndAdd(
                    adapter1.l[p2],
                    firstLastCurrency,
                    secondLastCurrency,
                    adapter2,
                    binding.secondCountrySpinner
                )
                firstLastCurrency = adapter1.l[p2]
            }
            isClick = false
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {

        }


    }

    private fun deleteAndAdd(
        deleteCurrency: Currency,
        lastCurrency: Currency?,
        findCurrency: Currency?,
        adapter: CountrySpinnerItemAdapter,
        spinner: Spinner
    ) {
        adapter.l.add(map[lastCurrency]!!, lastCurrency!!)
        adapter.l.remove(deleteCurrency)
        adapter.notifyDataSetChanged()
        spinner.setSelection(adapter.l.indexOf(findCurrency))
    }

    private val secondSpinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            binding.result.text = calculate(
                validate(binding.valueEt.text.toString()),
                adapter1.l[binding.firstCountrySpinner.selectedItemPosition].cb_price!!,
                adapter2.l[p2].cb_price!!,
                adapter2.l[p2].code
            )
            if (!isClick) {
                deleteAndAdd(
                    adapter2.l[p2],
                    secondLastCurrency,
                    firstLastCurrency,
                    adapter1,
                    binding.firstCountrySpinner
                )
                secondLastCurrency = adapter2.l[p2]
            }
            isClick = false
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validate(s: String): String {
        var res = s
        if (s.isNotEmpty() && s[0] == '.') res = "0$s";
        return res
    }

    private fun calculate(num: String, price1: Double, price2: Double, code: String): String {
        if (num == "" || num == "0.") return "0.0 ${Currencies.map[code]}"
        var resStr = validate(num)
        if (resStr.isNotEmpty() && resStr.last() == '.') resStr += '0';
        var res: Double = resStr.toDouble()
        res *= price1 / price2
        return String.format("%.2f", res) + " ${Currencies.map[code]}"
    }


    private val etListener1 = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            var validate = p0.toString()
            if (!isValid(p0?.toString()!!)) {
                validate = validate(p0.toString())
                binding.valueEt.setText(validate)
                binding.valueEt.setSelection(validate.length)
            }
            val index1 = binding.firstCountrySpinner.selectedItemPosition
            val index2 = binding.secondCountrySpinner.selectedItemPosition
            binding.result.text =
                calculate(
                    validate,
                    adapter1.l[index1].cb_price!!,
                    adapter2.l[index2].cb_price!!,
                    adapter2.l[index2].code
                )
        }

    }

    private val symbolListener = ViewTreeObserver.OnGlobalLayoutListener {
        try {
            binding.valueEt.setPadding(
                binding.symbol.width +
                        10 * resources.displayMetrics.density.toInt(),
                binding.valueEt.paddingTop,
                binding.valueEt.paddingRight,
                binding.valueEt.paddingBottom
            )
        } catch (e: NullPointerException) {
        }
    }

    private fun isValid(s: String): Boolean {
        if (s.length > 1 && s.last() == '.') return true
        if (s == "0.") return true
        if (s.isNotEmpty() && (s.first() == '.' || s.last() == '.')) return false
        return true
    }

    private fun searchCode(l: List<Currency>, c: String): Int {
        var res = 0
        for (i in l.indices) {
            if (l[i].code == c) res = i;
        }
        return res
    }

}