package com.anki.hima.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anki.hima.utils.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val repo: Repository = Repository) : ViewModel() {
    //维护一个消息列表
    private val _receiveData = MutableStateFlow(mutableStateListOf<Repository.OutGoingData>())
    val receiveData = _receiveData.asStateFlow()

    init {
        startJob()
    }

    fun sendMsg(msg: String) {
        viewModelScope.launch {
            repo.sendMsg(msg)
        }
    }

    fun getNickName(): String = repo.getUserInfo(false)
    private fun startJob() {
        viewModelScope.launch {
            repo.initWsSession()
            //ReceiveMsg
            repo.receive().collect {
                _receiveData.value.add(it)
            }
        }
    }

    fun stopJob() {
        repo.disConnect()
    }


    fun exitChatRoom() {
        viewModelScope.launch {

        }
    }
}