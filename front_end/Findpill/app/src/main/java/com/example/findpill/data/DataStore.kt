package com.example.findpill.data


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore


val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "setting")

object SettingKeys {
    val ISALARMED = booleanPreferencesKey("isalarmed")
}

object FavoriteKeys {
    val FAVORITES = stringSetPreferencesKey("favorites")
}

object CalendarKeys{
    val MORNING = stringSetPreferencesKey("morning")
    val AFTERNOON = stringSetPreferencesKey("afternoon")
    val NIGHT = stringSetPreferencesKey("night")
}