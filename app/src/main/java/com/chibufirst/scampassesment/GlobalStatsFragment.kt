package com.chibufirst.scampassesment

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.androidnetworking.AndroidNetworking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        lateinit var progressNewConfirmed: ProgressBar
        lateinit var progressTotalConfirmed: ProgressBar
        lateinit var progressNewDeaths: ProgressBar
        lateinit var progressTotalDeaths: ProgressBar
        lateinit var progressNewRecovered: ProgressBar
        lateinit var progressTotalRecovered: ProgressBar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidNetworking.initialize(context)
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_global_stats, container, false)

        globalStatistics = Statistics("", "", "", "", "", "", "")

        textNewConfirmed = view.findViewById(R.id.text_new_confirmed)
        textTotalConfirmed = view.findViewById(R.id.text_total_confirmed)
        textNewDeaths = view.findViewById(R.id.text_new_deaths)
        textTotalDeaths = view.findViewById(R.id.text_total_deaths)
        textNewRecovered = view.findViewById(R.id.text_new_recovered)
        textTotalRecovered = view.findViewById(R.id.text_total_recovered)

        progressNewConfirmed =view.findViewById(R.id.progress_new_confirmed)
        progressTotalConfirmed =view.findViewById(R.id.progress_total_confirmed)
        progressNewDeaths =view.findViewById(R.id.progress_new_deaths)
        progressTotalDeaths =view.findViewById(R.id.progress_total_deaths)
        progressNewRecovered =view.findViewById(R.id.progress_new_recovered)
        progressTotalRecovered =view.findViewById(R.id.progress_total_recovered)

        try {
            val statsUrl: URL? = ApiUtil.buildUrl("summary")
            StatisticsQueryTask().execute(statsUrl)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val builder: NetworkRequest.Builder = NetworkRequest.Builder()
            connectivityManager.registerNetworkCallback(
                builder.build(),
                object : ConnectivityManager.NetworkCallback() {

                    override fun onAvailable(network: Network) {
                        lifecycleScope.launch {
                            connectionAvailable(true)
                        }
                    }

                    override fun onLost(network: Network) {
                        lifecycleScope.launch {
                            connectionAvailable(false)
                        }
                    }
                }
            )
        }

        return view
    }

    private suspend fun connectionAvailable (isConnected: Boolean) {
        withContext(Dispatchers.Main) {
            if (isConnected) {
                try {
                    val statsUrl: URL? = ApiUtil.buildUrl("summary")
                    StatisticsQueryTask().execute(statsUrl)
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
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

            if (globalStatistics.new_confirmed.isNotEmpty() || globalStatistics.total_confirmed.isNotEmpty() || 
                globalStatistics.new_deaths.isNotEmpty() || globalStatistics.total_deaths.isNotEmpty() || 
                globalStatistics.new_recovered.isNotEmpty() || globalStatistics.total_recovered.isNotEmpty()) {
                
                setViewsVisible(true)

                textNewConfirmed.text = globalStatistics.new_confirmed
                textTotalConfirmed.text = globalStatistics.total_confirmed
                textNewDeaths.text = globalStatistics.new_deaths
                textTotalDeaths.text = globalStatistics.total_deaths
                textNewRecovered.text = globalStatistics.new_recovered
                textTotalRecovered.text = globalStatistics.total_recovered
            }
            else {
                setViewsVisible(false)
            }
        }

        private fun setViewsVisible(canSetVisible: Boolean) {
            if (canSetVisible) {
                textNewConfirmed.visibility = View.VISIBLE
                textTotalConfirmed.visibility = View.VISIBLE
                textNewDeaths.visibility = View.VISIBLE
                textTotalDeaths.visibility = View.VISIBLE
                textNewRecovered.visibility = View.VISIBLE
                textTotalRecovered.visibility = View.VISIBLE

                progressNewConfirmed.visibility = View.GONE
                progressTotalConfirmed.visibility = View.GONE
                progressNewDeaths.visibility = View.GONE
                progressTotalDeaths.visibility = View.GONE
                progressNewRecovered.visibility = View.GONE
                progressTotalRecovered.visibility = View.GONE
            }
            else {
                textNewConfirmed.visibility = View.GONE
                textTotalConfirmed.visibility = View.GONE
                textNewDeaths.visibility = View.GONE
                textTotalDeaths.visibility = View.GONE
                textNewRecovered.visibility = View.GONE
                textTotalRecovered.visibility = View.GONE

                progressNewConfirmed.visibility = View.VISIBLE
                progressTotalConfirmed.visibility = View.VISIBLE
                progressNewDeaths.visibility = View.VISIBLE
                progressTotalDeaths.visibility = View.VISIBLE
                progressNewRecovered.visibility = View.VISIBLE
                progressTotalRecovered.visibility = View.VISIBLE
            }
        }

    }
}
