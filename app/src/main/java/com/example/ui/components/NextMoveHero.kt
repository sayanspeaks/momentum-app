package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.NextBestAction

@Composable
fun NextMoveHero(
    action: NextBestAction?,
    activeTaskId: Int?,
    onStartTask: (Int) -> Unit,
    onCompleteTask: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showWhy by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(AccentBlue, NeonViolet)
                ),
                shape = RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF13172E) // Special deep indigo card back
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // Header Tag & AI badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = "Flash",
                        tint = AccentBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "YOUR NEXT MOVE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentBlue,
                        letterSpacing = 2.sp
                    )
                }
                
                // Confidence label
                action?.let {
                    Surface(
                        color = NeonViolet.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "AI Confidence",
                                tint = NeonViolet,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${it.confidence}% AI Confidence",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NeonViolet
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (action == null) {
                // Empty State
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "Everything is executed.",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CoolWhite
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add tasks to keep your momentum going!",
                        fontSize = 13.sp,
                        color = SlateGray
                    )
                }
            } else {
                // Active Action Detail
                Text(
                    text = "“${action.recommendation}”",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = CoolWhite,
                    lineHeight = 30.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Stats row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Time",
                            tint = SlateGray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${action.estimatedMinutes} min",
                            fontSize = 13.sp,
                            color = OffWhite,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Impact",
                            tint = EmeraldMint,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = action.impact,
                            fontSize = 13.sp,
                            color = EmeraldMint,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Urgency",
                            tint = if (action.urgency == "High") RoseRed else SunsetOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${action.urgency} Urgency",
                            fontSize = 13.sp,
                            color = if (action.urgency == "High") RoseRed else SunsetOrange,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Divider(color = DeepCardElevated, thickness = 1.dp)

                Spacer(modifier = Modifier.height(16.dp))

                // Why Now Section
                Text(
                    text = "WHY NOW?",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateGray,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    action.reasoning.forEach { reason ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Reason Check",
                                tint = EmeraldMint,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = reason,
                                fontSize = 13.sp,
                                color = OffWhite
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action CTAs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val isRunning = activeTaskId == action.taskId
                    
                    if (isRunning) {
                        Button(
                            onClick = { onCompleteTask(action.taskId) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("complete_active_task_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Done, contentDescription = "Complete")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("COMPLETE TASK", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { onStartTask(action.taskId) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("start_task_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Start")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("START TASK", fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = { showWhy = !showWhy },
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("show_why_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OffWhite),
                        border = BorderStroke(1.dp, DeepCardElevated)
                    ) {
                        Icon(
                            imageVector = if (showWhy) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Show Why"
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SHOW WHY")
                    }
                }

                // Show Why explanation
                AnimatedVisibility(
                    visible = showWhy,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .background(DeepCard.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .border(0.5.dp, DeepCardElevated, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "AI Logic",
                                tint = NeonViolet,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Momentum Execution Reasoning",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CoolWhite
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Momentum analyzed your current load of ${action.estimatedMinutes}m needed tasks. It selected this task because it is flagged as \"${action.urgency}\" urgency, fits perfectly inside your available time window, and unlocks subsequent dependent deliverables to maintain clean progress. Postponing this would cascade pressure onto later deadlines.",
                            fontSize = 12.sp,
                            color = SlateGray,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
