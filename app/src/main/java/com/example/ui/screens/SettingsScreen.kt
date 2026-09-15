package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
 * Settings Screen:
 * Displays user data (full name, phone number, email) and prominent verified sign.
 * Replaces KYC tab in the Main App.
 */
@Composable
fun SettingsScreen(
  viewModel: RealCoinViewModel,
  uiState: RealCoinUiState,
) {
  val profile = uiState.userProfile
  val kyc = profile.kycSubmission
  val displayName = kyc?.fullName?.takeIf { it.isNotBlank() } ?: profile.username
  val isVerified = profile.kycStatus == KycStatus.APPROVED

  LazyColumn(
    modifier =
      Modifier.fillMaxSize()
        .background(DarkBackground)
        .padding(horizontal = 16.dp),
  ) {
    // Header
    item {
      Spacer(modifier = Modifier.height(18.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column {
          Text(
            text = "Settings & Profile",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
          )
          Text(
            text = "Manage account, verification & security",
            fontSize = 11.sp,
            color = TextSecondary,
          )
        }

        Surface(
          shape = CircleShape,
          color = DarkSurface,
          border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
          modifier = Modifier.size(38.dp),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Settings, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
          }
        }
      }
      Spacer(modifier = Modifier.height(18.dp))
    }

    // User Profile Card with Verified Badge
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isVerified) GreenProfit.copy(alpha = 0.5f) else DarkCardBorder),
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            // Avatar
            Box(
              modifier =
                Modifier.size(54.dp)
                  .clip(CircleShape)
                  .background(Brush.linearGradient(listOf(GoldPrimary, GoldDark))),
              contentAlignment = Alignment.Center,
            ) {
              Text(
                text = displayName.take(2).uppercase(),
                color = DarkBackground,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
              )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = displayName,
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimary,
                )
                if (isVerified) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Icon(
                    Icons.Default.Verified,
                    contentDescription = "Verified",
                    tint = GreenProfit,
                    modifier = Modifier.size(18.dp),
                  )
                }
              }

              Text(
                text = "@${profile.username}",
                fontSize = 12.sp,
                color = TextSecondary,
              )

              Spacer(modifier = Modifier.height(4.dp))

              // VERIFIED SIGN / BADGE
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isVerified) GreenProfit.copy(alpha = 0.15f) else GoldPrimary.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (isVerified) GreenProfit.copy(alpha = 0.4f) else GoldPrimary.copy(alpha = 0.4f),
                ),
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Icon(
                    if (isVerified) Icons.Default.CheckCircle else Icons.Default.Security,
                    contentDescription = null,
                    tint = if (isVerified) GreenProfit else GoldPrimary,
                    modifier = Modifier.size(12.dp),
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = if (isVerified) "VERIFIED ID • PRO MEMBER" else "KYC IN REVIEW",
                    color = if (isVerified) GreenProfit else GoldPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                  )
                }
              }
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // USERS DATA SECTION (Phone number, Email, Legal ID)
    item {
      Text(
        text = "USER INFORMATION",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = GoldPrimary,
        letterSpacing = 1.sp,
      )
      Spacer(modifier = Modifier.height(8.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          UserDataRow(
            icon = Icons.Default.Person,
            label = "Full Legal Name",
            value = displayName,
            verified = isVerified,
          )

          Spacer(modifier = Modifier.height(12.dp))

          UserDataRow(
            icon = Icons.Default.Phone,
            label = "Phone Number",
            value = profile.phone,
            verified = true,
          )

          Spacer(modifier = Modifier.height(12.dp))

          UserDataRow(
            icon = Icons.Default.Email,
            label = "Email Address",
            value = profile.email,
            verified = true,
          )

          if (kyc != null) {
            Spacer(modifier = Modifier.height(12.dp))
            UserDataRow(
              icon = Icons.Default.Security,
              label = "National ID (${kyc.docType})",
              value = kyc.docNumber,
              verified = isVerified,
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          UserDataRow(
            icon = Icons.Default.AccountBalance,
            label = "BEP-20 Wallet Address",
            value = profile.walletAddress.take(10) + "..." + profile.walletAddress.takeLast(6),
            verified = true,
            trailingAction = {
              IconButton(
                onClick = { viewModel.showToast("Wallet address copied!") },
                modifier = Modifier.size(24.dp),
              ) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = GoldPrimary, modifier = Modifier.size(14.dp))
              }
            },
          )
        }
      }
      Spacer(modifier = Modifier.height(18.dp))
    }

    // VERIFICATION DETAILS & LIMITS CARD
    item {
      Text(
        text = "TRADING LIMITS & STATUS",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = GoldPrimary,
        letterSpacing = 1.sp,
      )
      Spacer(modifier = Modifier.height(8.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text("P2P Daily Limit", color = TextSecondary, fontSize = 13.sp)
            Text("Unlimited Volume", color = GreenProfit, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text("Withdrawal Limit", color = TextSecondary, fontSize = 13.sp)
            Text("No Daily Cap", color = GreenProfit, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text("Payment Methods", color = TextSecondary, fontSize = 13.sp)
            Text("${profile.paymentAccounts.size} Linked Accounts", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        }
      }
      Spacer(modifier = Modifier.height(18.dp))
    }

    // SUPPORT & ACTIONS
    item {
      Text(
        text = "PREFERENCES & SUPPORT",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = GoldPrimary,
        letterSpacing = 1.sp,
      )
      Spacer(modifier = Modifier.height(8.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          // Help Center Button
          Button(
            onClick = { viewModel.navigateTo(AppDestination.HELP_CENTER) },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.HelpOutline, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Text("Help Center & Inquiries", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Sign Out Button
          OutlinedButton(
            onClick = { viewModel.logout() },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, RedLoss.copy(alpha = 0.5f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = RedLoss),
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Logout, contentDescription = null, tint = RedLoss, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Sign Out", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}

@Composable
private fun UserDataRow(
  icon: ImageVector,
  label: String,
  value: String,
  verified: Boolean = false,
  trailingAction: (@Composable () -> Unit)? = null,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(
      modifier =
        Modifier.size(36.dp)
          .clip(CircleShape)
          .background(DarkCard),
      contentAlignment = Alignment.Center,
    ) {
      Icon(icon, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(text = label, fontSize = 11.sp, color = TextMuted)
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = value, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
        if (verified) {
          Spacer(modifier = Modifier.width(4.dp))
          Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = GreenProfit, modifier = Modifier.size(13.dp))
        }
      }
    }

    trailingAction?.invoke()
  }
}
