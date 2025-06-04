package com.example.alp_se

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.alp_se.Repository.ItineraryDayRepository
import com.example.alp_se.Repository.ItineraryRepository
import com.example.alp_se.Repository.NetworkItineraryDayRepository
import com.example.alp_se.Repository.NetworkItineraryRepository
import com.example.alp_se.Service.ItineraryDayService
import com.example.alp_se.Service.ItineraryService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val itineraryRepository: ItineraryRepository
    val itineraryDayRepository: ItineraryDayRepository
}

class DefaultAppContainer(
    private val userDataStore: DataStore<Preferences>
) : AppContainer {

    private val baseUrl = "http://10.0.2.2:3000/"

    // Retrofit initialization
    private fun initRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl(baseUrl)
            .build()
    }

    // Services
    private val itineraryRetrofitService: ItineraryService by lazy {
        val retrofit = initRetrofit()
        retrofit.create(ItineraryService::class.java)
    }

    private val itineraryDayRetrofitService: ItineraryDayService by lazy {
        val retrofit = initRetrofit()
        retrofit.create(ItineraryDayService::class.java)
    }

    // Repositories
    override val itineraryRepository: ItineraryRepository by lazy {
        NetworkItineraryRepository(itineraryRetrofitService)
    }

    override val itineraryDayRepository: ItineraryDayRepository by lazy {
        NetworkItineraryDayRepository(itineraryDayRetrofitService)
    }
}
