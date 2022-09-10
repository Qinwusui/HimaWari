package com.anki.hima.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anki.hima.viewmodel.MainViewModel

@ExperimentalMaterial3Api
@Composable
fun MessageScreen(mainViewModel: MainViewModel= viewModel()) {
    val listState = rememberLazyListState()
    Scaffold(floatingActionButton = {}, floatingActionButtonPosition = FabPosition.End) {
        LazyColumn(state = listState, contentPadding = it){

        }
    }
}