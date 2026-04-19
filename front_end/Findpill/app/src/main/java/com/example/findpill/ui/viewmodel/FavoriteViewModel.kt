package com.example.findpill.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.findpill.data.FavoriteKeys
import com.example.findpill.data.model.PillInfo
import com.example.findpill.data.repository.GetPillById
import com.example.findpill.data.settingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
        application: Application,
    private val getPillById: GetPillById
    ) : AndroidViewModel(application) {
    private val dataStore = application.settingsDataStore

    val favorites: Flow<Set<String>> = dataStore.data
        .map {
            it[FavoriteKeys.FAVORITES] ?: emptySet()
        }

    private val _pillList = MutableStateFlow<List<PillInfo>>(emptyList())
    val pillList: StateFlow<List<PillInfo>> = _pillList

    private var favoritesJob: Job? = null
    fun loadFavoritePills() {
        favoritesJob?.cancel()
        favoritesJob = viewModelScope.launch {
            favorites.collect { ids ->
                android.util.Log.d("FavoriteViewModel", "⭐ 즐겨찾기 ID 수집됨: $ids")

                val result = coroutineScope {
                    ids.map { id ->
                        async {
                            try {
                                android.util.Log.d("FavoriteViewModel", "📡 ID $id 에 대한 알약 정보 요청 중")
                                val pill = getPillById.getPillById(id)
                                if (pill == null) {
                                    android.util.Log.w("FavoriteViewModel", "⚠️ ID $id 에 대한 응답이 null임")
                                } else {
                                    android.util.Log.d("FavoriteViewModel", "✅ ID $id 에 대한 알약 정보 수신 완료: ${pill.name}")
                                }
                                pill
                            } catch (e: Exception) {
                                android.util.Log.e("FavoriteViewModel", "❌ ID $id 에 대한 요청 중 예외 발생: ${e.message}", e)
                                null
                            }
                        }
                    }.awaitAll().filterNotNull()
                }

                android.util.Log.d("FavoriteViewModel", "🎯 최종 수신된 알약 개수: ${result.size}")
                _pillList.value = result
            }
        }
    }

    suspend fun loadPillsByIds(ids: List<Int>): List<PillInfo> = coroutineScope {
        ids.map { id ->
            async {
                try {
                    Log.d("FavoriteViewModel", "$id 에 대해 요청 시도")
                    val pill = getPillById.getPillById(id.toString())
                    Log.d("FavoriteViewModel", "ID $id 결과: $pill")
                    pill
                } catch (e: Exception) {
                    android.util.Log.e("FavoriteViewModel", "ID $id 조회 실패", e)
                    null
                }
            }
        }.awaitAll().filterNotNull()
    }


    fun addFavorite(id: String) = viewModelScope.launch{
        dataStore.edit { prefs ->
            val current = prefs[FavoriteKeys.FAVORITES] ?: emptySet()
            prefs[FavoriteKeys.FAVORITES] = current + id
        }

        loadFavoritePills()
    }

    fun removeFavorite(id:String) = viewModelScope.launch{
        dataStore.edit { prefs ->
            val current = prefs[FavoriteKeys.FAVORITES] ?: emptySet()
            prefs[FavoriteKeys.FAVORITES] = current - id
        }
        loadFavoritePills()
    }

    fun clearFavorite() = viewModelScope.launch {
        dataStore.edit { prefs ->
            prefs[FavoriteKeys.FAVORITES] = emptySet()
        }
    }
}