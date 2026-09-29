package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.ui.theme.*

@Composable
fun OnboardingScreen(
    onBuildDay: (String) -> Unit,
    onTryDemo: () -> Unit,
    isThinking: Boolean
) {
    var textInput by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkSlate,
                        Color(0xFF0F1223),
                        Color(0xFF07080D)
                    )
                )
            )
            .padding(24.dp)
            .navigationBarsPadding()
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 500.dp)
        ) {
            // App Logo Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(AccentBlue, NeonViolet)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = "Momentum Icon",
                    tint = CoolWhite,
                    modifier = Modifier.size(45.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Brand Title
            Text(
                text = "MOMENTUM",
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                color = CoolWhite,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle / Tagline
            Text(
                text = "Your AI doesn’t manage your to-do list. It manages your next move.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                color = OffWhite,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Input Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Brush.linearGradient(colors = listOf(AccentBlue.copy(alpha = 0.4f), NeonViolet.copy(alpha = 0.4f))),
                        RoundedCornerShape(20.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = DeepCard.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Initialize Your Momentum",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CoolWhite,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = {
                            Text(
                                "Tell Momentum what you need to get done...",
                                color = SlateGray,
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = DeepCardElevated,
                            focusedTextColor = CoolWhite,
                            unfocusedTextColor = CoolWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isThinking) {
                        CircularProgressIndicator(
                            color = AccentBlue,
                            modifier = Modifier.size(36.dp)
                        )
                    } else {
                        Button(
                            onClick = { if (textInput.isNotBlank()) onBuildDay(textInput) },
                            enabled = textInput.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("build_day_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentBlue,
                                disabledContainerColor = SlateGray.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "BUILD MY DAY",
                                fontWeight = FontWeight.Bold,
                                color = CoolWhite,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Next"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Or start instantly with preloaded projects:",
                        fontSize = 12.sp,
                        color = SlateGray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onTryDemo,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                            .testTag("try_demo_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AccentBlue
                        ),
                        border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.5f))
                    ) {
                        Text(
                            "TRY DEMO & SIMULATE DAY",
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Features short highlight
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FeatureBadge(title = "AI Next Move")
                FeatureBadge(title = "Reality Check")
                FeatureBadge(title = "Task Autopsy")
            }
        }
    }
}

@Composable
fun FeatureBadge(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(DeepCard, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .border(0.5.dp, DeepCardElevated, RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(AccentBlue, RoundedCornerShape(3.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            color = OffWhite,
            fontWeight = FontWeight.Medium
        )
    }
}

// Helper state remember
@Composable
fun <T> rememberState(initial: T): MutableState<T> = remember { mutableStateOf(initial) }
