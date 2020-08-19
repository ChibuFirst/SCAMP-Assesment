package com.chibufirst.scampassesment

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : AppCompatActivity(), SearchView.OnQueryTextListener,
    MenuItem.OnActionExpandListener {

    companion object {
        lateinit var recyclerSearch: RecyclerView
        lateinit var textInfo: TextView
        var isOnline: Boolean = false
        lateinit var countriesArrayList: ArrayList<Statistics>
        lateinit var adapter: CountriesRecyclerAdapter
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        title = ""

        textInfo = findViewById(R.id.text_info)

        recyclerSearch = findViewById(R.id.recycler_search)
        recyclerSearch.setHasFixedSize(true)
        recyclerSearch.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        countriesArrayList = ArrayList()

        if (isOnline) {
            textInfo.text = getString(R.string.please_wait)
            if (countriesArrayList.size > 0) {
                textInfo.visibility = View.GONE
                recyclerSearch.visibility = View.VISIBLE
            }
        }
        else {
            textInfo.text = getString(R.string.no_internet)
            textInfo.visibility = View.VISIBLE
            recyclerSearch.visibility = View.GONE
        }

        try {
            val statsURL: URL = ApiUtil.buildUrl("summary")
            SearchCountriesTask().execute(statsURL)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

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
    }

    private suspend fun connectionAvailable (isConnected: Boolean) {
        withContext(Dispatchers.Main) {
            if (isConnected) {
                isOnline = true
                textInfo.text = getString(R.string.please_wait)
                if (countriesArrayList.size > 0) {
                    textInfo.visibility = View.GONE
                    recyclerSearch.visibility = View.VISIBLE
                }

                try {
                    val statsURL: URL = ApiUtil.buildUrl("summary")
                    SearchCountriesTask().execute(statsURL)
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else {
                textInfo.text = getString(R.string.no_internet)
                textInfo.visibility = View.VISIBLE
                recyclerSearch.visibility = View.GONE
                isOnline = false
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        val menuItem: MenuItem = menu?.findItem(R.id.action_search) ?: return false
        menuItem.expandActionView()
        menuItem.setOnActionExpandListener(this)
        val searchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (isOnline) {
            if (countriesArrayList.size > 0) {
                val userInput = newText?.toLowerCase(Locale.ROOT)
                val newList: ArrayList<Statistics> = ArrayList()
                for (country in countriesArrayList) {
                    if (userInput?.let { country.countries.toLowerCase(Locale.ROOT).contains(it) }!!) {
                        newList.add(country)
                    }
                }
                adapter.updateList(newList)
            }
            else {
                Toast.makeText(this, "Please wait...", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        this.onBackPressed()
        return true
    }

    class SearchCountriesTask : AsyncTask<URL, Void, String>() {

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
            countriesArrayList = ApiUtil.getCountriesStatistics(result)
            if (countriesArrayList.size > 0) {
                textInfo.visibility = View.GONE
                recyclerSearch.visibility = View.VISIBLE

                adapter = CountriesRecyclerAdapter(countriesArrayList)
                recyclerSearch.adapter = adapter
            }
            else {
                textInfo.visibility = View.VISIBLE
                recyclerSearch.visibility = View.GONE
            }
        }

    }
}
