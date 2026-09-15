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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.KycTier
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GreenProfit
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.RealCoinUiState
import com.example.ui.viewmodel.RealCoinViewModel

@Composable
fun KYCScreen(
  viewModel: RealCoinViewModel,
  uiState: RealCoinUiState,
  onBack: () -> Unit,
) {
  var showVerificationDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier =
      Modifier.fillMaxSize()
        .background(DarkBackground)
        .padding(horizontal = 16.dp),
  ) {
    item {
      Spacer(modifier = Modifier.height(16.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        IconButton(onClick = onBack) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Identity Verification (KYC)",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary,
        )
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Profile Summary Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
      ) {
        Row(
          modifier = Modifier.padding(18.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Box(
            modifier =
              Modifier.size(54.dp)
                .clip(CircleShape)
                .background(GoldPrimary),
            contentAlignment = Alignment.Center,
          ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(32.dp))
          }
          Spacer(modifier = Modifier.width(14.dp))
          Column {
            Text(
              text = uiState.userProfile.username,
              color = TextPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp,
            )
            Text(
              text = uiState.userProfile.email,
              color = TextSecondary,
              fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = GreenProfit, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "${uiState.userProfile.currentKycTier.title} Active",
                color = GreenProfit,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
              )
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(20.dp))
    }

    item {
      Text(
        text = "Verification Tiers & Limits",
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
      )
      Spacer(modifier = Modifier.height(12.dp))
    }

    // Tier 1
    item {
      TierCard(
        tierName = "Tier 1: Basic",
        limit = "$500 USD / Day",
        perks = listOf("Email & Phone verified", "Standard P2P trading", "Instant RealChain transfers"),
        isCompleted = true,
        isActive = false,
        onUpgrade = {},
      )
      Spacer(modifier = Modifier.height(12.dp))
    }

    // Tier 2
    item {
      TierCard(
        tierName = "Tier 2: Advanced Identity",
        limit = "$25,000 USD / Day",
        perks = listOf("National ID or Passport", "Higher P2P withdrawal limits", "Merchant Express Escrow access"),
        isCompleted = uiState.userProfile.currentKycTier.tierLevel >= 2,
        isActive = uiState.userProfile.currentKycTier == KycTier.TIER_2,
        onUpgrade = {},
      )
      Spacer(modifier = Modifier.height(12.dp))
    }

    // Tier 3
    item {
      TierCard(
        tierName = "Tier 3: Pro Merchant",
        limit = "Unlimited Volume",
        perks = listOf("Proof of Address + Live Selfie", "0% P2P maker fee", "Priority 24/7 dedicated account manager"),
        isCompleted = uiState.userProfile.currentKycTier.tierLevel >= 3,
        isActive = uiState.userProfile.currentKycTier == KycTier.TIER_3,
        onUpgrade = { showVerificationDialog = true },
      )
      Spacer(modifier = Modifier.height(80.dp))
    }
  }

  if (showVerificationDialog) {
    KycSubmitDialog(
      onDismiss = { showVerificationDialog = false },
      onSubmit = { name, docType, docNum ->
        viewModel.submitKycUpgrade(name, docType, docNum)
        showVerificationDialog = false
      },
    )
  }
}

@Composable
fun TierCard(
  tierName: String,
  limit: String,
  perks: List<String>,
  isCompleted: Boolean,
  isActive: Boolean,
  onUpgrade: () -> Unit,
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    border =
      androidx.compose.foundation.BorderStroke(
        1.dp,
        if (isCompleted) GreenProfit.copy(alpha = 0.5f) else DarkCardBorder,
      ),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(text = tierName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (isCompleted) GreenProfit.copy(alpha = 0.15f) else DarkCard,
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Lock,
              contentDescription = null,
              tint = if (isCompleted) GreenProfit else TextMuted,
              modifier = Modifier.size(14.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isCompleted) "Verified" else "Available",
              color = if (isCompleted) GreenProfit else TextSecondary,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(text = "Daily Limit: $limit", color = GoldPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

      Spacer(modifier = Modifier.height(10.dp))

      perks.forEach { perk ->
        Row(
          modifier = Modifier.padding(vertical = 2.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(TextSecondary))
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = perk, color = TextSecondary, fontSize = 12.sp)
        }
      }

      if (!isCompleted) {
        Spacer(modifier = Modifier.height(14.dp))
        Button(
          onClick = onUpgrade,
          modifier = Modifier.fillMaxWidth().height(40.dp),
          colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
          shape = RoundedCornerShape(10.dp),
        ) {
          Text("Upgrade to Pro Tier 3", color = DarkBackground, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun KycSubmitDialog(
  onDismiss: () -> Unit,
  onSubmit: (String, String, String) -> Unit,
) {
  var fullName by remember { mutableStateOf("") }
  var docType by remember { mutableStateOf("National ID / Passport") }
  var docNumber by remember { mutableStateOf("") }
  var isPhotoCaptured by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = DarkSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "Pro Tier 3 Verification",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = fullName,
          onValueChange = { fullName = it },
          label = { Text("Full Legal Name") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors =
            OutlinedTextFieldDefaults.colors(
              focusedBorderColor = GoldPrimary,
              unfocusedBorderColor = DarkCardBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
            ),
          shape = RoundedCornerShape(12.dp),
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = docNumber,
          onValueChange = { docNumber = it },
          label = { Text("Document ID Number") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors =
            OutlinedTextFieldDefaults.colors(
              focusedBorderColor = GoldPrimary,
              unfocusedBorderColor = DarkCardBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
            ),
          shape = RoundedCornerShape(12.dp),
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Capture Document Box
        Box(
          modifier =
            Modifier.fillMaxWidth()
              .height(90.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(DarkCard)
              .border(1.dp, if (isPhotoCaptured) GreenProfit else DarkCardBorder, RoundedCornerShape(12.dp))
              .clickable { isPhotoCaptured = !isPhotoCaptured },
          contentAlignment = Alignment.Center,
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              if (isPhotoCaptured) Icons.Default.CheckCircle else Icons.Default.CameraAlt,
              contentDescription = null,
              tint = if (isPhotoCaptured) GreenProfit else GoldPrimary,
              modifier = Modifier.size(28.dp),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = if (isPhotoCaptured) "Document & Selfie Attached ✓" else "Tap to Capture ID & Live Selfie",
              color = if (isPhotoCaptured) GreenProfit else TextSecondary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            if (fullName.isNotBlank() && docNumber.isNotBlank()) {
              onSubmit(fullName, docType, docNumber)
            }
          },
          modifier = Modifier.fillMaxWidth().height(48.dp),
          colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
          shape = RoundedCornerShape(12.dp),
        ) {
          Text("Submit & Verify Now", color = DarkBackground, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
