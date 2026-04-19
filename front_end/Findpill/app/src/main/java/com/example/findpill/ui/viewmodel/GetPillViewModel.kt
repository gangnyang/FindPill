package com.example.findpill.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findpill.data.model.PillInfo
import com.example.findpill.data.repository.GetPillById
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetPillViewModel @Inject constructor(
    private val repository: GetPillById
): ViewModel() {
    private val _pillInfo = MutableStateFlow<PillInfo?>(null)
    val pillInfo: StateFlow<PillInfo?> = _pillInfo

    fun fetchPillInfoById(id: String){
        viewModelScope.launch{
            try{
                val result = repository.getPillById(id)
                if (result == null) {
                    Log.e("GetPillViewModel", "Result is null (404 or empty body?)")
                } else {
                    Log.d("GetPillViewModel", "Successfully fetched pill info: $result")
                }
                _pillInfo.value = result
            }catch(e:Exception){
                Log.e("GetPillViewModel", "Exception during fetch: ${e.message}", e)
                _pillInfo.value = null
            }
        }
    }
}