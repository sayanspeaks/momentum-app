package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.viewmodel.PostponeChoice

@Composable
fun FutureYouDialog(
    taskTitle: String,
    estimatedMinutes: Int,
    currentTomorrowMinutes: Int,
    onChoice: (PostponeChoice) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(1.dp, SunsetOrange.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .testTag("future_you_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSlate)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SunsetOrange.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Schedule Conflict",
                        tint = SunsetOrange,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "FUTURE YOU WARNING",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SunsetOrange,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Postponing “$taskTitle”",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = CoolWhite,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "You already have ${currentTomorrowMinutes / 60}h ${currentTomorrowMinutes % 60}m planned tomorrow. Moving this task there will overload your day with an additional ${estimatedMinutes}m.",
                    fontSize = 13.sp,
                    color = OffWhite,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "SELECT AN INTELLIGENT ROUTE:",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateGray,
                    modifier = Modifier.align(Alignment.Start),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Postpone Choice buttons
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ChoiceButton(
                        title = "Move to Tomorrow Evening",
                        subtitle = "+${estimatedMinutes}m tomorrow (Increases fatigue risk)",
                        onClick = { onChoice(PostponeChoice.TOMORROW) },
                        testTag = "postpone_tomorrow_btn"
                    )

                    ChoiceButton(
                        title = "Move to Wednesday Afternoon",
                        subtitle = "Balanced load (Highly recommended by AI)",
                        onClick = { onChoice(PostponeChoice.WEDNESDAY) },
                        testTag = "postpone_wednesday_btn"
                    )

                    ChoiceButton(
                        title = "Split into Two 15-min Sessions",
                        subtitle = "Maintains execution without overloads",
                        onClick = { onChoice(PostponeChoice.SPLIT) },
                        testTag = "postpone_split_btn"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("KEEP ON TODAY'S LIST", color = SlateGray, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ChoiceButton(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        colors = ButtonDefaults.buttonColors(containerColor = DeepCard),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(0.5.dp, DeepCardElevated),
        contentPadding = PaddingValues(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                color = CoolWhite,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = SlateGray,
                fontWeight = FontWeight.Light
            )
        }
    }
}
