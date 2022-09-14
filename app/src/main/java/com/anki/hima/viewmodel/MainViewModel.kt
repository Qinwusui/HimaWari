package com.anki.hima.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anki.hima.utils.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repo: Repository = Repository) : ViewModel() {
    init {
        checkAccount()
    }

    private val _login = MutableStateFlow(false)
    val login = _login.asStateFlow()
    private fun checkAccount() {
        viewModelScope.launch {
            repo.loginBySp().collect {
                _login.value = it
            }
        }
    }

    fun login(uName: String, pwd: String) {
        viewModelScope.launch {
            repo.login(uName, pwd).collect {
                _login.value = it
            }
        }
    }

    private val _sign = MutableStateFlow(false)
    val sign = _sign.asStateFlow()
    fun signIn(uName: String, qq: String, pwd: String) {
        viewModelScope.launch {
            repo.signIn(uName, qq, pwd).collect {
                _sign.value = it
            }
        }
    }

    fun getUserInfo(needQQ: Boolean) = repo.getUserInfo(needQQ)
}