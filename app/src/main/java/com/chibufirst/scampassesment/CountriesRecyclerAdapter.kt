package com.chibufirst.scampassesment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CountriesRecyclerAdapter(countriesArrayList: ArrayList<Statistics>) :
    RecyclerView.Adapter<CountriesRecyclerAdapter.CountriesViewHolder>() {

    private var mCountriesArrayList: ArrayList<Statistics> = countriesArrayList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountriesViewHolder {
        return CountriesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.countries_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return mCountriesArrayList.size
    }

    override fun onBindViewHolder(holder: CountriesViewHolder, position: Int) {
        val statistics: Statistics = mCountriesArrayList[position]
        holder.bind(statistics)
    }

    class CountriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var textCountries: TextView = itemView.findViewById(R.id.text_countries)
        private var textConfirmed: TextView = itemView.findViewById(R.id.text_confirmed)
        private var textDeaths: TextView = itemView.findViewById(R.id.text_deaths)
        private var textRecovered: TextView = itemView.findViewById(R.id.text_recovered)

        fun bind(statistics: Statistics) {
            textCountries.text = statistics.countries
            val confirmed = "Confirmed: ${statistics.total_confirmed}"
            textConfirmed.text = confirmed
            val deaths = "Deaths: ${statistics.total_deaths}"
            textDeaths.text = deaths
            val recovered = "Recovered: ${statistics.total_recovered}"
            textRecovered.text = recovered
        }
    }

    fun updateList(newArrayList: ArrayList<Statistics>) {
        mCountriesArrayList = ArrayList()
        mCountriesArrayList.addAll(newArrayList)
        notifyDataSetChanged()
    }
}