package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Text
import coil.compose.AsyncImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KycStatus
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GreenProfit
import com.example.ui.theme.RedLoss
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.RealCoinUiState
import com.example.ui.viewmodel.RealCoinViewModel

/**
 * KYC Waiting Screen
 * User MUST wait here until Admin approves their ID.
 * Access to the Main App is strictly prohibited until approved.
 * If rejected, user is returned to Landing page to resubmit.
 * If approved, user is automatically directed into Main App.
 */
@Composable
fun KycWaitingScreen(
  viewModel: RealCoinViewModel,
  uiState: RealCoinUiState,
) {
  // Reactive auto-navigation: If Admin approves, go to MAIN. If rejected, go to LANDING.
  LaunchedEffect(uiState.userProfile.kycStatus) {
    when (uiState.userProfile.kycStatus) {
      KycStatus.APPROVED -> {
        viewModel.navigateTo(AppDestination.MAIN)
      }
      KycStatus.REJECTED -> {
        viewModel.navigateTo(AppDestination.LANDING)
      }
      else -> {
        // Stay on waiting screen
      }
    }
  }

  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "pulseScale",
  )

  val submission = uiState.userProfile.kycSubmission

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkBackground)
      .padding(horizontal = 20.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    item {
      Spacer(modifier = Modifier.height(48.dp))

      // RealCoin Brand Header
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(GoldLight, GoldPrimary, GoldDark))),
          contentAlignment = Alignment.Center,
        ) {
          Text("R", fontWeight = FontWeight.Black, fontSize = 20.sp, color = DarkBackground)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text("RealCoin Network", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Animated Glowing Hourglass
      Box(
        modifier = Modifier
          .size(110.dp)
          .scale(pulseScale)
          .clip(CircleShape)
          .background(GoldPrimary.copy(alpha = 0.12f))
          .border(2.dp, GoldPrimary.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center,
      ) {
        Box(
          modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(GoldPrimary.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            Icons.Default.HourglassEmpty,
            contentDescription = "Verification in progress",
            tint = GoldPrimary,
            modifier = Modifier.size(44.dp),
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Title & Subtitle
      Text(
        text = "Verification Under Review",
        fontSize = 24.sp,
        fontWeight = FontWeight.ExtraBold,
        color = TextPrimary,
        textAlign = TextAlign.Center,
      )

      Spacer(modifier = Modifier.height(10.dp))

      Surface(
        shape = RoundedCornerShape(20.dp),
        color = GoldPrimary.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(Icons.Default.AccessTime, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Status: PENDING ADMIN APPROVAL",
            color = GoldPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "Your identity documents have been submitted to the RealCoin compliance desk. To ensure security and compliance, access to the Main App, Wallet, and P2P exchange is paused until an administrator verifies your ID.",
        fontSize = 13.sp,
        color = TextSecondary,
        textAlign = TextAlign.Center,
        lineHeight = 20.sp,
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Submission Details Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Security, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Submitted Details", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }

          Spacer(modifier = Modifier.height(14.dp))

          DetailRow("Full Legal Name", submission?.fullName ?: uiState.userProfile.username)
          DetailRow("Document Type", submission?.docType ?: "National ID (Fayda)")
          DetailRow("Document Number", submission?.docNumber ?: "••••••••")
          DetailRow("Submission Time", submission?.submittedAt ?: "Today")
          DetailRow("Review Queue", "Priority Queue (Est: 5 - 15 mins)")

          if (submission?.frontPhotoUri != null || submission?.selfiePhotoUri != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Submitted ID & Selfie Photos:", fontSize = 11.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              if (submission?.frontPhotoUri != null) {
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkCard)
                    .border(1.dp, GreenProfit.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                  contentAlignment = Alignment.Center,
                ) {
                  AsyncImage(
                    model = submission.frontPhotoUri,
                    contentDescription = "Front ID",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                  )
                }
              }
              if (submission?.backPhotoUri != null) {
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkCard)
                    .border(1.dp, GreenProfit.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                  contentAlignment = Alignment.Center,
                ) {
                  AsyncImage(
                    model = submission.backPhotoUri,
                    contentDescription = "Back ID",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                  )
                }
              }
              if (submission?.selfiePhotoUri != null) {
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkCard)
                    .border(1.dp, GreenProfit.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                  contentAlignment = Alignment.Center,
                ) {
                  AsyncImage(
                    model = submission.selfiePhotoUri,
                    contentDescription = "Live Selfie",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(DarkCard)
              .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenProfit, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Front ID, Back ID, and Live Selfie attached ✓",
              fontSize = 11.sp,
              color = GreenProfit,
              fontWeight = FontWeight.Medium,
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Refresh / Check Status Button
      Button(
        onClick = {
          if (uiState.userProfile.kycStatus == KycStatus.APPROVED) {
            viewModel.navigateTo(AppDestination.MAIN)
          } else {
            viewModel.checkKycStatus()
          }
        },
        modifier = Modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
        shape = RoundedCornerShape(14.dp),
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Refresh, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Check Approval Status", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Contact Support Button
      OutlinedButton(
        onClick = { viewModel.navigateTo(AppDestination.HELP_CENTER) },
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.HelpOutline, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Help Center & Live Inquiries", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Log Out Button (Returns cleanly to Landing)
      OutlinedButton(
        onClick = { viewModel.logout() },
        modifier = Modifier.fillMaxWidth().height(44.dp),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Logout, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Sign Out to Landing Page", fontSize = 12.sp)
        }
      }

      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}

@Composable
private fun DetailRow(label: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(label, fontSize = 12.sp, color = TextMuted)
    Text(value, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
  }
}
