package com.example.findpill.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
class panelviewModel : ViewModel(){
    private val _showpanel = MutableStateFlow(false)
    val showpanel = _showpanel.asStateFlow()


    private var animated = false

    suspend fun showingpanel(){ // 패널을 띄우는 함수
        if(!animated){
            kotlinx.coroutines.delay(300)
            _showpanel.value = true
            animated = true
        }
    }
}