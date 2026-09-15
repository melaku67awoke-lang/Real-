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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.KycStatus
import com.example.data.model.TransactionItem
import com.example.data.model.TransactionType
import com.example.data.model.UserLevel
import com.example.data.model.UserProfile
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.GreenProfit
import com.example.ui.theme.RedLoss
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.RealCoinUiState
import com.example.ui.viewmodel.RealCoinViewModel

/**
 * Requirements:
 * 1. Remove Send & Receive in dashboard, ONLY Deposit and Withdraw.
 * 2. Remove live market rate Bitcoin/ETH prices, ONLY RealCoin Price in USD & ETB (and USD/ETB rate).
 * 3. VIP Tiered Levels based on total deposit volume (cumulative, not one-time):
 *    - Starter ($100 total): 30 REAL/day
 *    - Silver ($150 total): 50 REAL/day
 *    - Bronze ($200 total): 75 REAL/day
 *    - Gold ($250 total): 100 REAL/day
 *    - Diamond ($300 total): 150 REAL/day
 * 4. P2P & Lucky Spin shortcuts.
 */
@Composable
fun HomeScreen(
  viewModel: RealCoinViewModel,
  uiState: RealCoinUiState,
  onNavigateToSpin: () -> Unit,
  onNavigateToP2P: () -> Unit,
  onNavigateToSettings: () -> Unit,
) {
  var showDepositDialog by remember { mutableStateOf(false) }
  var showWithdrawDialog by remember { mutableStateOf(false) }
  var showLevelTiersModal by remember { mutableStateOf(false) }

  LazyColumn(
    modifier =
      Modifier.fillMaxSize()
        .background(DarkBackground)
        .padding(horizontal = 16.dp),
  ) {
    // Header Row with User Info & Admin Shortcut
    item {
      Spacer(modifier = Modifier.height(16.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable { onNavigateToSettings() },
        ) {
          Box(
            modifier =
              Modifier.size(42.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(GoldPrimary, GoldDark))),
            contentAlignment = Alignment.Center,
          ) {
            Text("RC", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = uiState.userProfile.username,
              color = TextPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
            )
            Row(
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Verified",
                tint = GreenProfit,
                modifier = Modifier.size(13.dp),
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Verified Member",
                color = GreenProfit,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
              )
            }
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          // Help Center Link Button
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
            modifier = Modifier.clickable { viewModel.navigateTo(AppDestination.HELP_CENTER) },
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                Icons.Default.HelpOutline,
                contentDescription = "Help Center",
                tint = GoldPrimary,
                modifier = Modifier.size(14.dp),
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("Help", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          // Admin Portal Link Button
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
            modifier = Modifier.clickable { viewModel.navigateTo(AppDestination.ADMIN_LOGIN) },
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                Icons.Default.AdminPanelSettings,
                contentDescription = "Admin",
                tint = GoldPrimary,
                modifier = Modifier.size(14.dp),
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("Admin", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // Prominent Today's Market Value Card in Dashboard
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.TrendingUp, contentDescription = null, tint = GreenProfit, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Today's Market Value",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = GreenProfit.copy(alpha = 0.15f),
            ) {
              Text(
                text = "LIVE TICKER",
                color = GreenProfit,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column {
              Text("1 RealCoin (REAL)", fontSize = 11.sp, color = TextMuted)
              Spacer(modifier = Modifier.height(2.dp))
              Text("$%.4f USD".format(uiState.realPriceUsd), fontSize = 17.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
            }
            Box(modifier = Modifier.width(1.dp).height(36.dp).background(DarkCardBorder))
            Column {
              Text("Value in ETB", fontSize = 11.sp, color = TextMuted)
              Spacer(modifier = Modifier.height(2.dp))
              Text("%.2f Birr".format(uiState.realPriceEtb), fontSize = 17.sp, fontWeight = FontWeight.Bold, color = GreenProfit)
            }
            Box(modifier = Modifier.width(1.dp).height(36.dp).background(DarkCardBorder))
            Column {
              Text("USD / ETB Rate", fontSize = 11.sp, color = TextMuted)
              Spacer(modifier = Modifier.height(2.dp))
              Text("%.2f ETB".format(uiState.usdToEtbRate), fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "• Live rate syncs your dashboard portfolio, P2P orderbook, and deposits/withdrawals",
            fontSize = 10.sp,
            color = TextMuted,
          )
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // Hero Balance Card: Shows balance in RealCoin, USD, and ETB
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
      ) {
        Box(
          modifier =
            Modifier.fillMaxWidth()
              .background(
                Brush.verticalGradient(
                  listOf(
                    GoldPrimary.copy(alpha = 0.14f),
                    DarkSurface,
                  )
                )
              )
              .padding(20.dp),
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "Total Portfolio Balance",
                  color = TextSecondary,
                  fontSize = 12.sp,
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                  onClick = { viewModel.toggleBalanceVisibility() },
                  modifier = Modifier.size(20.dp),
                ) {
                  Icon(
                    imageVector = if (uiState.isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle Balance",
                    tint = TextMuted,
                  )
                }
              }

              Surface(
                color = GoldPrimary.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp),
              ) {
                Text(
                  text = "BSC / BEP-20",
                  color = GoldPrimary,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 1. Balance in REAL COIN
            Text(
              text = if (uiState.isBalanceVisible) "%.2f REAL".format(uiState.realBalance) else "•••••••• REAL",
              fontSize = 28.sp,
              fontWeight = FontWeight.ExtraBold,
              color = GoldPrimary,
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 2. Balance in USD and 3. Balance in ETB
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(16.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = if (uiState.isBalanceVisible) "≈ $%.2f USD".format(uiState.balanceUsd) else "≈ $•••• USD",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
              )
              Text(
                text = "•",
                color = TextMuted,
                fontSize = 14.sp,
              )
              Text(
                text = if (uiState.isBalanceVisible) "≈ %.2f ETB (Birr)".format(uiState.balanceEtb) else "≈ •••• ETB",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GreenProfit,
              )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // User Request: "Remove send and recieve in dashboard, only Deposit and withdraw"
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              // Deposit Button with +10% Bonus indicator
              Button(
                onClick = { showDepositDialog = true },
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(14.dp),
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.TrendingUp, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(20.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text("Deposit", color = DarkBackground, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    Text("+10% Bonus", color = DarkBackground.copy(alpha = 0.85f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }

              // Withdraw Button
              Button(
                onClick = { showWithdrawDialog = true },
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.CurrencyExchange, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text("Withdraw", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("USDT (BEP-20)", color = GoldPrimary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                  }
                }
              }
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // VIP Member Level & Daily Free Reward Card (Requirement 3)
    item {
      VipLevelCard(
        userProfile = uiState.userProfile,
        onClaimDailyReward = { viewModel.claimDailyLevelReward() },
        onDepositClick = { showDepositDialog = true },
        onViewAllTiersClick = { showLevelTiersModal = true },
      )
      Spacer(modifier = Modifier.height(16.dp))
    }

    // 10% Bonus Callout Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth().clickable { showDepositDialog = true },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Box(
            modifier =
              Modifier.size(40.dp)
                .clip(CircleShape)
                .background(GoldPrimary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
          ) {
            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(22.dp))
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Deposit USDT & Get 10% RealCoin Bonus",
              color = GoldPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
            )
            Text(
              text = "BEP-20 address: 0x8e54...9df4 • Min $25 USD",
              color = TextSecondary,
              fontSize = 11.sp,
            )
          }
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = GoldPrimary,
          ) {
            Text(
              text = "Deposit",
              color = DarkBackground,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Shortcuts Banner: P2P Exchange & Spin to Win
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Card(
          modifier = Modifier.weight(1f).clickable { onNavigateToP2P() },
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurface),
          border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Icon(Icons.Default.CurrencyExchange, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("P2P Exchange", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("Telebirr & CBE", color = TextMuted, fontSize = 11.sp)
          }
        }

        Card(
          modifier = Modifier.weight(1f).clickable { onNavigateToSpin() },
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurface),
          border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Icon(Icons.Default.Casino, contentDescription = null, tint = GreenProfit, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Lucky Spin Wheel", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("1 Free / 24h • 10 REAL", color = GreenProfit, fontSize = 11.sp)
          }
        }
      }
      Spacer(modifier = Modifier.height(20.dp))
    }

    // Recent Transactions Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = "Recent Transactions",
          color = TextPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
        )
        Text(
          text = "${uiState.transactions.size} records",
          color = TextMuted,
          fontSize = 12.sp,
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
    }

    // Transactions List
    items(uiState.transactions) { tx ->
      TransactionRow(tx = tx)
      Spacer(modifier = Modifier.height(8.dp))
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }

  // Deposit Dialog (BEP-20 USDT with +10% Bonus)
  if (showDepositDialog) {
    DepositDialog(
      currentBalanceReal = uiState.realBalance,
      realPriceUsd = uiState.realPriceUsd,
      realPriceEtb = uiState.realPriceEtb,
      usdToEtbRate = uiState.usdToEtbRate,
      onDismiss = { showDepositDialog = false },
      onSubmitDepositTx = { usdAmount, txHash ->
        viewModel.submitUsdtBep20Deposit(usdAmount, txHash)
      },
      onInstantDemoCredit = { usdAmount ->
        viewModel.submitInstantDemoDeposit(usdAmount)
      },
    )
  }

  // Withdraw Dialog (Telebirr & CBE)
  if (showWithdrawDialog) {
    WithdrawDialog(
      availableReal = uiState.realBalance,
      realPriceUsd = uiState.realPriceUsd,
      realPriceEtb = uiState.realPriceEtb,
      onDismiss = { showWithdrawDialog = false },
      onWithdrawConfirm = { method, amt, account ->
        viewModel.submitWithdrawal(method, amt, account)
      },
    )
  }

  // VIP Tiers Breakdown Dialog
  if (showLevelTiersModal) {
    VipTiersDialog(
      currentVolume = uiState.userProfile.totalDepositVolumeUsd,
      currentLevel = uiState.userProfile.currentLevel,
      onDismiss = { showLevelTiersModal = false },
      onDepositClick = {
        showLevelTiersModal = false
        showDepositDialog = true
      },
    )
  }
}

/**
 * VIP Level and Daily Free Reward Component
 * Tracks cumulative deposit volume and allows daily reward claiming per level.
 */
@Composable
fun VipLevelCard(
  userProfile: UserProfile,
  onClaimDailyReward: () -> Unit,
  onDepositClick: () -> Unit,
  onViewAllTiersClick: () -> Unit,
) {
  val level = userProfile.currentLevel
  val volume = userProfile.totalDepositVolumeUsd
  val now = System.currentTimeMillis()
  val lastClaim = userProfile.lastDailyRewardClaimTime
  val canClaimReward = level != UserLevel.NONE && (lastClaim == 0L || (now - lastClaim >= 24 * 60 * 60 * 1000L))

  val remainingMillis = if (lastClaim != 0L && !canClaimReward) {
    (24 * 60 * 60 * 1000L) - (now - lastClaim)
  } else 0L
  val remainingHours = (remainingMillis / (1000 * 60 * 60)).coerceAtLeast(0)
  val remainingMins = ((remainingMillis / (1000 * 60)) % 60).coerceAtLeast(0)

  // Next level threshold calculation
  val nextLevelThreshold = when (level) {
    UserLevel.NONE -> 100.0
    UserLevel.STARTER -> 150.0
    UserLevel.SILVER -> 200.0
    UserLevel.BRONZE -> 250.0
    UserLevel.GOLD -> 300.0
    UserLevel.DIAMOND -> 300.0
    else -> 100.0
  }
  val nextLevelTitle = when (level) {
    UserLevel.NONE -> "Starter ($100)"
    UserLevel.STARTER -> "Silver ($150)"
    UserLevel.SILVER -> "Bronze ($200)"
    UserLevel.BRONZE -> "Gold ($250)"
    UserLevel.GOLD -> "Diamond ($300)"
    UserLevel.DIAMOND -> "Max VIP Level"
    else -> "Starter ($100)"
  }
  val progress = if (nextLevelThreshold > 0) (volume / nextLevelThreshold).coerceIn(0.0, 1.0).toFloat() else 1f

  val levelColor = when (level) {
    UserLevel.NONE -> TextMuted
    UserLevel.STARTER -> GreenProfit
    UserLevel.SILVER -> Color(0xFFC0C0C0)
    UserLevel.BRONZE -> Color(0xFFCD7F32)
    UserLevel.GOLD -> GoldPrimary
    UserLevel.DIAMOND -> Color(0xFF60A5FA)
    else -> TextMuted
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, levelColor.copy(alpha = 0.5f)),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header with Level Badge & View All link
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Star, contentDescription = null, tint = levelColor, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = if (level == UserLevel.NONE) "Standard Tier" else "${level.title} VIP",
              color = TextPrimary,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 15.sp,
            )
            Text(
              text = "Cumulative Deposit Volume: $%.2f USD".format(volume),
              fontSize = 11.sp,
              color = GoldPrimary,
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = DarkCard,
          border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
          modifier = Modifier.clickable { onViewAllTiersClick() },
        ) {
          Text(
            text = "View All 5 Tiers",
            fontSize = 10.sp,
            color = GoldPrimary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Progress bar to next VIP level
      if (level != UserLevel.DIAMOND) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(
            text = "Next: $nextLevelTitle",
            fontSize = 11.sp,
            color = TextSecondary,
          )
          Text(
            text = "$%.0f / $%.0f USD (total)".format(volume, nextLevelThreshold),
            fontSize = 11.sp,
            color = TextMuted,
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
          color = levelColor,
          trackColor = DarkCard,
        )
      } else {
        Text(
          text = "★ Diamond VIP Status Unlocked (Maximum Level)",
          color = Color(0xFF60A5FA),
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Daily Free Reward Claim Section
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = DarkCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Daily Free VIP Reward",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
              )
            }
            Text(
              text = if (level == UserLevel.NONE) "0 REAL / day" else "+%.0f REAL / day".format(level.dailyRewardReal),
              color = if (level == UserLevel.NONE) TextMuted else GreenProfit,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 12.sp,
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = when {
              level == UserLevel.NONE -> "Deposit a total volume of $100 to unlock Starter VIP and earn 30 REAL free daily reward!"
              canClaimReward -> "Your daily reward of %.0f REAL is ready to claim today! (Resets every 24h)".format(level.dailyRewardReal)
              else -> "Daily reward already claimed today. Next reward unlocks in %dh %dm.".format(remainingHours, remainingMins)
            },
            fontSize = 11.sp,
            color = TextSecondary,
            lineHeight = 15.sp,
          )

          Spacer(modifier = Modifier.height(10.dp))

          if (level == UserLevel.NONE) {
            Button(
              onClick = onDepositClick,
              modifier = Modifier.fillMaxWidth().height(42.dp),
              colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
              shape = RoundedCornerShape(10.dp),
            ) {
              Text("DEPOSIT USDT TO UNLOCK VIP REWARDS", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          } else {
            Button(
              onClick = onClaimDailyReward,
              enabled = canClaimReward,
              modifier = Modifier.fillMaxWidth().height(42.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = GreenProfit,
                disabledContainerColor = DarkSurface,
              ),
              shape = RoundedCornerShape(10.dp),
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  if (canClaimReward) Icons.Default.CheckCircle else Icons.Default.Lock,
                  contentDescription = null,
                  tint = if (canClaimReward) DarkBackground else TextMuted,
                  modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (canClaimReward) "CLAIM DAILY REWARD (+%.0f REAL)".format(level.dailyRewardReal)
                         else "CLAIMED (Reset in %dh %dm)".format(remainingHours, remainingMins),
                  color = if (canClaimReward) DarkBackground else TextMuted,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                )
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Detailed VIP Tiers Breakdown Dialog showing all 5 levels
 */
@Composable
fun VipTiersDialog(
  currentVolume: Double,
  currentLevel: UserLevel,
  onDismiss: () -> Unit,
  onDepositClick: () -> Unit,
) {
  val tiers = listOf(
    Triple("Starter Level", "$100 Total Deposit Volume", "30 Real per day"),
    Triple("Silver Level", "$150 Total Deposit Volume", "50 Real daily reward"),
    Triple("Bronze Level", "$200 Total Deposit Volume", "75 Real coin per day"),
    Triple("Gold Level", "$250 Total Deposit Volume", "100 Real coin per day"),
    Triple("Diamond Level", "$300 Total Deposit Volume", "150 Real coin per day"),
  )

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier.fillMaxWidth().padding(12.dp),
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = DarkSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
    ) {
      LazyColumn(modifier = Modifier.padding(20.dp)) {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column {
              Text(
                text = "VIP Levels & Daily Rewards",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
              )
              Text(
                text = "Total volume, not one-time deposit",
                fontSize = 11.sp,
                color = GoldPrimary,
              )
            }
            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Your Current Volume: $%.2f USD (%s)".format(currentVolume, currentLevel.title),
            fontSize = 12.sp,
            color = GreenProfit,
            fontWeight = FontWeight.Bold,
          )

          Spacer(modifier = Modifier.height(14.dp))
        }

        items(tiers) { (title, requirement, reward) ->
          val isCurrent = currentLevel.title == title
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isCurrent) GoldPrimary.copy(alpha = 0.15f) else DarkCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isCurrent) GoldPrimary else DarkCardBorder),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column {
                Text(
                  text = title,
                  color = if (isCurrent) GoldPrimary else TextPrimary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                )
                Text(
                  text = requirement,
                  color = TextSecondary,
                  fontSize = 11.sp,
                )
              }
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = GreenProfit.copy(alpha = 0.15f),
              ) {
                Text(
                  text = reward,
                  color = GreenProfit,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                )
              }
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(18.dp))
          Button(
            onClick = onDepositClick,
            modifier = Modifier.fillMaxWidth().height(46.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
            shape = RoundedCornerShape(12.dp),
          ) {
            Text("Deposit USDT to Level Up", color = DarkBackground, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun TransactionRow(tx: TransactionItem) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier =
            Modifier.size(38.dp)
              .clip(CircleShape)
              .background(
                when (tx.type) {
                  TransactionType.DEPOSIT -> GreenProfit.copy(alpha = 0.15f)
                  TransactionType.WITHDRAW -> RedLoss.copy(alpha = 0.15f)
                  TransactionType.P2P_BUY -> GreenProfit.copy(alpha = 0.15f)
                  TransactionType.P2P_SELL -> RedLoss.copy(alpha = 0.15f)
                  TransactionType.REWARD -> GoldPrimary.copy(alpha = 0.15f)
                  TransactionType.SEND -> RedLoss.copy(alpha = 0.15f)
                  TransactionType.RECEIVE -> GreenProfit.copy(alpha = 0.15f)
                  else -> DarkCard
                }
              ),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector =
              when (tx.type) {
                TransactionType.DEPOSIT -> Icons.Default.TrendingUp
                TransactionType.WITHDRAW -> Icons.Default.CurrencyExchange
                TransactionType.P2P_BUY -> Icons.Default.ArrowDownward
                TransactionType.P2P_SELL -> Icons.Default.ArrowUpward
                TransactionType.REWARD -> Icons.Default.Casino
                TransactionType.SEND -> Icons.Default.ArrowUpward
                TransactionType.RECEIVE -> Icons.Default.ArrowDownward
                else -> Icons.Default.TrendingUp
              },
            contentDescription = null,
            tint =
              when (tx.type) {
                TransactionType.DEPOSIT -> GreenProfit
                TransactionType.WITHDRAW -> RedLoss
                TransactionType.P2P_BUY -> GreenProfit
                TransactionType.P2P_SELL -> RedLoss
                TransactionType.REWARD -> GoldPrimary
                TransactionType.SEND -> RedLoss
                TransactionType.RECEIVE -> GreenProfit
                else -> TextPrimary
              },
            modifier = Modifier.size(20.dp),
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = tx.title,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
          )
          Text(
            text = tx.timestamp,
            color = TextMuted,
            fontSize = 11.sp,
          )
        }
      }

      Column(horizontalAlignment = Alignment.End) {
        val isPositive = tx.type in listOf(TransactionType.DEPOSIT, TransactionType.P2P_BUY, TransactionType.REWARD, TransactionType.RECEIVE)
        Text(
          text = "${if (isPositive) "+" else "-"}%.2f REAL".format(tx.amountReal),
          color = if (isPositive) GreenProfit else RedLoss,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
        )
        Text(
          text = "≈ %.2f ETB".format(tx.amountEtb),
          color = TextMuted,
          fontSize = 10.sp,
        )
      }
    }
  }
}
