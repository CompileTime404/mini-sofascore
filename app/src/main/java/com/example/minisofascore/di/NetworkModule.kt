package com.example.minisofascore.di

import android.content.Context
import com.example.minisofascore.data.api.SofascoreApiService
import com.example.minisofascore.data.model.CardIncident
import com.example.minisofascore.data.model.GoalIncident
import com.example.minisofascore.data.model.Incident
import com.example.minisofascore.data.model.PeriodIncident
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory.provideContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.subclass
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.OptionalConverterFactory
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://academy-backend.sofascore.dev/"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        classDiscriminator = "type"
        serializersModule = SerializersModule {
            polymorphic(Incident::class, CardIncident::class, CardIncident.serializer())
            polymorphic(Incident::class, GoalIncident::class, GoalIncident.serializer())
            polymorphic(Incident::class, PeriodIncident::class, PeriodIncident.serializer())
        }
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
    }


    @Provides
    @Singleton
    fun provideConverterFactory(json: Json): Converter.Factory =
        json.asConverterFactory("application/json".toMediaType())

    @Provides
    @Singleton
    fun provideAuthInterceptor() : Interceptor {
        return Interceptor{ chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer token")
                .build()
            chain.proceed(newRequest)
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        authInterceptor: Interceptor
    ) : OkHttpClient{
        val cache = Cache(File(context.cacheDir, "http_cache"), 10 * 1024 * 1024)
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .cache(cache)
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ) : Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(converterFactory)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideSofascoreApiService(retrofit: Retrofit) : SofascoreApiService{
        return retrofit.create(SofascoreApiService::class.java)
    }
}
















