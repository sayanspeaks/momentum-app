package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Task
import com.example.ui.theme.*

@Composable
fun TaskGraphView(
    tasks: List<Task>,
    onNodeClick: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    // We can model a clean workflow diagram:
    // [Rahul's sales numbers (Waiting / Blocked)]
    //             ↓
    // [Finish Client Presentation (Blocked)]
    //             ↓
    // [Review Priya's API PR (Pending/Ready)]
    //             ↓
    // [Prepare AWS Architecture]

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(0.5.dp, DeepCardElevated, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = DeepCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "TASK EXECUTION FLOW GRAPH",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SlateGray,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                // Background connector line
                Canvas(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(start = 24.dp) // align line with icons
                ) {
                    val strokeWidth = 2.dp.toPx()
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

                    // Draw vertical line connecting nodes
                    drawLine(
                        color = SlateGray.copy(alpha = 0.3f),
                        start = Offset(x = 0f, y = 20.dp.toPx()),
                        end = Offset(x = 0f, y = size.height - 40.dp.toPx()),
                        strokeWidth = strokeWidth,
                        pathEffect = pathEffect
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    val representationTasks = tasks.take(5) // Limit to fit nicely
                    
                    representationTasks.forEach { task ->
                        val (icon, color, label) = when (task.status) {
                            "Completed" -> Triple(Icons.Default.Check, EmeraldMint, "COMPLETED")
                            "Blocked" -> Triple(Icons.Default.Block, RoseRed, "BLOCKED BY: " + (task.dependency ?: "Dependencies"))
                            "Started" -> Triple(Icons.Default.PlayArrow, AccentBlue, "IN PROGRESS")
                            else -> if (task.category == "WAITING FOR") {
                                Triple(Icons.Default.HourglassEmpty, SunsetOrange, "WAITING")
                            } else {
                                Triple(Icons.Default.PlayArrow, CoolWhite, "READY")
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNodeClick(task) }
                                .padding(vertical = 4.dp)
                                .testTag("graph_node_${task.id}")
                        ) {
                            // Circular Indicator Node
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(
                                        if (task.status == "Started") {
                                            AccentBlue.copy(alpha = 0.15f)
                                        } else {
                                            DeepCardElevated
                                        }
                                    )
                                    .border(
                                        width = if (task.status == "Started") 2.dp else 1.dp,
                                        color = color,
                                        shape = RoundedCornerShape(24.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = "Status",
                                    tint = color,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Details Card Node
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (task.status == "Started") {
                                        AccentBlue.copy(alpha = 0.05f)
                                    } else {
                                        DarkSlate.copy(alpha = 0.5f)
                                    }
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = 0.5.dp,
                                        color = if (task.status == "Started") AccentBlue.copy(alpha = 0.3f) else DeepCardElevated,
                                        shape = RoundedCornerShape(12.dp)
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
                                        Surface(
                                            color = color.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = label,
                                                fontSize = 8.sp,
                                                color = color,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
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
                                    
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${task.estimatedMinutes} min",
                                            fontSize = 10.sp,
                                            color = SlateGray
                                        )
                                        Box(modifier = Modifier.size(4.dp).background(SlateGray.copy(alpha = 0.5f), RoundedCornerShape(2.dp)))
                                        Text(
                                            text = "Due: ${task.deadline}",
                                            fontSize = 10.sp,
                                            color = SlateGray
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
