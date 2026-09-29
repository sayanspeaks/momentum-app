package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.Task
import com.example.ui.theme.*
import com.example.viewmodel.RescuePlan

@Composable
fun RescueModeModal(
    plan: RescuePlan?,
    allTasks: List<Task>,
    onAccept: () -> Unit,
    onDismiss: () -> Unit
) {
    if (plan == null) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .wrapContentHeight()
                .border(1.5.dp, RoseRed.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .testTag("rescue_modal"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSlate)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Alert Icon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(RoseRed.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CrisisAlert,
                        contentDescription = "Rescue Mode",
                        tint = RoseRed,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "RESCUE MODE ACTIVATED",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoseRed,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Momentum AI analyzed your remaining tasks and calculated a high-yield recovery schedule:",
                    fontSize = 13.sp,
                    color = SlateGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // AI Recovery Message card
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeepCard),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, DeepCardElevated, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "AI Plan",
                            tint = NeonViolet,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = plan.recoveryMessage,
                            fontSize = 12.sp,
                            color = OffWhite,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Recovery Lists Display
                RescueSection(
                    title = "🔥 CRITICAL (Do Today)",
                    color = RoseRed,
                    taskIds = plan.criticalIds,
                    allTasks = allTasks
                )

                Spacer(modifier = Modifier.height(12.dp))

                RescueSection(
                    title = "⏳ MOVE (Postpone)",
                    color = SunsetOrange,
                    taskIds = plan.moveIds,
                    allTasks = allTasks
                )

                Spacer(modifier = Modifier.height(12.dp))

                RescueSection(
                    title = "⚡ SPLIT (Half Session)",
                    color = NeonViolet,
                    taskIds = plan.splitIds,
                    allTasks = allTasks
                )

                Spacer(modifier = Modifier.height(12.dp))

                RescueSection(
                    title = "💤 DROP (Archive/Snooze)",
                    color = SlateGray,
                    taskIds = plan.dropIds,
                    allTasks = allTasks
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action CTAs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, DeepCardElevated),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OffWhite)
                    ) {
                        Text("CANCEL")
                    }

                    Button(
                        onClick = onAccept,
                        modifier = Modifier
                            .weight(1.5f)
                            .height(48.dp)
                            .testTag("accept_rescue_plan_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoseRed)
                    ) {
                        Text("ACCEPT RECOVERY PLAN", fontWeight = FontWeight.Bold, color = CoolWhite)
                    }
                }
            }
        }
    }
}

@Composable
fun RescueSection(
    title: String,
    color: Color,
    taskIds: List<Int>,
    allTasks: List<Task>
) {
    val matchedTasks = allTasks.filter { it.id in taskIds }
    
    if (matchedTasks.isNotEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(6.dp))
            matchedTasks.forEach { task ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(color, RoundedCornerShape(3.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = task.title,
                            fontSize = 13.sp,
                            color = CoolWhite,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = "${task.estimatedMinutes}m",
                        fontSize = 12.sp,
                        color = SlateGray
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Divider(color = DeepCardElevated, thickness = 0.5.dp)
        }
    }
}
