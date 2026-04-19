package com.example.findpill.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findpill.data.SettingKeys
import com.example.findpill.data.settingsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingViewModel(application: Application) : AndroidViewModel(application){
    private val dataStore = application.applicationContext.settingsDataStore

    val isalarmed: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[SettingKeys.ISALARMED] ?: false
        }

    fun setAlarm(enabled: Boolean){
        viewModelScope.launch{
            dataStore.edit { prefs ->
                prefs[SettingKeys.ISALARMED] = enabled
            }
        }
    }
}