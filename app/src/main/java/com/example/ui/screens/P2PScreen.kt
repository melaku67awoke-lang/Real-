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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.EscrowOrder
import com.example.data.model.EscrowStatus
import com.example.data.model.P2PAd
import com.example.data.model.P2PTradeType
import com.example.data.model.RealCoinConstants
import com.example.data.model.UserPaymentAccount
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
import com.example.ui.viewmodel.RealCoinUiState
import com.example.ui.viewmodel.RealCoinViewModel

@Composable
fun P2PScreen(
  viewModel: RealCoinViewModel,
  uiState: RealCoinUiState,
) {
  var selectedMainTab by remember { mutableIntStateOf(0) } // 0: Market, 1: My Ads & Payment Accounts, 2: Orders
  var selectedTradeFilter by remember { mutableStateOf(P2PTradeType.BUY) }
  var selectedAdForTrade by remember { mutableStateOf<P2PAd?>(null) }
  var showCreateAdDialog by remember { mutableStateOf(false) }
  var showAddPaymentDialog by remember { mutableStateOf(false) }
  var myAdsSubTab by remember { mutableIntStateOf(0) } // 0: My Ads, 1: Payment Accounts
  var disputeOrderId by remember { mutableStateOf<String?>(null) }
  val hasActiveOrders = uiState.activeEscrowOrders.any {
    it.status == EscrowStatus.PENDING_PAYMENT ||
    it.status == EscrowStatus.PAID_PENDING_RELEASE ||
    it.status == EscrowStatus.DISPUTED
  }

  LazyColumn(
    modifier =
      Modifier.fillMaxSize()
        .background(DarkBackground)
        .padding(horizontal = 16.dp),
  ) {
    // Header
    item {
      Spacer(modifier = Modifier.height(16.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column {
          Text(
            text = "P2P Express Escrow",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
          )
          Text(
            text = "Ethiopian Banks & Telebirr • Zero Gas Fees",
            fontSize = 11.sp,
            color = TextSecondary,
          )
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = GoldPrimary.copy(alpha = 0.15f),
          border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(Icons.Default.Security, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Escrow Protected", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
      Spacer(modifier = Modifier.height(12.dp))
    }

    // Today's Market Value Ticker Card (Requirement 5)
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = GreenProfit, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Today's Rate:", fontSize = 12.sp, color = TextMuted)
            Spacer(modifier = Modifier.width(4.dp))
            Text("1 REAL = %.2f ETB ($%.4f)".format(uiState.realPriceEtb, uiState.realPriceUsd), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
          }
          Text("%.2f ETB/USD".format(uiState.usdToEtbRate), fontSize = 11.sp, color = GreenProfit, fontWeight = FontWeight.SemiBold)
        }
      }
      Spacer(modifier = Modifier.height(12.dp))
    }

    // 3 Main Navigation Tabs: Market, My Ads, Orders
    item {
      TabRow(
        selectedTabIndex = selectedMainTab,
        containerColor = DarkSurface,
        contentColor = GoldPrimary,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedMainTab]),
            color = GoldPrimary,
          )
        },
        modifier = Modifier.clip(RoundedCornerShape(12.dp)),
      ) {
        Tab(
          selected = selectedMainTab == 0,
          onClick = { selectedMainTab = 0 },
          text = { Text("Market", fontWeight = FontWeight.Bold) },
        )
        Tab(
          selected = selectedMainTab == 1,
          onClick = { selectedMainTab = 1 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("My Ads", fontWeight = FontWeight.Bold)
              if (uiState.userAds.isNotEmpty()) {
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                  shape = CircleShape,
                  color = GoldPrimary,
                  modifier = Modifier.size(16.dp),
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(text = "${uiState.userAds.size}", fontSize = 9.sp, color = DarkBackground, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          },
        )
        Tab(
          selected = selectedMainTab == 2,
          onClick = { selectedMainTab = 2 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("Escrow Orders", fontWeight = FontWeight.Bold)
              if (uiState.activeEscrowOrders.isNotEmpty()) {
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                  shape = CircleShape,
                  color = GreenProfit,
                  modifier = Modifier.size(16.dp),
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(text = "${uiState.activeEscrowOrders.size}", fontSize = 9.sp, color = DarkBackground, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          },
        )
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // TAB 0: MARKETPLACE
    if (selectedMainTab == 0) {
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Button(
            onClick = { selectedTradeFilter = P2PTradeType.BUY },
            modifier = Modifier.weight(1f).height(40.dp),
            colors =
              ButtonDefaults.buttonColors(
                containerColor = if (selectedTradeFilter == P2PTradeType.BUY) GreenProfit else DarkSurface,
              ),
            shape = RoundedCornerShape(10.dp),
          ) {
            Text(
              "Buy REAL",
              color = if (selectedTradeFilter == P2PTradeType.BUY) DarkBackground else TextSecondary,
              fontWeight = FontWeight.Bold,
            )
          }

          Button(
            onClick = { selectedTradeFilter = P2PTradeType.SELL },
            modifier = Modifier.weight(1f).height(40.dp),
            colors =
              ButtonDefaults.buttonColors(
                containerColor = if (selectedTradeFilter == P2PTradeType.SELL) RedLoss else DarkSurface,
              ),
            shape = RoundedCornerShape(10.dp),
          ) {
            Text(
              "Sell REAL",
              color = if (selectedTradeFilter == P2PTradeType.SELL) Color.White else TextSecondary,
              fontWeight = FontWeight.Bold,
            )
          }
        }
        Spacer(modifier = Modifier.height(14.dp))
      }

      // User wants to BUY REAL -> show SELL ads posted by merchants
      // User wants to SELL REAL -> show BUY ads posted by merchants
      val targetMakerType = if (selectedTradeFilter == P2PTradeType.BUY) P2PTradeType.SELL else P2PTradeType.BUY
      val filteredAds = uiState.p2pAds.filter { it.tradeType == targetMakerType && it.isActive }
      items(filteredAds) { ad ->
        P2PAdCard(
          ad = ad,
          isTakerBuy = (selectedTradeFilter == P2PTradeType.BUY),
          onTradeClick = { selectedAdForTrade = ad },
        )
        Spacer(modifier = Modifier.height(10.dp))
      }
    }

    // TAB 1: MY ADS & PAYMENT ACCOUNTS
    if (selectedMainTab == 1) {
      item {
        // Sub-tabs: My Ads vs Payment Accounts
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Button(
            onClick = { myAdsSubTab = 0 },
            modifier = Modifier.weight(1f).height(38.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (myAdsSubTab == 0) GoldPrimary else DarkSurface,
            ),
            shape = RoundedCornerShape(10.dp),
          ) {
            Text(
              "My Ads (${uiState.userAds.size})",
              color = if (myAdsSubTab == 0) DarkBackground else TextSecondary,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
            )
          }

          Button(
            onClick = { myAdsSubTab = 1 },
            modifier = Modifier.weight(1f).height(38.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (myAdsSubTab == 1) GoldPrimary else DarkSurface,
            ),
            shape = RoundedCornerShape(10.dp),
          ) {
            Text(
              "Payment Accounts (${uiState.userProfile.paymentAccounts.size})",
              color = if (myAdsSubTab == 1) DarkBackground else TextSecondary,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
            )
          }
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (hasActiveOrders) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = RedLoss.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, RedLoss.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(Icons.Default.Lock, contentDescription = null, tint = RedLoss, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Active escrow order in progress. Ad & account deletions are protected.", color = RedLoss, fontSize = 11.sp)
            }
          }
          Spacer(modifier = Modifier.height(10.dp))
        }
      }

      if (myAdsSubTab == 0) {
        item {
          // Create New Ad Banner
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
          ) {
            Row(
              modifier = Modifier.padding(16.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column {
                Text(
                  text = "Merchant Ad Center",
                  color = TextPrimary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                )
                Text(
                  text = "Sell or Buy ads in Ethiopian Birr (ETB)",
                  color = TextSecondary,
                  fontSize = 11.sp,
                )
              }

              Button(
                onClick = { showCreateAdDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(10.dp),
              ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Post Ad", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            }
          }
          Spacer(modifier = Modifier.height(14.dp))
        }

        if (uiState.userAds.isEmpty()) {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = DarkSurface),
            ) {
              Column(
                modifier = Modifier.padding(28.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
              ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = TextMuted, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text("No active ads posted yet", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Create your first ad to sell or buy REAL with Ethiopian banks", color = TextSecondary, fontSize = 11.sp)
              }
            }
          }
        } else {
          items(uiState.userAds) { userAd ->
            UserAdCard(
              ad = userAd,
              hasActiveOrders = hasActiveOrders,
              onToggleActive = { viewModel.toggleAdActive(userAd.id) },
              onDelete = { viewModel.deleteMyAd(userAd.id) },
              onBlockedDelete = { viewModel.showToast("Cannot delete ad while an escrow order is active.") },
            )
            Spacer(modifier = Modifier.height(10.dp))
          }
        }
      } else {
        // myAdsSubTab == 1: PAYMENT ACCOUNTS
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
          ) {
            Row(
              modifier = Modifier.padding(16.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column {
                Text(
                  text = "Ethiopian Payment Accounts",
                  color = TextPrimary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                )
                Text(
                  text = "Linked to National ID: ${uiState.userProfile.kycSubmission?.fullName ?: uiState.userProfile.username}",
                  color = GreenProfit,
                  fontSize = 11.sp,
                )
              }

              Button(
                onClick = { showAddPaymentDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(10.dp),
              ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            }
          }
          Spacer(modifier = Modifier.height(14.dp))
        }

        if (uiState.userProfile.paymentAccounts.isEmpty()) {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = DarkSurface),
            ) {
              Column(
                modifier = Modifier.padding(28.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
              ) {
                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text("No payment accounts added yet", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Add your Ethiopian bank or Telebirr account to receive payments safely", color = TextSecondary, fontSize = 11.sp)
              }
            }
          }
        } else {
          items(uiState.userProfile.paymentAccounts) { account ->
            PaymentAccountCard(
              account = account,
              hasActiveOrders = hasActiveOrders,
              onDelete = { viewModel.deletePaymentAccount(account.id) },
              onBlockedDelete = { viewModel.showToast("Cannot delete payment account while an escrow order is active.") },
            )
            Spacer(modifier = Modifier.height(10.dp))
          }
        }
      }
    }

    // TAB 2: ESCROW ORDERS
    if (selectedMainTab == 2) {
      if (uiState.activeEscrowOrders.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
          ) {
            Column(
              modifier = Modifier.padding(28.dp).fillMaxWidth(),
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              Icon(Icons.Default.Lock, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
              Spacer(modifier = Modifier.height(10.dp))
              Text("No active escrow orders", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text("Initiate a buy or sell trade from the Market tab", color = TextSecondary, fontSize = 11.sp)
            }
          }
        }
      } else {
        items(uiState.activeEscrowOrders) { order ->
          EscrowOrderCard(
            order = order,
            onMarkPaid = { viewModel.markOrderAsPaid(order.orderId) },
            onRelease = { viewModel.releaseEscrowCoins(order.orderId) },
            onDispute = { disputeOrderId = order.orderId },
          )
          Spacer(modifier = Modifier.height(12.dp))
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }

  // Create Ad Dialog (Requirement 6)
  if (showCreateAdDialog) {
    CreateAdDialog(
      realPriceEtb = uiState.realPriceEtb,
      onDismiss = { showCreateAdDialog = false },
      onSubmitAd = { tradeType, amount, price, minLimit, maxLimit, paymentMethods ->
        viewModel.createMyAd(tradeType, amount, price, minLimit, maxLimit, paymentMethods)
        showCreateAdDialog = false
      },
    )
  }

  // Add Payment Account Dialog
  if (showAddPaymentDialog) {
    AddPaymentAccountDialog(
      nationalIdName = uiState.userProfile.kycSubmission?.fullName ?: uiState.userProfile.username,
      onDismiss = { showAddPaymentDialog = false },
      onSaveAccount = { bank, accNum ->
        viewModel.addPaymentAccount(bank, accNum)
        showAddPaymentDialog = false
      },
    )
  }

  // Trade Modal
  selectedAdForTrade?.let { ad ->
    P2PTradeDialog(
      ad = ad,
      isTakerBuy = (selectedTradeFilter == P2PTradeType.BUY),
      onDismiss = { selectedAdForTrade = null },
      onConfirmOrder = { cryptoAmt, fiatAmt ->
        viewModel.createP2POrder(ad, cryptoAmt, fiatAmt, isTakerBuy = (selectedTradeFilter == P2PTradeType.BUY))
        selectedAdForTrade = null
        selectedMainTab = 2 // Switch to orders tab
      },
    )
  }

  // Dispute Dialog
  disputeOrderId?.let { orderId ->
    DisputeDialog(
      orderId = orderId,
      onDismiss = { disputeOrderId = null },
      onSubmitDispute = { reason ->
        viewModel.raiseDispute(orderId, reason)
        disputeOrderId = null
      },
    )
  }
}

@Composable
fun P2PAdCard(
  ad: P2PAd,
  isTakerBuy: Boolean,
  onTradeClick: () -> Unit,
) {
  val isBuy = isTakerBuy

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(DarkCard),
            contentAlignment = Alignment.Center,
          ) {
            Text(ad.traderName.take(2).uppercase(), color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = ad.traderName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Spacer(modifier = Modifier.width(4.dp))
              Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = GreenProfit, modifier = Modifier.size(12.dp))
            }
            Text(
              text = "${ad.completedOrders} orders • ${ad.completionRate}% completion",
              color = TextMuted,
              fontSize = 10.sp,
            )
          }
        }

        // Price per unit in ETB (Today's market value ~5.00 ETB)
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "%.2f %s".format(ad.pricePerUnit, ad.fiatCurrency),
            color = if (isBuy) GreenProfit else GoldPrimary,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
          )
          Text(text = "per ${ad.cryptoSymbol}", color = TextMuted, fontSize = 10.sp)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Column {
          Text("Available:", fontSize = 10.sp, color = TextMuted)
          Text("%.0f REAL".format(ad.availableCrypto), fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
        }
        Column {
          Text("Order Limit:", fontSize = 10.sp, color = TextMuted)
          Text("%.0f - %.0f ETB".format(ad.minLimit, ad.maxLimit), fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Payment Methods Row & Action Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier.weight(1f),
        ) {
          ad.paymentMethods.take(3).forEach { method ->
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = DarkCard,
            ) {
              Text(
                text = method,
                color = GoldPrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              )
            }
          }
        }

        Button(
          onClick = onTradeClick,
          colors =
            ButtonDefaults.buttonColors(
              containerColor = if (isBuy) GreenProfit else RedLoss,
            ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.height(34.dp),
        ) {
          Text(if (isBuy) "Buy REAL" else "Sell REAL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
      }
    }
  }
}

/**
 * Requirement 6: My Ads Card with Active Toggle & Protected Delete
 */
@Composable
fun UserAdCard(
  ad: P2PAd,
  hasActiveOrders: Boolean,
  onToggleActive: () -> Unit,
  onDelete: () -> Unit,
  onBlockedDelete: () -> Unit,
) {
  val isBuy = ad.tradeType == P2PTradeType.BUY

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, if (ad.isActive) GoldPrimary.copy(alpha = 0.5f) else DarkCardBorder),
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (isBuy) GreenProfit.copy(alpha = 0.2f) else RedLoss.copy(alpha = 0.2f),
          ) {
            Text(
              text = if (isBuy) "BUY AD (for Sellers)" else "SELL AD (for Buyers)",
              color = if (isBuy) GreenProfit else RedLoss,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "%.2f ETB / REAL".format(ad.pricePerUnit),
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = if (ad.isActive) "Active" else "Paused",
            color = if (ad.isActive) GreenProfit else TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
          )
          Spacer(modifier = Modifier.width(6.dp))
          Switch(
            checked = ad.isActive,
            onCheckedChange = { onToggleActive() },
            colors = SwitchDefaults.colors(checkedThumbColor = GoldPrimary, checkedTrackColor = DarkCard),
            modifier = Modifier.size(36.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          IconButton(
            onClick = { if (hasActiveOrders) onBlockedDelete() else onDelete() },
            modifier = Modifier.size(24.dp),
          ) {
            Icon(
              Icons.Default.Delete,
              contentDescription = "Delete",
              tint = if (hasActiveOrders) TextMuted else RedLoss,
              modifier = Modifier.size(18.dp),
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text("Stock: %.0f REAL".format(ad.availableCrypto), fontSize = 11.sp, color = TextSecondary)
        Text("Limits: %.0f - %.0f ETB".format(ad.minLimit, ad.maxLimit), fontSize = 11.sp, color = TextSecondary)
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        ad.paymentMethods.forEach { m ->
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = DarkCard,
          ) {
            Text(
              text = m,
              color = GoldPrimary,
              fontSize = 9.sp,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            )
          }
        }
      }
    }
  }
}

