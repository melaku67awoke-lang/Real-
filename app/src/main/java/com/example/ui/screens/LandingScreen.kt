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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RealCoinConstants
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.GreenProfit
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.RealCoinViewModel

@Composable
fun LandingScreen(
  viewModel: RealCoinViewModel,
) {
  val uiState by viewModel.uiState.collectAsState()
  LazyColumn(
    modifier =
      Modifier.fillMaxSize()
        .background(DarkBackground)
        .padding(horizontal = 20.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    item {
      Spacer(modifier = Modifier.height(28.dp))

      // Top Navigation Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier =
              Modifier.size(40.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(GoldPrimary, GoldDark))),
            contentAlignment = Alignment.Center,
          ) {
            Text("RC", color = DarkBackground, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "RealCoin",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
          )
        }

        Surface(
          shape = RoundedCornerShape(20.dp),
          color = DarkSurface,
          border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
          modifier = Modifier.clickable { viewModel.navigateTo(AppDestination.ADMIN_LOGIN) },
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              Icons.Default.AdminPanelSettings,
              contentDescription = "Admin Portal",
              tint = GoldPrimary,
              modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Admin Portal",
              color = GoldPrimary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))
    }

    // Hero Section
    item {
      Box(
        modifier =
          Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
              Brush.verticalGradient(
                listOf(
                  GoldPrimary.copy(alpha = 0.18f),
                  DarkSurface,
                )
              )
            )
            .border(1.dp, DarkCardBorder, RoundedCornerShape(24.dp))
            .padding(24.dp),
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = GoldPrimary.copy(alpha = 0.15f),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(14.dp),
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Next-Gen P2P Escrow & Token Ecosystem",
                color = GoldPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
              )
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          Text(
            text = "Empowering Ethiopian &\nGlobal Crypto Finance",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 34.sp,
          )

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "Trade RealCoin securely with zero gas fees. Buy & Sell with Telebirr, CBE, Abyssinia, and deposit USDT via BEP-20 with a 10% token bonus.",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 19.sp,
          )

          Spacer(modifier = Modifier.height(22.dp))

          // Primary CTA Buttons
          Button(
            onClick = { viewModel.navigateTo(AppDestination.REGISTER) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
            shape = RoundedCornerShape(14.dp),
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                "Create Account & Get Started",
                color = DarkBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
              )
              Spacer(modifier = Modifier.width(8.dp))
              Icon(Icons.Default.ArrowForward, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(18.dp))
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedButton(
            onClick = { viewModel.navigateTo(AppDestination.LOGIN) },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
          ) {
            Text("Already registered? Sign In", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }

    // 10% USDT Deposit Bonus Callout
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f)),
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Box(
            modifier =
              Modifier.size(46.dp)
                .clip(CircleShape)
                .background(GoldPrimary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
          ) {
            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
          }
          Spacer(modifier = Modifier.width(14.dp))
          Column {
            Text(
              text = "Exclusive 10% RealCoin Bonus",
              color = GoldPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Deposit USDT via BEP-20 (Min $25 USD) and receive an instant 10% token bonus directly in your wallet.",
              color = TextSecondary,
              fontSize = 11.sp,
              lineHeight = 16.sp,
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }

    // Key Features Grid
    item {
      Text(
        text = "Why Choose RealCoin?",
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        modifier = Modifier.fillMaxWidth(),
      )
      Spacer(modifier = Modifier.height(12.dp))
    }

    item {
      FeatureCard(
        icon = Icons.Default.CurrencyExchange,
        title = "Ethiopian P2P Marketplace",
        description = "Trade with local banking: Telebirr, CBE Birr, CBE, Bank of Abyssinia, Dashen Bank, and Awash Bank.",
      )
      Spacer(modifier = Modifier.height(10.dp))
      FeatureCard(
        icon = Icons.Default.Security,
        title = "Automated Escrow & Dispute Resolution",
        description = "Every trade is secured by smart contract escrow. RealCoin Admin moderates disputes with 24/7 coverage.",
      )
      Spacer(modifier = Modifier.height(10.dp))
      FeatureCard(
        icon = Icons.Default.Speed,
        title = "Fast KYC & Tiered Limits",
        description = "Submit your Ethiopian National ID, Passport, or Kebele ID for instant approval and unlimited daily volume.",
      )
      Spacer(modifier = Modifier.height(32.dp))
    }
  }
}

@Composable
fun FeatureCard(
  icon: ImageVector,
  title: String,
  description: String,
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
        modifier =
          Modifier.size(38.dp)
            .clip(CircleShape)
            .background(DarkCard),
        contentAlignment = Alignment.Center,
      ) {
        Icon(icon, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(text = title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = description, color = TextSecondary, fontSize = 11.sp, lineHeight = 15.sp)
      }
    }
  }
}
