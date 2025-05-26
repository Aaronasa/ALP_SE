package com.example.alp_se

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.alp_se.Repository.ItineraryRepository
import com.example.alp_se.Repository.NetworkItineraryRepository
import com.example.alp_se.Service.ItineraryService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val itineraryRepository: ItineraryRepository
}

class DefaultAppContainer(
    private val userDataStore: DataStore<Preferences>
): AppContainer {
    // change it to your own local ip please
    private val baseUrl = "http://172.20.10.3:3000/"
    // RETROFIT SERVICE
    // delay object creation until needed using lazy
    private val itineraryRetrofitService: ItineraryService by lazy {
        val retrofit = initRetrofit()

        retrofit.create(ItineraryService::class.java)
    }


    // REPOSITORY INIT
    override val itineraryRepository: ItineraryRepository by lazy {
        NetworkItineraryRepository(itineraryRetrofitService)
    }

    private fun initRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = (HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
        client.addInterceptor(logging)

        return Retrofit
            .Builder()
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .client(client.build())
            .baseUrl(baseUrl)
            .build()
    }
}
