package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Task
import com.example.ui.theme.*

@Composable
fun MultimodalInputModal(
    parsedTasks: List<Task>?,
    isAnalyzing: Boolean,
    onSelectSource: (String) -> Unit,
    onImport: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedPreset by remember { mutableStateOf("handwritten_list") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(1.dp, NeonViolet.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .testTag("multimodal_input_modal"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSlate)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon Header
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(NeonViolet.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DocumentScanner,
                        contentDescription = "Document Input",
                        tint = NeonViolet,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "MULTIMODAL AI EXTRACTOR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonViolet,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Upload Note, Sketch, or Screenshot",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = CoolWhite,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Extract structured tasks instantly from visual layouts or handwriting logs.",
                    fontSize = 12.sp,
                    color = SlateGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (parsedTasks == null) {
                    // Selection view
                    Text(
                        text = "CHOOSE A DEMO FILE TO PARSE:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateGray,
                        modifier = Modifier.align(Alignment.Start),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PresetCard(
                        title = "Handwritten Notebook List",
                        description = "Contains dentist visit, utilities bill, charger, calling mom, AWS study.",
                        icon = Icons.Default.LibraryBooks,
                        isSelected = selectedPreset == "handwritten_list",
                        onClick = { selectedPreset = "handwritten_list" }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PresetCard(
                        title = "Client Email / Screenshot",
                        description = "Review comments about sprint updates and design feedback from Sarah.",
                        icon = Icons.Default.Image,
                        isSelected = selectedPreset == "screenshot",
                        onClick = { selectedPreset = "screenshot" }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isAnalyzing) {
                        CircularProgressIndicator(color = NeonViolet)
                    } else {
                        Button(
                            onClick = { onSelectSource(selectedPreset) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("analyze_image_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.FlashOn, contentDescription = "AI")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ANALYZE DOCUMENT WITH AI", fontWeight = FontWeight.Bold, color = CoolWhite)
                        }
                    }
                } else {
                    // Extractions Results Preview
                    Text(
                        text = "EXTRACTED TASK CANDIDATES:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateGray,
                        modifier = Modifier.align(Alignment.Start),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        parsedTasks.forEach { t ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(DeepCard, RoundedCornerShape(12.dp))
                                    .border(0.5.dp, DeepCardElevated, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = t.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CoolWhite
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${t.estimatedMinutes} mins · ${t.deadline} · ${t.category}",
                                        fontSize = 11.sp,
                                        color = SlateGray
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(EmeraldMint.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Ready",
                                        tint = EmeraldMint,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

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
                            onClick = onImport,
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("import_tasks_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("IMPORT ALL TASKS", fontWeight = FontWeight.Bold, color = CoolWhite)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PresetCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = if (isSelected) NeonViolet else DeepCardElevated,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DeepCardElevated else DeepCard
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) NeonViolet.copy(alpha = 0.2f) else DeepCardElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isSelected) NeonViolet else SlateGray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CoolWhite
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = SlateGray,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
