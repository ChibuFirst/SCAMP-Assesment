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
        private var textConfirmedNew: TextView = itemView.findViewById(R.id.text_confirmed_new)
        private var textConfirmedTotal: TextView = itemView.findViewById(R.id.text_confirmed_total)
        private var textDeathsNew: TextView = itemView.findViewById(R.id.text_deaths_new)
        private var textDeathsTotal: TextView = itemView.findViewById(R.id.text_deaths_total)
        private var textRecoveredNew: TextView = itemView.findViewById(R.id.text_recovered_new)
        private var textRecoveredTotal: TextView = itemView.findViewById(R.id.text_recovered_total)

        fun bind(statistics: Statistics) {
            textCountries.text = statistics.countries
            textConfirmedNew.text = "New Confirmed: \n${statistics.new_confirmed}"
            textConfirmedTotal.text = "Total Confirmed: \n${statistics.total_confirmed}"
            textDeathsNew.text = "New Deaths: \n${statistics.new_deaths}"
            textDeathsTotal.text = "Total Deaths: \n${statistics.total_deaths}"
            textRecoveredNew.text = "New Recovered: \n${statistics.new_recovered}"
            textRecoveredTotal.text = "Total Recovered: \n${statistics.total_recovered}"
        }
    }
}