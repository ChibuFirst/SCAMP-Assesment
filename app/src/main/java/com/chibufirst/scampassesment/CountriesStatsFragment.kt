package com.chibufirst.scampassesment

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.net.URL

class CountriesStatsFragment : Fragment() {

    companion object {
        lateinit var countriesRecyclerView: RecyclerView
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_countries_stats, container, false)

        countriesRecyclerView = view.findViewById(R.id.countries_recycler_view)
        countriesRecyclerView.setHasFixedSize(true)
        countriesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        try {
            val statsURL: URL = ApiUtil.buildUrl("summary")
            CountriesStatsQuery().execute(statsURL)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        return view
    }

    class CountriesStatsQuery : AsyncTask<URL, Void, String>() {

        override fun doInBackground(vararg params: URL?): String {
            val searchURL = params[0]
            var result = ""
            try {
                result = searchURL?.let { ApiUtil.getJson(it) }.toString()
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: String) {
            val countriesArrayList: ArrayList<Statistics> = ApiUtil.getCountriesStatistics(result)
            val adapter = CountriesRecyclerAdapter(countriesArrayList)
            countriesRecyclerView.adapter = adapter
        }

    }

}
