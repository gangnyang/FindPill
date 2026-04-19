package com.example.findpill.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findpill.data.model.InfoSearchRequest
import com.example.findpill.data.model.PillInfo
import com.example.findpill.data.model.PillSearchResponse
import com.example.findpill.data.repository.InfoSearchRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InfoSearchViewModel @Inject constructor(
    private val repository: InfoSearchRepo
) : ViewModel() {
    private val _result = MutableStateFlow(PillSearchResponse(status="INIT", pill = emptyList()))
    val result: StateFlow<PillSearchResponse> = _result

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun search(request: InfoSearchRequest, onSuccess: () -> Unit){
        viewModelScope.launch{
            _loading.value = true
            _error.value = null

            Log.d("InfoSearchViewModel", "알약 검색 요청: $request")

            try {
                val response = repository.pillInfoSearch(request)

                if (response != null) {
                    _result.value = response
                    Log.d("InfoSearchViewModel", "검색 성공: ${response.pill.size}개")
                    onSuccess()
                } else {
                    _error.value = "검색 결과가 없습니다."
                    Log.w("InfoSearchViewModel", "검색 결과 없음 또는 null. request=$request")
                }

            } catch (e: Exception) {
                _error.value = "서버 요청 중 오류 발생"
                Log.e("InfoSearchViewModel", "API 호출 중 예외 발생: ${e.message}", e)
            } finally {
                _loading.value = false
            }
        }
    }
}