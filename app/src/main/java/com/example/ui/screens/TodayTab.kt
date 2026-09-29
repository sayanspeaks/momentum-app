package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Task
import com.example.ui.components.RealityCheckCard
import com.example.ui.components.formatMinutes
import com.example.ui.theme.*

@Composable
fun TodayTab(
    tasks: List<Task>,
    availableMinutes: Int,
    activeTaskId: Int?,
    onStartTask: (Int) -> Unit,
    onCompleteTask: (Int) -> Unit,
    onPostponeClick: (Task) -> Unit,
    onAutopsyClick: (Task) -> Unit,
    onRebuildDay: () -> Unit,
    onTriggerRescue: () -> Unit,
    onToggleSubtask: (Task, Int) -> Unit,
    onDeleteTask: (Int) -> Unit
) {
    val pendingTasks = tasks.filter { it.status != "Completed" }
    val totalRequiredMinutes = pendingTasks.sumOf { it.estimatedMinutes }

    // Partition tasks
    val mustDo = pendingTasks.filter { it.category == "MUST DO" }
    val shouldDo = pendingTasks.filter { it.category == "SHOULD DO" }
    val canDo = pendingTasks.filter { it.category == "CAN DO" }
    val waitingFor = pendingTasks.filter { it.category == "WAITING FOR" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("today_tab_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Reality Check Alert Card
        item {
            RealityCheckCard(
                availableMinutes = availableMinutes,
                totalRequiredMinutes = totalRequiredMinutes,
                onRebuildDay = onRebuildDay
            )
        }

        // Today's summary & Rescue button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Today's Agenda",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CoolWhite
                    )
                    Text(
                        text = "${pendingTasks.size} pending tasks · ${totalRequiredMinutes}m total load",
                        fontSize = 13.sp,
                        color = SlateGray
                    )
                }

                Button(
                    onClick = onTriggerRescue,
                    colors = ButtonDefaults.buttonColors(containerColor = RoseRed.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, RoseRed.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("rescue_trigger_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CrisisAlert,
                        contentDescription = "Rescue Day",
                        tint = RoseRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "My day is screwed",
                        color = RoseRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // --- MUST DO SECTION ---
        if (mustDo.isNotEmpty()) {
            item { SectionHeader("🔴 MUST DO", RoseRed) }
            items(mustDo) { task ->
                TaskCard(
                    task = task,
                    isRunning = activeTaskId == task.id,
                    onStart = { onStartTask(task.id) },
                    onComplete = { onCompleteTask(task.id) },
                    onPostpone = { onPostponeClick(task) },
                    onAutopsy = { onAutopsyClick(task) },
                    onToggleSub = { idx -> onToggleSubtask(task, idx) },
                    onDelete = { onDeleteTask(task.id) }
                )
            }
        }

        // --- SHOULD DO SECTION ---
        if (shouldDo.isNotEmpty()) {
            item { SectionHeader("🟡 SHOULD DO", SunsetOrange) }
            items(shouldDo) { task ->
                TaskCard(
                    task = task,
                    isRunning = activeTaskId == task.id,
                    onStart = { onStartTask(task.id) },
                    onComplete = { onCompleteTask(task.id) },
                    onPostpone = { onPostponeClick(task) },
                    onAutopsy = { onAutopsyClick(task) },
                    onToggleSub = { idx -> onToggleSubtask(task, idx) },
                    onDelete = { onDeleteTask(task.id) }
                )
            }
        }

        // --- CAN DO SECTION ---
        if (canDo.isNotEmpty()) {
            item { SectionHeader("🟢 CAN DO", EmeraldMint) }
            items(canDo) { task ->
                TaskCard(
                    task = task,
                    isRunning = activeTaskId == task.id,
                    onStart = { onStartTask(task.id) },
                    onComplete = { onCompleteTask(task.id) },
                    onPostpone = { onPostponeClick(task) },
                    onAutopsy = { onAutopsyClick(task) },
                    onToggleSub = { idx -> onToggleSubtask(task, idx) },
                    onDelete = { onDeleteTask(task.id) }
                )
            }
        }

        // --- WAITING FOR SECTION ---
        if (waitingFor.isNotEmpty()) {
            item { SectionHeader("⏳ WAITING FOR / BLOCKERS", SlateGray) }
            items(waitingFor) { task ->
                TaskCard(
                    task = task,
                    isRunning = activeTaskId == task.id,
                    onStart = { onStartTask(task.id) },
                    onComplete = { onCompleteTask(task.id) },
                    onPostpone = { onPostponeClick(task) },
                    onAutopsy = { onAutopsyClick(task) },
                    onToggleSub = { idx -> onToggleSubtask(task, idx) },
                    onDelete = { onDeleteTask(task.id) }
                )
            }
        }

        if (pendingTasks.isEmpty()) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "All Done",
                        tint = EmeraldMint,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Awesome Job!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CoolWhite
                    )
                    Text(
                        text = "You've crushed all tasks on your agenda.",
                        fontSize = 13.sp,
                        color = SlateGray
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, color: Color) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        color = color,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun TaskCard(
    task: Task,
    isRunning: Boolean,
    onStart: () -> Unit,
    onComplete: () -> Unit,
    onPostpone: () -> Unit,
    onAutopsy: () -> Unit,
    onToggleSub: (Int) -> Unit,
    onDelete: () -> Unit
) {
    val isBlocked = task.status == "Blocked"
    val showAutopsy = task.postponedCount >= 3

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(
                width = if (isRunning) 1.5.dp else 0.5.dp,
                color = if (isRunning) AccentBlue else DeepCardElevated,
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("task_card_${task.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isRunning) AccentBlue.copy(alpha = 0.05f) else DeepCard
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Title and Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1.0f)) {
                    Text(
                        text = task.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = CoolWhite
                    )
                    if (task.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.description,
                            fontSize = 12.sp,
                            color = SlateGray
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = SlateGray.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subtasks if present
            val subtasks = task.getSubtasks()
            if (subtasks.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "SUBTASKS:",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateGray,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    subtasks.forEachIndexed { idx, sub ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleSub(idx) }
                                .padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (sub.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = "Toggle Subtask",
                                tint = if (sub.isCompleted) EmeraldMint else SlateGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = sub.title,
                                fontSize = 12.sp,
                                color = if (sub.isCompleted) SlateGray else OffWhite,
                                textDecoration = if (sub.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                            )
                        }
                    }
                }
            }

            // Flags / Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Estimated time
                    Surface(
                        color = DeepCardElevated,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${task.estimatedMinutes}m",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = CoolWhite,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Deadline badge
                    Surface(
                        color = DeepCardElevated,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = task.deadline,
                            fontSize = 11.sp,
                            color = if (task.deadline == "Today") RoseRed else SlateGray,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Postponed count warning
                    if (task.postponedCount > 0) {
                        Surface(
                            color = SunsetOrange.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Snoozed ${task.postponedCount}x",
                                fontSize = 11.sp,
                                color = SunsetOrange,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Status Badge
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isBlocked) {
                        Surface(
                            color = RoseRed.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Block,
                                    contentDescription = "Blocked",
                                    tint = RoseRed,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "BLOCKED",
                                    fontSize = 10.sp,
                                    color = RoseRed,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Custom AI note block
            val aiNote = getAiTipForTask(task)
            if (aiNote != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DeepCardElevated.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "AI Tip",
                        tint = NeonViolet,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = aiNote,
                        fontSize = 11.sp,
                        color = SlateGray,
                        lineHeight = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // CTAs at bottom of card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Postpone / Autopsy actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (showAutopsy) {
                        Button(
                            onClick = onAutopsy,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("autopsy_card_btn")
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = "Autopsy", modifier = Modifier.size(14.dp), tint = CoolWhite)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("AUTOPSY", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (task.status != "Completed" && !isBlocked) {
                        OutlinedButton(
                            onClick = onPostpone,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, DeepCardElevated),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateGray),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("postpone_card_btn")
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = "Snooze", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("SNOOZE", fontSize = 11.sp)
                        }
                    }
                }

                // Primary complete/start execution
                if (!isBlocked) {
                    if (isRunning) {
                        Button(
                            onClick = onComplete,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("complete_card_btn")
                        ) {
                            Icon(Icons.Default.Done, contentDescription = "Complete", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("COMPLETE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = onStart,
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("start_card_btn")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Start", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("START", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Blocked notification message
                    Text(
                        text = "Awaiting: ${task.dependency ?: "Rahul"}",
                        fontSize = 11.sp,
                        color = RoseRed,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// Custom AI Tips based on keyword matching
fun getAiTipForTask(task: Task): String? {
    val t = task.title.lowercase()
    return when {
        t.contains("api") -> "Completing this PR unlocks server deployment pipelines."
        t.contains("presentation") -> "This presentation unlocks Priya's final design validation."
        t.contains("aws") -> "Crucial structural dependency for high scalability."
        t.contains("car") -> "Important personal sanity item. Quick to finish."
        t.contains("report") -> "Rahul relies on these analytics metrics for compilation."
        else -> null
    }
}
