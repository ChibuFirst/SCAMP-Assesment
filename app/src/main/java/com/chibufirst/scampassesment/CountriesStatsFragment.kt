package com.chibufirst.scampassesment

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class CountriesStatsFragment : Fragment() {

    companion object {
        lateinit var countriesRecyclerView: RecyclerView
        lateinit var progressBar: ProgressBar
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_countries_stats, container, false)

        progressBar = view.findViewById(R.id.progress_bar)
        countriesRecyclerView = view.findViewById(R.id.countries_recycler_view)
        countriesRecyclerView.setHasFixedSize(true)
        countriesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        countriesRecyclerView.visibility = View.GONE

        try {
            val statsURL: URL = ApiUtil.buildUrl("summary")
            CountriesStatsQuery().execute(statsURL)
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
                    val statsURL: URL = ApiUtil.buildUrl("summary")
                    CountriesStatsQuery().execute(statsURL)
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
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
            if (countriesArrayList.size > 0) {
                progressBar.visibility = View.GONE
                countriesRecyclerView.visibility = View.VISIBLE
            }
            else {
                progressBar.visibility = View.VISIBLE
                countriesRecyclerView.visibility = View.GONE
            }
            val adapter = CountriesRecyclerAdapter(countriesArrayList)
            countriesRecyclerView.adapter = adapter
        }

    }

}
