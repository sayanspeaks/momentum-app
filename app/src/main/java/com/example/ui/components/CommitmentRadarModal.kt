package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.viewmodel.CommitmentDetection

@Composable
fun CommitmentRadarModal(
    detection: CommitmentDetection?,
    onTrack: () -> Unit,
    onDismiss: () -> Unit
) {
    if (detection == null) return

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(1.dp, AccentBlue.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .testTag("commitment_radar_modal"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSlate)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AccentBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = "Radar",
                        tint = AccentBlue,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "COMMITMENT RADAR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentBlue,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Potential Commitment Detected",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = CoolWhite
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Detection content card
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeepCard),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, DeepCardElevated, RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CommitmentField(label = "PERSON / COMMITTER", value = detection.person)
                        CommitmentField(label = "COMMITMENT DETAIL", value = detection.commitment)
                        CommitmentField(label = "EXTRACTED DEADLINE", value = detection.deadline)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // CTAs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, DeepCardElevated),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OffWhite)
                    ) {
                        Text("DISMISS")
                    }

                    Button(
                        onClick = onTrack,
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("track_commitment_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("TRACK COMMITMENT", fontWeight = FontWeight.Bold, color = CoolWhite)
                    }
                }
            }
        }
    }
}

@Composable
fun CommitmentField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = SlateGray,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            color = CoolWhite,
            fontWeight = FontWeight.Medium
        )
    }
}
