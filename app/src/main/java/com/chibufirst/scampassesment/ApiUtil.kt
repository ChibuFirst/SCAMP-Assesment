package com.chibufirst.scampassesment

import android.net.Uri
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class ApiUtil private constructor() {

    companion object {
        private const val BASE_API_URL = "https://api.covid19api.com"
        private const val GLOBAL = "Global"
        private const val COUNTRIES = "Countries"
        private const val COUNTRY = "Country"
        private const val NEW_CONFIRMED = "NewConfirmed"
        private const val TOTAL_CONFIRMED = "TotalConfirmed"
        private const val NEW_DEATHS = "NewDeaths"
        private const val TOTAL_DEATHS = "TotalDeaths"
        private const val NEW_RECOVERED = "NewRecovered"
        private const val TOTAL_RECOVERED = "TotalRecovered"

        fun buildUrl(title: String): URL {
            lateinit var url: URL
            val uri: Uri = Uri.parse(BASE_API_URL)
                .buildUpon()
                .appendPath(title)
                .build()
            try {
                url = URL(uri.toString())
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            return url
        }

        @Throws(IOException::class)
        fun getJson(url: URL): String? {
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            return try {
                val stream: InputStream = connection.inputStream
                val scanner = Scanner(stream)
                scanner.useDelimiter("\\A")
                val hasData: Boolean = scanner.hasNext()
                if (hasData) {
                    scanner.next()
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.d("Error", e.toString())
                null
            } finally {
                connection.disconnect()
            }
        }

        fun getGlobalStatistics(json: String): Statistics {
            lateinit var statistics: Statistics
            statistics = Statistics("", "", "", "", "", "", "")
            try {
                val jsonStatistics = JSONObject(json)
                val infoJSON: JSONObject = jsonStatistics.getJSONObject(GLOBAL)
                statistics = Statistics("",
                    infoJSON.getString(NEW_CONFIRMED),
                    infoJSON.getString(TOTAL_CONFIRMED),
                    infoJSON.getString(NEW_DEATHS),
                    infoJSON.getString(TOTAL_DEATHS),
                    infoJSON.getString(NEW_RECOVERED),
                    infoJSON.getString(TOTAL_RECOVERED))
            }
            catch (e: JSONException) {
                e.printStackTrace()
            }
            return statistics
        }

        fun getCountriesStatistics(json: String): ArrayList<Statistics> {
            val stats: ArrayList<Statistics> = ArrayList()
            try {
                val statsJSON = JSONObject(json)
                val arrayJSON: JSONArray = statsJSON.getJSONArray(COUNTRIES)
                for (i in 0 until arrayJSON.length()) {
                    val element = Statistics(
                        arrayJSON.getJSONObject(i).getString(COUNTRY),
                        arrayJSON.getJSONObject(i).getString(NEW_CONFIRMED),
                        arrayJSON.getJSONObject(i).getString(TOTAL_CONFIRMED),
                        arrayJSON.getJSONObject(i).getString(NEW_DEATHS),
                        arrayJSON.getJSONObject(i).getString(TOTAL_DEATHS),
                        arrayJSON.getJSONObject(i).getString(NEW_RECOVERED),
                        arrayJSON.getJSONObject(i).getString(TOTAL_RECOVERED)
                    )
                    stats.add(element)
                }
            }
            catch (e: JSONException) {
                e.printStackTrace()
            }
            return stats
        }

    }

}