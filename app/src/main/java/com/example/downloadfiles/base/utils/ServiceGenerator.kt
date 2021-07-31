package com.example.downloadfiles.base.utils


import com.example.downloadfiles.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class ServiceGenerator {

    fun <S> createService(
        serviceClass: Class<S>
    ): S {

        val httpClient = OkHttpClient.Builder()
        val builder = Retrofit.Builder()

        httpClient.connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            httpClient.addInterceptor(logging)
        }


        builder.baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())


        val retrofit = builder.build()
        return retrofit.create(serviceClass)
    }

    companion object {
        private const val BASE_URL = "https://run.mocky.io/"
    }
}