/**
 * Payment Account Card with Ethiopian Bank details and Protected Delete
 */
@Composable
fun PaymentAccountCard(
  account: UserPaymentAccount,
  hasActiveOrders: Boolean,
  onDelete: () -> Unit,
  onBlockedDelete: () -> Unit,
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
  ) {
    Row(
      modifier = Modifier.padding(14.dp).fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f),
      ) {
        Box(
          modifier = Modifier.size(42.dp).clip(CircleShape).background(GoldPrimary.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center,
        ) {
          Icon(Icons.Default.AccountBalance, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(account.bankName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Text(account.accountNumber, color = GoldLight, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Verified, contentDescription = null, tint = GreenProfit, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(account.accountName, color = TextMuted, fontSize = 11.sp)
          }
        }
      }

      IconButton(
        onClick = { if (hasActiveOrders) onBlockedDelete() else onDelete() },
        modifier = Modifier.size(32.dp),
      ) {
        Icon(
          Icons.Default.Delete,
          contentDescription = "Delete Account",
          tint = if (hasActiveOrders) TextMuted else RedLoss,
          modifier = Modifier.size(20.dp),
        )
      }
    }
  }
}

/**
 * Requirement 6: Create Ad Dialog with Ethiopian Payment Methods:
 * Telebirr, CBE Birr, CBE, Abyssinia, Dashen, Awash
 */
