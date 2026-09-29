package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Task
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.MomentumViewModel
import com.example.viewmodel.PostponeChoice
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MomentumViewModel,
    onReset: () -> Unit
) {
    val context = LocalContext.current
    val tasks by viewModel.allTasks.collectAsState()
    val availableMinutes by viewModel.availableMinutes.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()
    val nextBestAction by viewModel.nextBestAction.collectAsState()
    val activeTaskId by viewModel.activeTaskId.collectAsState()
    val rebuiltSchedule by viewModel.rebuiltSchedule.collectAsState()
    val rescuePlan by viewModel.rescuePlan.collectAsState()
    val autopsySuggestion by viewModel.autopsySuggestion.collectAsState()
    val detectedCommitment by viewModel.detectedCommitment.collectAsState()
    val parsedImageTasks by viewModel.parsedImageTasks.collectAsState()

    var currentTab by remember { mutableStateOf("Today") }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showMultimodalModal by remember { mutableStateOf(false) }

    // Listen to ViewModel events (Toasts)
    LaunchedEffect(Unit) {
        viewModel.toastNotification.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.linearGradient(colors = listOf(AccentBlue, NeonViolet))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = "Logo",
                                tint = CoolWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "MOMENTUM",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = CoolWhite
                        )
                    }
                },
                actions = {
                    // AI Status indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(DeepCard, RoundedCornerShape(12.dp))
                            .border(0.5.dp, DeepCardElevated, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isAiThinking) NeonViolet else EmeraldMint)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAiThinking) "THINKING" else "AI SYNCED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAiThinking) NeonViolet else EmeraldMint
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // User Initials Avatar
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DeepCardElevated)
                            .clickable { showSettingsDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "JD",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSlate)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSlate,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == "Today",
                    onClick = { currentTab = "Today" },
                    icon = { Icon(Icons.Default.Today, contentDescription = "Today") },
                    label = { Text("Today") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentBlue,
                        selectedTextColor = AccentBlue,
                        indicatorColor = DeepCardElevated,
                        unselectedIconColor = SlateGray,
                        unselectedTextColor = SlateGray
                    ),
                    modifier = Modifier.testTag("nav_today")
                )
                NavigationBarItem(
                    selected = currentTab == "Tasks",
                    onClick = { currentTab = "Tasks" },
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "Tasks") },
                    label = { Text("Tasks") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentBlue,
                        selectedTextColor = AccentBlue,
                        indicatorColor = DeepCardElevated,
                        unselectedIconColor = SlateGray,
                        unselectedTextColor = SlateGray
                    ),
                    modifier = Modifier.testTag("nav_tasks")
                )
                NavigationBarItem(
                    selected = currentTab == "Timeline",
                    onClick = { currentTab = "Timeline" },
                    icon = { Icon(Icons.Default.Timeline, contentDescription = "Timeline") },
                    label = { Text("Timeline") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentBlue,
                        selectedTextColor = AccentBlue,
                        indicatorColor = DeepCardElevated,
                        unselectedIconColor = SlateGray,
                        unselectedTextColor = SlateGray
                    ),
                    modifier = Modifier.testTag("nav_timeline")
                )
                NavigationBarItem(
                    selected = currentTab == "Insights",
                    onClick = { currentTab = "Insights" },
                    icon = { Icon(Icons.Default.Psychology, contentDescription = "Insights") },
                    label = { Text("Insights") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentBlue,
                        selectedTextColor = AccentBlue,
                        indicatorColor = DeepCardElevated,
                        unselectedIconColor = SlateGray,
                        unselectedTextColor = SlateGray
                    ),
                    modifier = Modifier.testTag("nav_insights")
                )
            }
        },
        containerColor = DarkSlate
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Hero display only visible on main Today / dashboard tab
            if (currentTab == "Today") {
                NextMoveHero(
                    action = nextBestAction,
                    activeTaskId = activeTaskId,
                    onStartTask = { viewModel.startTask(it) },
                    onCompleteTask = { viewModel.completeTask(it) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Tab rendering
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (currentTab) {
                    "Today" -> TodayTab(
                        tasks = tasks,
                        availableMinutes = availableMinutes,
                        activeTaskId = activeTaskId,
                        onStartTask = { viewModel.startTask(it) },
                        onCompleteTask = { viewModel.completeTask(it) },
                        onPostponeClick = {
                            // Prompt Future You warning
                            viewModel.postponeTask(it.id, PostponeChoice.WEDNESDAY) // Fast custom postponing or we can use dialog
                        },
                        onAutopsyClick = { viewModel.requestAutopsy(it) },
                        onRebuildDay = { viewModel.requestRebuildDay() },
                        onTriggerRescue = { viewModel.requestRescuePlan() },
                        onToggleSubtask = { t, idx -> viewModel.toggleSubtask(t, idx) },
                        onDeleteTask = { viewModel.deleteTask(it) }
                    )
                    "Tasks" -> TasksTab(
                        tasks = tasks,
                        onAddNlTask = { viewModel.createWithNaturalLanguage(it) },
                        onSimulateUpdate = { viewModel.simulateUpdate() },
                        onCheckCommitment = { viewModel.checkCommitmentText(it) },
                        onTriggerMultimodal = { showMultimodalModal = true },
                        onDeleteTask = { viewModel.deleteTask(it) }
                    )
                    "Timeline" -> TimelineTab(
                        tasks = tasks,
                        availableMinutes = availableMinutes
                    )
                    "Insights" -> InsightsTab(
                        tasks = tasks
                    )
                }
            }
        }
    }

    // --- MODALS AND POPUPS HUB ---

    // 1. Reality Check Day Rebuilt modal
    if (rebuiltSchedule != null) {
        Dialog(onDismissRequest = { viewModel.dismissRebuiltSchedule() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .border(1.dp, SunsetOrange.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .testTag("rebuilt_schedule_dialog"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSlate)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Autorenew,
                        contentDescription = "Rebuild",
                        tint = SunsetOrange,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "REBUILD YOUR DAY",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunsetOrange
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = rebuiltSchedule?.aiJustification ?: "",
                        fontSize = 12.sp,
                        color = CoolWhite,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.dismissRebuiltSchedule() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = OffWhite),
                            border = BorderStroke(1.dp, DeepCardElevated)
                        ) {
                            Text("DISMISS")
                        }
                        Button(
                            onClick = { viewModel.acceptRebuiltSchedule() },
                            modifier = Modifier.weight(1.5f),
                            colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)
                        ) {
                            Text("ACCEPT DAY PLAN", fontWeight = FontWeight.Bold, color = DarkSlate)
                        }
                    }
                }
            }
        }
    }

    // 2. Rescue Plan modal
    RescueModeModal(
        plan = rescuePlan,
        allTasks = tasks,
        onAccept = { viewModel.acceptRescuePlan() },
        onDismiss = { viewModel.dismissRescuePlan() }
    )

    // 3. Autopsy suggestion modal
    AutopsyModal(
        suggestion = autopsySuggestion,
        onFixTask = { viewModel.fixTaskWithAutopsy() },
        onDismiss = { viewModel.dismissAutopsy() }
    )

    // 4. Commitment Radar Detection modal
    CommitmentRadarModal(
        detection = detectedCommitment,
        onTrack = { viewModel.trackCommitment() },
        onDismiss = { viewModel.dismissCommitment() }
    )

    // 5. Multimodal Image parsing modal
    if (showMultimodalModal) {
        MultimodalInputModal(
            parsedTasks = parsedImageTasks,
            isAnalyzing = isAiThinking,
            onSelectSource = { viewModel.selectMockImageInput(it) },
            onImport = {
                viewModel.importParsedImageTasks()
                showMultimodalModal = false
            },
            onDismiss = {
                viewModel.dismissParsedImageTasks()
                showMultimodalModal = false
            }
        )
    }

    // 6. Settings / Reset Dialog
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("App Preferences", color = CoolWhite) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Daily Available Minutes:", fontSize = 12.sp, color = SlateGray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Slider(
                            value = availableMinutes.toFloat(),
                            onValueChange = { viewModel.setAvailableMinutes(it.toInt()) },
                            valueRange = 60f..480f,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = formatMinutes(availableMinutes),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentBlue
                        )
                    }
                    Divider(color = DeepCardElevated)
                    Text(
                        text = "Reset all saved schedules, database, and settings to start fresh.",
                        fontSize = 11.sp,
                        color = SlateGray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReset()
                        showSettingsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseRed)
                ) {
                    Text("RESET APPLICATION", color = CoolWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("CLOSE", color = SlateGray)
                }
            },
            containerColor = DeepCard
        )
    }
}
