package com.chibufirst.scampassesment

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.androidnetworking.AndroidNetworking
import java.lang.Exception
import java.net.URL

class GlobalStatsFragment : Fragment() {

    companion object {
        lateinit var globalStatistics: Statistics
        lateinit var textNewConfirmed: TextView
        lateinit var textTotalConfirmed: TextView
        lateinit var textNewDeaths: TextView
        lateinit var textTotalDeaths: TextView
        lateinit var textNewRecovered: TextView
        lateinit var textTotalRecovered: TextView

        const val GLOBAL = "Global"
        const val COUNTRIES = "Countries"
        const val NEW_CONFIRMED = "NewConfirmed"
        const val TOTAL_CONFIRMED = "TotalConfirmed"
        const val NEW_DEATHS = "NewDeaths"
        const val TOTAL_DEATHS = "TotalDeaths"
        const val NEW_RECOVERED = "NewRecovered"
        const val TOTAL_RECOVERED = "TotalRecovered"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidNetworking.initialize(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_global_stats, container, false)

        textNewConfirmed = view.findViewById<TextView>(R.id.text_new_confirmed)
        textTotalConfirmed = view.findViewById<TextView>(R.id.text_total_confirmed)
        textNewDeaths = view.findViewById<TextView>(R.id.text_new_deaths)
        textTotalDeaths = view.findViewById<TextView>(R.id.text_total_deaths)
        textNewRecovered = view.findViewById<TextView>(R.id.text_new_recovered)
        textTotalRecovered = view.findViewById<TextView>(R.id.text_total_recovered)

        try {
            val statsUrl: URL? = ApiUtil.buildUrl("summary")
            StatisticsQueryTask().execute(statsUrl)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        return view
    }

    class StatisticsQueryTask : AsyncTask<URL, Void, String>() {

        override fun doInBackground(vararg params: URL?): String {
            val searchURL: URL? = params[0]
            var result = ""
            try {
                result = searchURL?.let { ApiUtil.getJson(it) }.toString()
            }
            catch (e: Exception) {
                Log.e("Error", e.printStackTrace().toString())
            }
            return result
        }

        override fun onPostExecute(result: String) {
            globalStatistics = ApiUtil.getGlobalStatistics(result)
            textNewConfirmed.text = globalStatistics.new_confirmed
            textTotalConfirmed.text = globalStatistics.total_confirmed
            textNewDeaths.text = globalStatistics.new_deaths
            textTotalDeaths.text = globalStatistics.total_deaths
            textNewRecovered.text = globalStatistics.new_recovered
            textTotalRecovered.text = globalStatistics.total_recovered

//            val statsArrayList: ArrayList<Statistics>? = ApiUtil.getCountriesStatistics(result)
        }

    }
}
