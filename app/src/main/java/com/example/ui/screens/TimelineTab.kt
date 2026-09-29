package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Timeline
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
import com.example.ui.components.formatMinutes
import com.example.ui.theme.*

@Composable
fun TimelineTab(
    tasks: List<Task>,
    availableMinutes: Int
) {
    val pendingTasks = tasks.filter { it.status != "Completed" && it.status != "Blocked" }
    val totalRequiredMinutes = pendingTasks.sumOf { it.estimatedMinutes }
    val isOverloaded = totalRequiredMinutes > availableMinutes

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("timeline_tab_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Momentum Day Timeline",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CoolWhite
                )
                Text(
                    text = "Hourly execution block for ready actions.",
                    fontSize = 13.sp,
                    color = SlateGray
                )
            }
        }

        // Timeline health warning
        if (isOverloaded) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = RoseRed.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, RoseRed.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Alert",
                            tint = RoseRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Timeline Overflow: Scheduled actions require ${formatMinutes(totalRequiredMinutes)} but you have only ${formatMinutes(availableMinutes)} of execution budget remaining. Use Reality Check to prune.",
                            fontSize = 12.sp,
                            color = RoseRed,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Timeline block cards
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepCard),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, DeepCardElevated, RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "CHRONOLOGICAL FORECAST",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateGray,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (pendingTasks.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No pending ready actions.", color = SlateGray)
                        }
                    } else {
                        // Display chronological slots
                        var currentHour = 9
                        var currentMin = 0

                        pendingTasks.forEachIndexed { index, task ->
                            val duration = task.estimatedMinutes
                            val startStr = String.format("%02d:%02d", currentHour, currentMin)
                            
                            // Advance clock
                            currentMin += duration
                            currentHour += currentMin / 60
                            currentMin %= 60

                            val endStr = String.format("%02d:%02d", currentHour, currentMin)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Time Col
                                Column(
                                    modifier = Modifier.width(60.dp),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = startStr,
                                        fontSize = 13.sp,
                                        color = CoolWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = endStr,
                                        fontSize = 11.sp,
                                        color = SlateGray
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // Timeline Dot/Node line
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.height(IntrinsicSize.Max)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (task.status == "Started") AccentBlue else NeonViolet
                                            )
                                    )
                                    if (index < pendingTasks.size - 1) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .weight(1f)
                                                .background(DeepCardElevated)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // Task detail block
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (task.status == "Started") {
                                            AccentBlue.copy(alpha = 0.05f)
                                        } else {
                                            DeepCardElevated.copy(alpha = 0.3f)
                                        }
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(
                                            0.5.dp,
                                            if (task.status == "Started") AccentBlue else DeepCardElevated,
                                            RoundedCornerShape(12.dp)
                                        )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = task.title,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CoolWhite
                                            )
                                            Text(
                                                text = "${task.estimatedMinutes}m",
                                                fontSize = 11.sp,
                                                color = SlateGray,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (task.description.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = task.description,
                                                fontSize = 11.sp,
                                                color = SlateGray,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
