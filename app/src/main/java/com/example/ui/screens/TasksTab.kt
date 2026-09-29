package com.example.ui.screens

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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Task
import com.example.ui.components.formatMinutes
import com.example.ui.theme.*

@Composable
fun TasksTab(
    tasks: List<Task>,
    onAddNlTask: (String) -> Unit,
    onSimulateUpdate: () -> Unit,
    onCheckCommitment: (String) -> Unit,
    onTriggerMultimodal: () -> Unit,
    onDeleteTask: (Int) -> Unit
) {
    var nlInput by remember { mutableStateOf("") }
    var chatInput by remember { mutableStateOf("") }
    var activeSubTab by remember { mutableStateOf("NL_INPUT") } // "NL_INPUT" or "COMMIT_RADAR"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("tasks_tab_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Actions Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Task Execution Intel",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CoolWhite
                    )
                    Text(
                        text = "Inject tasks, simulate feeds, and analyze chats.",
                        fontSize = 13.sp,
                        color = SlateGray
                    )
                }

                // SIMULATE UPDATE action
                Button(
                    onClick = onSimulateUpdate,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, EmeraldMint.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("simulate_update_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Autorenew,
                        contentDescription = "Simulate",
                        tint = EmeraldMint,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "SIMULATE UPDATE",
                        color = EmeraldMint,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Subtabs selector (NL Task vs Commitment Radar)
        item {
            TabRow(
                selectedTabIndex = if (activeSubTab == "NL_INPUT") 0 else 1,
                containerColor = Color.Transparent,
                contentColor = CoolWhite,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[if (activeSubTab == "NL_INPUT") 0 else 1]),
                        color = AccentBlue
                    )
                },
                divider = {}
            ) {
                Tab(
                    selected = activeSubTab == "NL_INPUT",
                    onClick = { activeSubTab = "NL_INPUT" },
                    text = { Text("Natural Language Creation", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nl_creation_subtab")
                )
                Tab(
                    selected = activeSubTab == "COMMIT_RADAR",
                    onClick = { activeSubTab = "COMMIT_RADAR" },
                    text = { Text("Commitment Radar", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("commitment_radar_subtab")
                )
            }
        }

        // --- NATURAL LANGUAGE TASK CREATION INTERFACE ---
        if (activeSubTab == "NL_INPUT") {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "COGNITIVE TASK PARSING",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentBlue,
                                letterSpacing = 1.sp
                            )
                            
                            // Multimodal scanner trigger
                            Row(
                                modifier = Modifier
                                    .clickable { onTriggerMultimodal() }
                                    .background(NeonViolet.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                    .border(0.5.dp, NeonViolet.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Attachment,
                                    contentDescription = "Scan Image",
                                    tint = NeonViolet,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Scan Image / doc",
                                    fontSize = 10.sp,
                                    color = NeonViolet,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = nlInput,
                            onValueChange = { nlInput = it },
                            placeholder = {
                                Text(
                                    "I need to send final slides to Priya tomorrow, but waiting for Rahul's sales numbers...",
                                    color = SlateGray,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("nl_task_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = DeepCardElevated,
                                focusedTextColor = CoolWhite,
                                unfocusedTextColor = CoolWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (nlInput.isNotBlank()) {
                                    onAddNlTask(nlInput)
                                    nlInput = ""
                                }
                            },
                            enabled = nlInput.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("nl_task_submit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("PARSE & COMPILE TASK", fontWeight = FontWeight.Bold, color = CoolWhite)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "QUICK SUGGESTIONS:",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateGray,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        SuggestionChipItem(text = "I need to finish the client presentation by tomorrow") {
                            nlInput = "I need to finish the client presentation by tomorrow"
                        }
                        SuggestionChipItem(text = "Remind me to send Rahul the database report after he sends me numbers") {
                            nlInput = "Remind me to send Rahul the database report after he sends me the numbers"
                        }
                        SuggestionChipItem(text = "I need to prepare for my AWS interview this week") {
                            nlInput = "I need to prepare for my AWS interview this week"
                        }
                    }
                }
            }
        }

        // --- COMMITMENT RADAR INTERFACE ---
        if (activeSubTab == "COMMIT_RADAR") {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "CHAT ANALYSIS ENGINE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonViolet,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Paste a thread, email, or Slack dialogue. Momentum extracts hidden deliverables and creates follow-ups.",
                            fontSize = 12.sp,
                            color = SlateGray,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = chatInput,
                            onValueChange = { chatInput = it },
                            placeholder = {
                                Text(
                                    "Paste chat: \"Hey! I'll send you the report by Friday. Let's touch base then.\"",
                                    color = SlateGray,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("radar_chat_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonViolet,
                                unfocusedBorderColor = DeepCardElevated,
                                focusedTextColor = CoolWhite,
                                unfocusedTextColor = CoolWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (chatInput.isNotBlank()) {
                                    onCheckCommitment(chatInput)
                                    chatInput = ""
                                }
                            },
                            enabled = chatInput.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("radar_chat_submit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Radar, contentDescription = "Radar")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("EXTRACT COMMITMENTS", fontWeight = FontWeight.Bold, color = CoolWhite)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "QUICK FEED SAMPLE:",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateGray,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        SuggestionChipItem(text = "Rahul: \"I'll send you the sales database report by Friday.\"") {
                            chatInput = "Rahul: \"I'll send you the sales database report by Friday.\""
                        }
                    }
                }
            }
        }

        // --- TASK REGISTRY HEADER ---
        item {
            Text(
                text = "Task Registry (${tasks.size} total)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CoolWhite,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // --- LIST OF ALL TASKS ---
        items(tasks) { task ->
            RegistryTaskRow(
                task = task,
                onDelete = { onDeleteTask(task.id) }
            )
        }

        if (tasks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No tasks in registry.", color = SlateGray)
                }
            }
        }
    }
}

@Composable
fun SuggestionChipItem(text: String, onClick: () -> Unit) {
    Surface(
        color = DeepCardElevated.copy(alpha = 0.4f),
        border = BorderStroke(0.5.dp, DeepCardElevated),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Tip",
                tint = SlateGray,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                color = OffWhite,
                maxLines = 1
            )
        }
    }
}

@Composable
fun RegistryTaskRow(
    task: Task,
    onDelete: () -> Unit
) {
    val statusColor = when (task.status) {
        "Completed" -> EmeraldMint
        "Blocked" -> RoseRed
        "Started" -> AccentBlue
        else -> SlateGray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, DeepCardElevated, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = DeepCard)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(statusColor, RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (task.isCompleted) SlateGray else CoolWhite,
                    textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${task.estimatedMinutes}m · ${task.deadline} · ${task.category}",
                    fontSize = 11.sp,
                    color = SlateGray
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = SlateGray.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
