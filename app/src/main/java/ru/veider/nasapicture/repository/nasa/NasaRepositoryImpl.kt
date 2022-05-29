package ru.veider.nasapicture.repository.nasa

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.veider.nasapicture.BuildConfig
import ru.veider.nasapicture.const.TAG
import java.io.IOException

class NasaRepositoryImpl : NasaRepository {

    private val baseUrl = "https://api.nasa.gov/"

    private val podApi = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .client(createOkHttpClient(APODInterceptor()))
        .build()
        .create(NasaAPI::class.java)

    private fun createOkHttpClient(interceptor: Interceptor): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }
        return okHttpClient.build()
    }

    private inner class APODInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            Log.d(TAG, "NASA: "+chain.request().toString()+"\n")
            return chain.proceed(chain.request())
        }
    }

    override suspend fun pod(date:String): PODResponse = podApi.getPicture(BuildConfig.NASA_API_KEY, date)
}