package com.kaeonx.moneymanager.xerepository.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.exchangeratesapi.io/"

// You can read more here: https://www.valueof.io/blog/writing-a-custom-moshi-adapter
//private val moshi = Moshi.Builder()
//    .add(KotlinJsonAdapterFactory())
//    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create())
//    .addConverterFactory(MoshiConverterFactory.create(moshi))
//    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()


// Singleton, since retrofit.create() is expensive
object XENetwork {
    val retrofitService: XENetworkService by lazy { retrofit.create(XENetworkService::class.java) }
}

// Named as such since this defines the API our Retrofit service creates
interface XENetworkService {
    @GET("latest")
//    fun getBuildings(): Call<String>
    // Moshi will do the necessary conversion for us
    suspend fun fetchNetworkXEContainer(@Query("base") baseCurrency: String): NetworkXEContainer
}