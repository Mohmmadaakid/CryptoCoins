package com.example.mycryptoapplication.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.mycryptoapplication.adapter.MarketAdapter
import com.example.mycryptoapplication.apis.ApiInterface
import com.example.mycryptoapplication.apis.ApiUtilities
import com.example.mycryptoapplication.databinding.FragmentMarketBinding
import com.example.mycryptoapplication.model.CryptoCurrency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MarketFragment : Fragment() {
    private lateinit var binding: FragmentMarketBinding

    private lateinit var list: List<CryptoCurrency>
    private lateinit var adapter: MarketAdapter




        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            binding = FragmentMarketBinding.inflate(layoutInflater)

            list= listOf()

            adapter= MarketAdapter(requireContext(),list,"market")
            binding.currencyRecyclerView.adapter=adapter

            lifecycleScope.launch(Dispatchers.IO) {
                val res=ApiUtilities.getInstance().create(ApiInterface::class.java).getMarketData()

                if (res.body()!=null)
                {
                    withContext(Dispatchers.Main){
                        list=res.body()!!.data.cryptoCurrencyList

                        adapter.updateData(list)
                        binding.spinKitView.visibility=GONE
                    }
                }
            }

            searchCoin()



            return binding.root
        }

    lateinit var searchText:String
    private fun searchCoin() {

        binding.searchEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                searchText=s.toString().toLowerCase()
                updateRecyclerView()
            }
        })



    }

    private fun updateRecyclerView() {
        val data=ArrayList<CryptoCurrency>()

        for (item in list)
        {
            val coinName=item.name.lowercase(Locale.getDefault())
            val coinSymbol=item.symbol.lowercase(Locale.getDefault())

            if (coinName.contains(searchText)||coinSymbol.contains(searchText))
            {
                data.add(item)
            }

        }
        adapter.updateData(data)
    }


}
