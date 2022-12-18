package com.anki.hima.ui.activity

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anki.hima.ui.screens.NavScreen
import com.anki.hima.ui.screens.chat.ChatScreen
import com.anki.hima.ui.screens.user.UserScreen
import com.anki.hima.ui.screens.user.UserSearchScreen
import com.anki.hima.ui.theme.HimaWariTheme
import com.anki.hima.viewmodel.MainViewModel


class MainActivity : ComponentActivity() {

    private val mainViewModel by viewModels<MainViewModel>()
    private val request =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkPermission()) {
            requestPermission()
        }

        setContent {
            HimaWariTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = NavScreen.MainView.route,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable(NavScreen.MainView.route) {
                            NavScreen(mainViewModel = mainViewModel, navController = navController)
                        }
                        composable(NavScreen.ChatView.route) {
                            ChatScreen(mainViewModel = mainViewModel, navController = navController)
                        }
                        composable(NavScreen.UserView.route) {
                            UserScreen(mainViewModel = mainViewModel, navController = navController)
                        }
                        composable(NavScreen.UserSearchView.route) {
                            UserSearchScreen(
                                mainViewModel = mainViewModel,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
            val result1 =
                ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        val permissions = arrayOf(
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
        )
        request.launch(permissions)
        if (SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data = Uri.parse("package:${applicationContext.packageName}")
            startActivity(intent)
        }

    }

}