package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberDrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@Composable
fun ScheduleList(navController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ScheduleList.add(Schedule("Meeting", "2024-06-17", "14:00", 126.309, Color.Red))
    ScheduleList.add(Schedule("Dentist Appointment", "2024-06-17", "17:00", 126.1389, Color.Blue))
    ScheduleList.add(Schedule("Lunch with John", "2024-06-18", "12:00", 127.64, Color.Green))
    ScheduleList.add(Schedule("Project Deadline", "2024-06-18", "23:00", 127.456, Color.Magenta))
    val groupedScheduleList = ScheduleList.groupBy { it.date }

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(onCloseDrawer = {
                scope.launch { drawerState.close() }
            }, navController = navController)
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        if (drawerState.isOpen) {
                            alpha = 0.5f
                        }
                    }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Schedule ++") },
                            navigationIcon = {
                                IconButton(onClick = {
                                    scope.launch {
                                        if (drawerState.isOpen) {
                                            drawerState.close()
                                        } else {
                                            drawerState.open()
                                        }
                                    }
                                }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                                }
                            }
                        )
                    }
                ) { paddingValues ->
                    ScheduleListScreen(modifier = Modifier.padding(paddingValues), groupedScheduleList)
                }
            }
        }
    )
}

@Composable
fun ScheduleListScreen(modifier: Modifier = Modifier, groupedScheduleList:Map<String,List<Schedule>>) {
    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        groupedScheduleList.forEach { (date, schedules) ->
            Text(
                text = date,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            schedules.sortedBy { it.time }
            for(i:Int in 0..<schedules.size) DailySchedule(schedules[i])
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

@Composable
fun DailySchedule(schedule: Schedule){
    Row(modifier = Modifier.padding(vertical = 10.dp)){
        Box(modifier = Modifier
            .size(20.dp)
            .background(schedule.color, CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Row {
            Text(
                text = schedule.time,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = schedule.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
    Spacer(modifier = Modifier.height(15.dp))
}