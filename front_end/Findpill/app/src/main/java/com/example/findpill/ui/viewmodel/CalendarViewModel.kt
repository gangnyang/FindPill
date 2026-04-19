package com.example.findpill.ui.viewmodel

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.findpill.data.CalendarKeys
import com.example.findpill.data.model.PillInfo
import com.example.findpill.data.repository.GetPillById
import com.example.findpill.data.settingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    application: Application,
    private val getPillById: GetPillById
) : AndroidViewModel(application) {
    private val dataStore = application.settingsDataStore

    val morning: Flow<Set<String>> = dataStore.data.map {it[CalendarKeys.MORNING] ?: emptySet()}
    val afternoon: Flow<Set<String>> = dataStore.data.map {it[CalendarKeys.AFTERNOON] ?: emptySet()}
    val night: Flow<Set<String>> = dataStore.data.map {it[CalendarKeys.NIGHT] ?: emptySet()}

    private val _morningPills = MutableStateFlow<List<PillInfo>>(emptyList())
    val morningPills: StateFlow<List<PillInfo>> = _morningPills

    private val _afternoonPills = MutableStateFlow<List<PillInfo>>(emptyList())
    val afternoonPills: StateFlow<List<PillInfo>> = _afternoonPills

    private val _nightPills = MutableStateFlow<List<PillInfo>>(emptyList())
    val nightPills: StateFlow<List<PillInfo>> = _nightPills

    init {
        loadAllPlans()
    }

    fun loadAllPlans() {
        viewModelScope.launch {
            launch {
                morning.collect { ids ->
                    _morningPills.value = ids.parallelFetch()
                }
            }
            launch {
                afternoon.collect { ids ->
                    _afternoonPills.value = ids.parallelFetch()
                }
            }
            launch {
                night.collect { ids ->
                    _nightPills.value = ids.parallelFetch()
                }
            }
        }
    }

    private suspend fun Set<String>.parallelFetch(): List<PillInfo> = kotlinx.coroutines.coroutineScope {
        map{
            async{
                try{
                    getPillById.getPillById(it)
                }catch(_: Exception){
                    null
                }
            }
        }.awaitAll().filterNotNull()
    }

    fun addPlan(time:String, id:String) = viewModelScope.launch(){
        val key = when (time){
            "morning" -> CalendarKeys.MORNING
            "afternoon" -> CalendarKeys.AFTERNOON
            "night" -> CalendarKeys.NIGHT
            else -> return@launch
        }

        dataStore.edit { prefs ->
            val current = prefs[key] ?: emptySet()
            prefs[key] = current + id
        }
    }

    fun removePlan(time:String, id:String) = viewModelScope.launch(){
        val key = when(time){
            "morning" -> CalendarKeys.MORNING
            "afternoon" -> CalendarKeys.AFTERNOON
            "night" -> CalendarKeys.NIGHT
            else -> return@launch
        }

        dataStore.edit { prefs ->
            val current = prefs[key] ?: emptySet()
            prefs[key] = current - id
        }
    }

    fun clearAllPlan() = viewModelScope.launch {
        dataStore.edit { prefs ->
            prefs[CalendarKeys.MORNING] = emptySet()
            prefs[CalendarKeys.AFTERNOON] = emptySet()
            prefs[CalendarKeys.NIGHT] = emptySet()
        }
    }
}