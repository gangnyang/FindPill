package com.example.findpill.di

import com.example.findpill.data.api.GetPillByIdApi
import com.example.findpill.data.api.InfoSearchApi
import com.example.findpill.data.repository.GetPillById
import com.example.findpill.data.repository.InfoSearchRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    @Named("beatmania")
    fun provideBeatmaniaRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("http://beatmania.app:8000/") // 여기에 실제 API 주소
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    @Named("findpill")
    fun provideFindpillRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("http://beatmania.app:9998/") // 여기에 실제 API 주소
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideInfoSearchApi(@Named("findpill")retrofit: Retrofit): InfoSearchApi =
        retrofit.create(InfoSearchApi::class.java)

    @Provides
    @Singleton
    fun provideInfoSearch(api: InfoSearchApi): InfoSearchRepo =
        InfoSearchRepo(api)

    @Provides
    @Singleton
    fun provideGetPillByIdApi(@Named("findpill")retrofit: Retrofit): GetPillByIdApi =
        retrofit.create(GetPillByIdApi::class.java)

    @Provides
    @Singleton
    fun provideGetPillById(api: GetPillByIdApi): GetPillById =
        GetPillById(api)
}
