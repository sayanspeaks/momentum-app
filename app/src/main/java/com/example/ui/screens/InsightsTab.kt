package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Task
import com.example.ui.theme.*

@Composable
fun InsightsTab(
    tasks: List<Task>
) {
    val total = tasks.size
    val completed = tasks.count { it.status == "Completed" }
    val postponed = tasks.sumOf { it.postponedCount }
    val blocked = tasks.count { it.status == "Blocked" }
    
    val completionRate = if (total > 0) (completed.toFloat() / total.toFloat() * 100).toInt() else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("insights_tab_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Execution Insights",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CoolWhite
                )
                Text(
                    text = "AI-powered productivity audit and pattern analysis.",
                    fontSize = 13.sp,
                    color = SlateGray
                )
            }
        }

        // Analytics Score cards Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Completion",
                    value = "$completionRate%",
                    subtitle = "$completed of $total tasks",
                    color = EmeraldMint,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Postponed",
                    value = "$postponed",
                    subtitle = "Snooze triggers",
                    color = SunsetOrange,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Blocked",
                    value = "$blocked",
                    subtitle = "Awaiting feeds",
                    color = RoseRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Custom drawn bar graph of weekly progress
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepCard),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, DeepCardElevated, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "WEEKLY EXECUTION DENSITY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateGray,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Draw bar graph using Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val bars = listOf(20f, 45f, 90f, 65f, 110f, 40f, 15f)
                            val labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                            val barWidth = 24.dp.toPx()
                            val spacing = (size.width - (barWidth * bars.size)) / (bars.size + 1)
                            val maxHeight = size.height - 30.dp.toPx()

                            bars.forEachIndexed { idx, value ->
                                val x = spacing + idx * (barWidth + spacing)
                                val height = (value / 120f) * maxHeight
                                val y = maxHeight - height

                                // Draw bar gradient
                                drawRoundRect(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(AccentBlue, NeonViolet)
                                    ),
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, height),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                )
                            }
                        }

                        // Labels Row below graph
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                                Text(text = day, fontSize = 10.sp, color = SlateGray, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // AI Recommendations cards
        item {
            Text(
                text = "Momentum AI Observations",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CoolWhite,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            InsightCard(
                observation = "Your estimates are usually 25% lower than actual completion times.",
                suggestion = "AI suggested: We've calibrated your next move engine to automatically pad complex deliverables with +15m buffer.",
                badgeColor = NeonViolet
            )
        }

        item {
            InsightCard(
                observation = "You complete short tasks 2.1× more consistently before 5 PM.",
                suggestion = "AI suggested: Momentum will prioritize booking car services or sending database metrics early in your day's queue.",
                badgeColor = EmeraldMint
            )
        }

        item {
            InsightCard(
                observation = "Large tasks (>60m) are postponed frequently.",
                suggestion = "AI suggested: We recommend running a 'Task Autopsy' for stalled projects to partition them into sub-30 minute sprints.",
                badgeColor = SunsetOrange
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.border(0.5.dp, DeepCardElevated, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DeepCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                color = SlateGray,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = SlateGray,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
fun InsightCard(
    observation: String,
    suggestion: String,
    badgeColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, DeepCardElevated, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DeepCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "AI Insight",
                    tint = badgeColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = observation,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CoolWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = suggestion,
                    fontSize = 11.sp,
                    color = SlateGray,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
