package com.example.myapplication

import android.Manifest
import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavigationGraph(navController = navController)
//            checkLocationPermissionAndStartService(navController = navController)
//            MainScreen(navController = navController)
//            AddSchedule(navController = navController)
            ScheduleList(navController = navController)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startLocationService()
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
            }
        }

    private fun checkLocationPermissionAndStartService() {
        when {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                startLocationService()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private val isLocationServiceRunning: Boolean
        get() {
            val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
                if (LocationService::class.java.name == service.service.className) {
                    if (service.foreground) {
                        return true
                    }
                }
            }
            return false
        }

    private fun startLocationService() {
        if (!isLocationServiceRunning) {
            val intent = Intent(applicationContext, LocationService::class.java)
            intent.action = Constants.ACTION_START_LOCATION_SERVICE
            startService(intent)
            Toast.makeText(this, "Location service started", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopLocationService() {
        if (isLocationServiceRunning) {
            val intent = Intent(applicationContext, LocationService::class.java)
            intent.action = Constants.ACTION_STOP_LOCATION_SERVICE
            startService(intent)
            Toast.makeText(this, "Location service stopped", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(navController = navController)
        }
        composable("addSchedule") { // <- 여기로 이동
            AddSchedule(navController = navController)
        }
        composable("ScheduleList") { // <- 여기로 이동
            ScheduleList(navController = navController)
        }
        composable("System") { // <- 여기로 이동
            System(navController = navController)
        }
    }
}

@Composable
fun DrawerContent(onCloseDrawer: () -> Unit, navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF505050))
            .padding(16.dp)
    ) {
        Text(
            text = "홈",
            color = Color(0xFFFFFFFF),
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onCloseDrawer()
                    navController.navigate("main")
                }
                .padding(vertical = 8.dp)

        )
        Text(
            text = "일정",
            color = Color(0xFFFFFFFF),
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onCloseDrawer()
                    navController.navigate("ScheduleList")
                }
                .padding(vertical = 8.dp)

        )
        Text(
            text = "일정 추가",
            color = Color(0xFFFFFFFF),
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onCloseDrawer()
                    navController.navigate("addSchedule")
                }
                .padding(vertical = 8.dp)
        )
        Text(
            text = "설정",
            color = Color(0xFFFFFFFF),
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onCloseDrawer()
                    navController.navigate("System")
                }
                .padding(vertical = 8.dp)
        )
    }
}