@Composable
fun CreateAdDialog(
  realPriceEtb: Double = 5.0,
  onDismiss: () -> Unit,
  onSubmitAd: (P2PTradeType, Double, Double, Double, Double, List<String>) -> Unit,
) {
  var tradeType by remember { mutableStateOf(P2PTradeType.SELL) }
  var cryptoAmountText by remember { mutableStateOf("5000") }
  var pricePerUnitText by remember(realPriceEtb) { mutableStateOf("%.2f".format(realPriceEtb)) } // Today's market value: 5 birr
  var minLimitText by remember { mutableStateOf("500") }
  var maxLimitText by remember { mutableStateOf("25000") }
  var selectedPaymentMethods by remember { mutableStateOf(setOf("Telebirr", "CBE")) }
  var formError by remember { mutableStateOf<String?>(null) }

  val allMethods = RealCoinConstants.ETHIOPIAN_PAYMENT_METHODS

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier.fillMaxWidth().padding(12.dp),
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = DarkSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
    ) {
      LazyColumn(modifier = Modifier.padding(20.dp)) {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = "Post P2P Advertisement",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary,
            )
            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Buy or Sell Selector
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = { tradeType = P2PTradeType.SELL },
              modifier = Modifier.weight(1f).height(38.dp),
              colors = ButtonDefaults.buttonColors(containerColor = if (tradeType == P2PTradeType.SELL) RedLoss else DarkCard),
              shape = RoundedCornerShape(8.dp),
            ) {
              Text("I want to SELL REAL", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Button(
              onClick = { tradeType = P2PTradeType.BUY },
              modifier = Modifier.weight(1f).height(38.dp),
              colors = ButtonDefaults.buttonColors(containerColor = if (tradeType == P2PTradeType.BUY) GreenProfit else DarkCard),
              shape = RoundedCornerShape(8.dp),
            ) {
              Text("I want to BUY REAL", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Price per REAL in ETB (defaults to Today's Market Value 5.00 ETB)
          OutlinedTextField(
            value = pricePerUnitText,
            onValueChange = { pricePerUnitText = it },
            label = { Text("Price per REAL (ETB)") },
            placeholder = { Text("5.00 ETB", color = TextMuted) },
            trailingIcon = { Text("ETB", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(end = 8.dp)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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

          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text("Market rate: %.2f ETB".format(realPriceEtb), fontSize = 11.sp, color = GreenProfit)
            Text(
              text = "Use Market Rate",
              fontSize = 11.sp,
              color = GoldPrimary,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.clickable { pricePerUnitText = "%.2f".format(realPriceEtb) },
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Amount of REAL
          OutlinedTextField(
            value = cryptoAmountText,
            onValueChange = { cryptoAmountText = it },
            label = { Text("Total REAL Available") },
            trailingIcon = { Text("REAL", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(end = 8.dp)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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

          // Limits
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = minLimitText,
              onValueChange = { minLimitText = it },
              label = { Text("Min Limit (ETB)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              singleLine = true,
              modifier = Modifier.weight(1f),
              colors =
                OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = GoldPrimary,
                  unfocusedBorderColor = DarkCardBorder,
                  focusedTextColor = TextPrimary,
                  unfocusedTextColor = TextPrimary,
                ),
              shape = RoundedCornerShape(12.dp),
            )
            OutlinedTextField(
              value = maxLimitText,
              onValueChange = { maxLimitText = it },
              label = { Text("Max Limit (ETB)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              singleLine = true,
              modifier = Modifier.weight(1f),
              colors =
                OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = GoldPrimary,
                  unfocusedBorderColor = DarkCardBorder,
                  focusedTextColor = TextPrimary,
                  unfocusedTextColor = TextPrimary,
                ),
              shape = RoundedCornerShape(12.dp),
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text("Select Supported Ethiopian Payment Methods", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
          Spacer(modifier = Modifier.height(6.dp))

          // Checkboxes for all Ethiopian banks & Telebirr
          allMethods.forEach { method ->
            val isChecked = selectedPaymentMethods.contains(method)
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier =
                Modifier.fillMaxWidth().clickable {
                  selectedPaymentMethods =
                    if (isChecked) selectedPaymentMethods - method
                    else selectedPaymentMethods + method
                }.padding(vertical = 4.dp),
            ) {
              Checkbox(
                checked = isChecked,
                onCheckedChange = {
                  selectedPaymentMethods =
                    if (it) selectedPaymentMethods + method
                    else selectedPaymentMethods - method
                },
                colors = CheckboxDefaults.colors(checkedColor = GoldPrimary, checkmarkColor = DarkBackground),
              )
              Text(text = method, color = TextPrimary, fontSize = 12.sp)
            }
          }

          if (formError != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = formError!!, color = Color.Red, fontSize = 12.sp)
          }

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = {
              val cryptoAmt = cryptoAmountText.toDoubleOrNull()
              val price = pricePerUnitText.toDoubleOrNull()
              val minL = minLimitText.toDoubleOrNull()
              val maxL = maxLimitText.toDoubleOrNull()

              if (cryptoAmt == null || cryptoAmt <= 0) {
                formError = "Please enter a valid crypto amount"
              } else if (price == null || price <= 0) {
                formError = "Please enter valid price in ETB"
              } else if (selectedPaymentMethods.isEmpty()) {
                formError = "Please select at least one Ethiopian payment method"
              } else {
                onSubmitAd(
                  tradeType,
                  cryptoAmt,
                  price,
                  minL ?: 500.0,
                  maxL ?: (cryptoAmt * price),
                  selectedPaymentMethods.toList(),
                )
              }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
            shape = RoundedCornerShape(12.dp),
          ) {
            Text("Publish Advertisement", color = DarkBackground, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun P2PTradeDialog(
  ad: P2PAd,
  isTakerBuy: Boolean = (ad.tradeType == P2PTradeType.SELL),
  onDismiss: () -> Unit,
  onConfirmOrder: (Double, Double) -> Unit,
) {
  var cryptoAmountText by remember { mutableStateOf("1000") }
  val cryptoAmt = cryptoAmountText.toDoubleOrNull() ?: 0.0
  val fiatAmt = cryptoAmt * ad.pricePerUnit
  val isBuy = isTakerBuy

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier.fillMaxWidth().padding(14.dp),
      shape = RoundedCornerShape(22.dp),
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
            text = "${if (isBuy) "Buy" else "Sell"} REAL ${if (isBuy) "from" else "to"} ${ad.traderName}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Unit Price: %.2f ETB per REAL".format(ad.pricePerUnit),
          color = GoldPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = cryptoAmountText,
          onValueChange = { cryptoAmountText = it },
          label = { Text("Amount of REAL") },
          trailingIcon = { Text("REAL", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(end = 8.dp)) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = DarkCard,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Text(if (isBuy) "You will pay:" else "You will receive:", fontSize = 12.sp, color = TextMuted)
            Text("%.2f ETB (Birr)".format(fiatAmt), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GreenProfit)
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = {
            if (cryptoAmt > 0) {
              onConfirmOrder(cryptoAmt, fiatAmt)
            }
          },
          modifier = Modifier.fillMaxWidth().height(48.dp),
          colors =
            ButtonDefaults.buttonColors(
              containerColor = if (isBuy) GreenProfit else RedLoss,
            ),
          shape = RoundedCornerShape(12.dp),
        ) {
          Text(
            if (isBuy) "Lock Escrow & Buy REAL" else "Lock Escrow & Sell REAL",
            color = Color.White,
            fontWeight = FontWeight.Bold,
          )
        }
      }
    }
  }
}

/**
 * Requirement: Add Payment Account Dialog with Ethiopian payment bank drop down menu,
 * user account number, and account name locked as National ID name.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPaymentAccountDialog(
  nationalIdName: String,
  onDismiss: () -> Unit,
  onSaveAccount: (bank: String, accountNumber: String) -> Unit,
) {
  val ethiopianBanks = listOf(
    "Telebirr",
    "CBE Birr",
    "Commercial Bank of Ethiopia (CBE)",
    "Bank of Abyssinia (BOA)",
    "Dashen Bank",
    "Awash Bank",
    "Wegagen Bank",
    "Nib International Bank",
    "Hibret Bank",
    "Cooperative Bank of Oromia",
  )
  var selectedBank by remember { mutableStateOf(ethiopianBanks[0]) }
  var isDropdownExpanded by remember { mutableStateOf(false) }
  var accountNumberText by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier.fillMaxWidth().padding(12.dp),
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = DarkSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text("Add Payment Account", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Ethiopian Bank Dropdown
        Text("Select Ethiopian Bank / Payment Method", fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(6.dp))

        ExposedDropdownMenuBox(
          expanded = isDropdownExpanded,
          onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
        ) {
          OutlinedTextField(
            value = selectedBank,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = GoldPrimary,
              unfocusedBorderColor = DarkCardBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
            ),
            shape = RoundedCornerShape(12.dp),
          )
          ExposedDropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false },
            modifier = Modifier.background(DarkSurface),
          ) {
            ethiopianBanks.forEach { bank ->
              DropdownMenuItem(
                text = { Text(bank, color = TextPrimary) },
                onClick = {
                  selectedBank = bank
                  isDropdownExpanded = false
                },
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Account Number
        Text("Account / Phone Number", fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = accountNumberText,
          onValueChange = { accountNumberText = it; errorMessage = null },
          placeholder = { Text("e.g. 100012345678 or 0912345678", color = TextMuted) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GoldPrimary,
            unfocusedBorderColor = DarkCardBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
          ),
          shape = RoundedCornerShape(12.dp),
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Account Name (National ID Name - Read-only / Locked)
        Text("Account Name (National ID Name)", fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = DarkCard,
          border = androidx.compose.foundation.BorderStroke(1.dp, GreenProfit.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(Icons.Default.Verified, contentDescription = null, tint = GreenProfit, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(nationalIdName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Text("Locked to verified National ID legal name", color = TextMuted, fontSize = 10.sp)
            }
          }
        }

        if (errorMessage != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(errorMessage!!, color = RedLoss, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = {
            if (accountNumberText.trim().length < 4) {
              errorMessage = "Please enter a valid account or phone number"
            } else {
              onSaveAccount(selectedBank, accountNumberText.trim())
            }
          },
          modifier = Modifier.fillMaxWidth().height(48.dp),
          colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
          shape = RoundedCornerShape(12.dp),
        ) {
          Text("Save Payment Account", color = DarkBackground, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun EscrowOrderCard(
  order: EscrowOrder,
  onMarkPaid: () -> Unit,
  onRelease: () -> Unit,
  onDispute: () -> Unit,
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    border =
      androidx.compose.foundation.BorderStroke(
        1.dp,
        when (order.status) {
          EscrowStatus.COMPLETED -> GreenProfit.copy(alpha = 0.5f)
          EscrowStatus.DISPUTED -> RedLoss.copy(alpha = 0.7f)
          else -> GoldPrimary.copy(alpha = 0.5f)
        },
      ),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column {
          Text(text = "Order #${order.orderId}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Text(text = "Trader: ${order.traderName} • Method: ${order.paymentMethod}", color = TextMuted, fontSize = 11.sp)
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color =
            when (order.status) {
              EscrowStatus.COMPLETED -> GreenProfit.copy(alpha = 0.2f)
              EscrowStatus.DISPUTED -> RedLoss.copy(alpha = 0.2f)
              EscrowStatus.PAID_PENDING_RELEASE -> GoldPrimary.copy(alpha = 0.2f)
              else -> DarkCard
            },
        ) {
          Text(
            text = order.status.name.replace("_", " "),
            color =
              when (order.status) {
                EscrowStatus.COMPLETED -> GreenProfit
                EscrowStatus.DISPUTED -> RedLoss
                else -> GoldPrimary
              },
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Column {
          Text("Crypto Locked in Escrow", fontSize = 10.sp, color = TextMuted)
          Text("%.2f REAL".format(order.cryptoAmount), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
        }
        Column(horizontalAlignment = Alignment.End) {
          Text("Fiat Payment", fontSize = 10.sp, color = TextMuted)
          Text("%.2f ETB".format(order.fiatAmount), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GreenProfit)
        }
      }

      if (order.disputeReason != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Dispute note: ${order.disputeReason}", color = RedLoss, fontSize = 11.sp)
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Action Buttons depending on status
      when (order.status) {
        EscrowStatus.PENDING_PAYMENT -> {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = onMarkPaid,
              modifier = Modifier.weight(1f).height(40.dp),
              colors = ButtonDefaults.buttonColors(containerColor = GreenProfit),
              shape = RoundedCornerShape(10.dp),
            ) {
              Text("I Have Paid (Telebirr/CBE)", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
            OutlinedButton(
              onClick = onDispute,
              modifier = Modifier.height(40.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = RedLoss),
              border = androidx.compose.foundation.BorderStroke(1.dp, RedLoss),
              shape = RoundedCornerShape(10.dp),
            ) {
              Text("Dispute", fontSize = 11.sp)
            }
          }
        }
        EscrowStatus.PAID_PENDING_RELEASE -> {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = onRelease,
              modifier = Modifier.weight(1f).height(40.dp),
              colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
              shape = RoundedCornerShape(10.dp),
            ) {
              Text("Payment Received, Release REAL", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
            OutlinedButton(
              onClick = onDispute,
              modifier = Modifier.height(40.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = RedLoss),
              border = androidx.compose.foundation.BorderStroke(1.dp, RedLoss),
              shape = RoundedCornerShape(10.dp),
            ) {
              Text("Dispute", fontSize = 11.sp)
            }
          }
        }
        EscrowStatus.DISPUTED -> {
          Text(
            text = "⚖️ Under Admin Escrow Review. Admin will resolve within 10 minutes.",
            color = RedLoss,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
          )
        }
        EscrowStatus.COMPLETED -> {
          Text(
            text = "✓ Order Successfully Settled & Released.",
            color = GreenProfit,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
          )
        }
        else -> {}
      }
    }
  }
}

@Composable
fun DisputeDialog(
  orderId: String,
  onDismiss: () -> Unit,
  onSubmitDispute: (String) -> Unit,
) {
  var reason by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = DarkSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, RedLoss),
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text("Raise Escrow Dispute", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "Order #$orderId will be locked and escalated to RealCoin Admin moderation team for manual review.",
          fontSize = 12.sp,
          color = TextSecondary,
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = reason,
          onValueChange = { reason = it },
          label = { Text("Reason for Dispute") },
          placeholder = { Text("e.g. Telebirr SMS received but seller refuses to release", color = TextMuted) },
          modifier = Modifier.fillMaxWidth(),
          colors =
            OutlinedTextFieldDefaults.colors(
              focusedBorderColor = RedLoss,
              unfocusedBorderColor = DarkCardBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
            ),
          shape = RoundedCornerShape(12.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = { onSubmitDispute(reason) },
          modifier = Modifier.fillMaxWidth().height(46.dp),
          colors = ButtonDefaults.buttonColors(containerColor = RedLoss),
          shape = RoundedCornerShape(12.dp),
        ) {
          Text("Submit Dispute to Admin", color = Color.White, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
