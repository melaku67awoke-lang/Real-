package com.example.ui.screens

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.GreenProfit
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.RealCoinUiState
import com.example.ui.viewmodel.RealCoinViewModel
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/**
 * Requirement:
 * 1. Written rewards on wheel slices and show rewards before spinning.
 * 2. Only 1 free spin per 24 hours.
 * 3. After 1 free spin, users can play by 10 REAL coin deducted from balance.
 */
@Composable
fun SpinWheelScreen(
  viewModel: RealCoinViewModel,
  uiState: RealCoinUiState,
  onBack: () -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  val rotation = remember { Animatable(0f) }
  var showWinDialog by remember { mutableStateOf(false) }
  var wonAmount by remember { mutableStateOf(0.0) }

  val wheelPrizes = listOf(10.0, 5.0, 50.0, 15.0, 100.0, 25.0, 5.0, 75.0)
  val wheelSlices = listOf(
    "10 REAL",
    "5 REAL",
    "50 REAL",
    "15 REAL",
    "100 REAL",
    "25 REAL",
    "5 REAL",
    "75 REAL",
  )

  val sliceColors = listOf(
    Color(0xFFD4AF37), // GoldPrimary
    Color(0xFF1E293B), // Slate Dark
    Color(0xFFF59E0B), // Amber Gold
    Color(0xFF0F172A), // Midnight Dark
    Color(0xFFEAB308), // Bright Gold
    Color(0xFF1E293B), // Slate Dark
    Color(0xFFD97706), // Deep Amber
    Color(0xFF8B5CF6), // Royal Purple Accent
  )

  val now = System.currentTimeMillis()
  val lastFreeSpin = uiState.userProfile.lastFreeSpinTime
  val isFreeSpinAvailable = lastFreeSpin == 0L || (now - lastFreeSpin >= 24 * 60 * 60 * 1000L)
  val remainingMillis = if (!isFreeSpinAvailable) {
    (24 * 60 * 60 * 1000L) - (now - lastFreeSpin)
  } else 0L
  val remainingHours = (remainingMillis / (1000 * 60 * 60)).coerceAtLeast(0)
  val remainingMins = ((remainingMillis / (1000 * 60)) % 60).coerceAtLeast(0)

  val hasEnoughBalanceForPaidSpin = uiState.realBalance >= 10.0

  val textPaint = remember {
    Paint().apply {
      color = android.graphics.Color.WHITE
      textSize = 34f
      isAntiAlias = true
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      textAlign = Paint.Align.CENTER
    }
  }

  LazyColumn(
    modifier =
      Modifier.fillMaxSize()
        .background(DarkBackground)
        .padding(horizontal = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    item {
      Spacer(modifier = Modifier.height(16.dp))

      // Header Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        IconButton(onClick = onBack) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = "Lucky Spin & Win",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
          )
          Text(
            text = "1 Free Spin / 24h • Next spins 10 REAL",
            fontSize = 11.sp,
            color = GoldPrimary,
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 24H Rule & Balance Card
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isFreeSpinAvailable) GreenProfit.copy(alpha = 0.5f) else GoldPrimary.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                if (isFreeSpinAvailable) Icons.Default.CheckCircle else Icons.Default.Lock,
                contentDescription = null,
                tint = if (isFreeSpinAvailable) GreenProfit else GoldPrimary,
                modifier = Modifier.size(20.dp),
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (isFreeSpinAvailable) "Daily Free Spin Ready" else "Free Spin Used (1 / 24h limit)",
                fontWeight = FontWeight.Bold,
                color = if (isFreeSpinAvailable) GreenProfit else TextPrimary,
                fontSize = 13.sp,
              )
            }
            if (!isFreeSpinAvailable) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = DarkCard,
              ) {
                Text(
                  text = "Reset in %dh %dm".format(remainingHours, remainingMins),
                  fontSize = 11.sp,
                  color = GoldPrimary,
                  fontWeight = FontWeight.SemiBold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = if (isFreeSpinAvailable) {
              "You have 1 free spin available today. Spins are strictly limited to 1 time per 24 hours. After that, play for 10 REAL coin!"
            } else {
              "Your 24-hour free spin has been claimed. You can continue playing by paying 10 REAL coin per spin from your balance."
            },
            fontSize = 11.sp,
            color = TextSecondary,
            lineHeight = 16.sp,
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = "Your Balance: %,.2f REAL".format(uiState.realBalance),
              color = GoldPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
            )
            Text(
              text = "Spin Cost: ${if (isFreeSpinAvailable) "FREE" else "10 REAL"}",
              color = if (isFreeSpinAvailable) GreenProfit else TextSecondary,
              fontWeight = FontWeight.SemiBold,
              fontSize = 12.sp,
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Requirement: Show rewards BEFORE spinning
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Wheel Reward Pool (Shows Before Spin)",
              color = TextPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
            )
          }
          Spacer(modifier = Modifier.height(10.dp))

          // 8 Prize Badges Grid
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            listOf("100 REAL (Grand)", "75 REAL", "50 REAL", "25 REAL").forEach { reward ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (reward.contains("100")) GoldPrimary.copy(alpha = 0.2f) else DarkCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (reward.contains("100")) GoldPrimary else DarkCardBorder),
                modifier = Modifier.weight(1f),
              ) {
                Text(
                  text = reward,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (reward.contains("100")) GoldPrimary else TextPrimary,
                  modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
              }
            }
          }
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            listOf("15 REAL", "10 REAL", "5 REAL", "5 REAL").forEach { reward ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = DarkCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.weight(1f),
              ) {
                Text(
                  text = reward,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Medium,
                  color = TextSecondary,
                  modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Wheel Container with Needle Pointer & Written Rewards on Wheel
      Box(
        modifier = Modifier.size(310.dp),
        contentAlignment = Alignment.Center,
      ) {
        // Rotating Wheel Canvas
        Canvas(
          modifier =
            Modifier.size(290.dp)
              .rotate(rotation.value)
        ) {
          val radius = size.width / 2f
          val center = Offset(radius, radius)
          val sliceAngle = 360f / wheelSlices.size

          // Draw Slices with Written Rewards
          for (i in wheelSlices.indices) {
            val startAngle = i * sliceAngle
            drawArc(
              color = sliceColors[i % sliceColors.size],
              startAngle = startAngle,
              sweepAngle = sliceAngle,
              useCenter = true,
              size = size,
            )

            // Divider Line
            val radLine = Math.toRadians(startAngle.toDouble())
            drawLine(
              color = Color.Black.copy(alpha = 0.5f),
              start = center,
              end = Offset(
                (center.x + radius * cos(radLine)).toFloat(),
                (center.y + radius * sin(radLine)).toFloat()
              ),
              strokeWidth = 2.dp.toPx(),
            )

            // Written Reward on Slice Canvas
            val midAngle = startAngle + sliceAngle / 2f
            val radMid = Math.toRadians(midAngle.toDouble())
            val textDist = radius * 0.65f
            val tx = (center.x + textDist * cos(radMid)).toFloat()
            val ty = (center.y + textDist * sin(radMid)).toFloat()

            drawContext.canvas.nativeCanvas.save()
            drawContext.canvas.nativeCanvas.rotate(midAngle + 90f, tx, ty)
            drawContext.canvas.nativeCanvas.drawText(wheelSlices[i], tx, ty + 8f, textPaint)
            drawContext.canvas.nativeCanvas.restore()
          }

          // Outer Gold Ring
          drawCircle(
            brush = Brush.linearGradient(listOf(GoldPrimary, GoldDark, GoldLight)),
            radius = radius,
            center = center,
            style = Stroke(width = 8.dp.toPx()),
          )

          // Center Hub
          drawCircle(
            color = DarkBackground,
            radius = 32.dp.toPx(),
            center = center,
          )
          drawCircle(
            color = GoldPrimary,
            radius = 24.dp.toPx(),
            center = center,
          )
        }

        // Center RealCoin Hub Icon
        Box(
          modifier =
            Modifier.size(46.dp)
              .clip(CircleShape)
              .background(GoldPrimary),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            Icons.Default.Casino,
            contentDescription = null,
            tint = DarkBackground,
            modifier = Modifier.size(26.dp),
          )
        }

        // Top Needle Pointer
        Canvas(
          modifier =
            Modifier.size(40.dp)
              .align(Alignment.TopCenter)
        ) {
          val path =
            Path().apply {
              moveTo(size.width / 2f, size.height)
              lineTo(size.width * 0.2f, 0f)
              lineTo(size.width * 0.8f, 0f)
              close()
            }
          drawPath(path, color = GoldPrimary, style = Fill)
          drawPath(path, color = Color.White, style = Stroke(width = 2.dp.toPx()))
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Spin Action Button
      val isPaidSpin = !isFreeSpinAvailable
      val canSpin = !uiState.isSpinning && (isFreeSpinAvailable || hasEnoughBalanceForPaidSpin)

      Button(
        onClick = {
          if (canSpin) {
            coroutineScope.launch {
              // Requirement: Win rate for prizes above 10 REAL coin is strictly 20% (protecting platform from losing money)
              val roll = kotlin.random.Random.nextDouble()
              val targetIndex = if (roll < 0.20) {
                // 20% chance: Above 10 REAL (indices: 3 -> 15 REAL, 5 -> 25 REAL, 2 -> 50 REAL, 7 -> 75 REAL, 4 -> 100 REAL)
                val highRoll = kotlin.random.Random.nextDouble()
                when {
                  highRoll < 0.45 -> 3 // 15 REAL
                  highRoll < 0.75 -> 5 // 25 REAL
                  highRoll < 0.90 -> 2 // 50 REAL
                  highRoll < 0.97 -> 7 // 75 REAL
                  else -> 4            // 100 REAL (Grand Prize)
                }
              } else {
                // 80% chance: 10 REAL or below (indices: 0 -> 10 REAL, 1 -> 5 REAL, 6 -> 5 REAL)
                val lowIndices = listOf(0, 1, 6)
                lowIndices.random()
              }

              val sliceAngle = 360f / wheelSlices.size
              // Align targetIndex sector center under top needle (270 degrees)
              val sliceCenterAngle = targetIndex * sliceAngle + sliceAngle / 2f
              val fullSpins = (6..9).random() * 360f
              val normalizedCurrent = rotation.value % 360f
              val targetAngle = 270f - sliceCenterAngle
              val delta = ((targetAngle - normalizedCurrent) % 360f + 360f) % 360f
              val finalRotation = rotation.value + fullSpins + delta

              val prize = wheelPrizes[targetIndex]

              viewModel.spinWheel(
                isPaidSpin = isPaidSpin,
                prizeAmount = prize,
              ) { wonPrize ->
                wonAmount = wonPrize
                showWinDialog = true
              }

              rotation.animateTo(
                targetValue = finalRotation,
                animationSpec =
                  tween(
                    durationMillis = 3200,
                    easing = FastOutSlowInEasing,
                  ),
              )
            }
          }
        },
        enabled = canSpin,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        colors =
          ButtonDefaults.buttonColors(
            containerColor = if (isFreeSpinAvailable) GreenProfit else GoldPrimary,
            disabledContainerColor = DarkCard,
          ),
        shape = RoundedCornerShape(14.dp),
      ) {
        Text(
          text = when {
            uiState.isSpinning -> "SPINNING WHEEL..."
            isFreeSpinAvailable -> "SPIN WHEEL (FREE DAILY SPIN)"
            hasEnoughBalanceForPaidSpin -> "SPIN WITH 10 REAL COIN"
            else -> "INSUFFICIENT BALANCE (10 REAL NEEDED)"
          },
          color = if (!canSpin) TextMuted else DarkBackground,
          fontWeight = FontWeight.ExtraBold,
          fontSize = 15.sp,
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      if (!isFreeSpinAvailable && !hasEnoughBalanceForPaidSpin) {
        Text(
          text = "You need at least 10 REAL coin to play. Deposit USDT or wait for your daily free spin reset.",
          color = Color(0xFFEF4444),
          fontSize = 11.sp,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Congratulations Dialog
  if (showWinDialog) {
    Dialog(onDismissRequest = { showWinDialog = false }) {
      Card(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(2.dp, GoldPrimary),
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Icon(
            Icons.Default.Celebration,
            contentDescription = null,
            tint = GoldPrimary,
            modifier = Modifier.size(56.dp),
          )

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "CONGRATULATIONS!",
            color = GoldPrimary,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "You won",
            color = TextSecondary,
            fontSize = 14.sp,
          )

          Text(
            text = "+%.0f REAL".format(wonAmount),
            color = GreenProfit,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp,
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Tokens have been credited directly into your wallet balance!",
            color = TextMuted,
            fontSize = 12.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
          )

          Spacer(modifier = Modifier.height(20.dp))

          Button(
            onClick = { showWinDialog = false },
            modifier = Modifier.fillMaxWidth().height(46.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
            shape = RoundedCornerShape(12.dp),
          ) {
            Text("Claim & Return", color = DarkBackground, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
