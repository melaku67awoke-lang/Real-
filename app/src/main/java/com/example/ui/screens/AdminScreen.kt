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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.ScrollableTabRow
import coil.compose.AsyncImage
import androidx.compose.material3.Tab
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DepositRequest
import com.example.data.model.EscrowOrder
import com.example.data.model.EscrowStatus
import com.example.data.model.KycStatus
import com.example.data.model.KycSubmission
import com.example.data.model.RealCoinConstants
import com.example.data.model.SupportTicket
import com.example.data.model.TicketStatus
import com.example.data.model.TransactionStatus
import com.example.data.model.WithdrawRequest
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GreenProfit
import com.example.ui.theme.RedLoss
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.RealCoinUiState
import com.example.ui.viewmodel.RealCoinViewModel

@Composable
fun AdminLoginScreen(
  viewModel: RealCoinViewModel,
  onBack: () -> Unit,
) {
  var passwordInput by remember { mutableStateOf("") }
  var isPasswordVisible by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Column(
    modifier =
      Modifier.fillMaxSize()
        .background(DarkBackground)
        .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Spacer(modifier = Modifier.height(20.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      IconButton(onClick = onBack) {
        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
      }
      Spacer(modifier = Modifier.width(8.dp))
      Text("Admin Authentication", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }

    Spacer(modifier = Modifier.height(40.dp))

    Box(
      modifier = Modifier.size(64.dp).clip(CircleShape).background(DarkSurface).border(1.dp, GoldPrimary, CircleShape),
      contentAlignment = Alignment.Center,
    ) {
      Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(36.dp))
    }

    Spacer(modifier = Modifier.height(16.dp))
    Text("RealCoin Admin Portal", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    Text(
      text = "Manage KYC verifications, P2P disputes, deposits, and withdrawals",
      fontSize = 12.sp,
      color = TextSecondary,
      textAlign = androidx.compose.ui.text.style.TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(30.dp))

    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = DarkSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Text("Authorized Personnel Only", fontSize = 13.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
          value = passwordInput,
          onValueChange = { passwordInput = it; errorMessage = null },
          label = { Text("Enter Admin Password") },
          leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GoldPrimary) },
          trailingIcon = {
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
              Icon(
                if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = null,
                tint = TextMuted,
              )
            }
          },
          visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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

        if (errorMessage != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = errorMessage!!, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            val ok = viewModel.adminLogin(passwordInput)
            if (!ok) {
              errorMessage = "Incorrect admin password. Access restricted."
            }
          },
          modifier = Modifier.fillMaxWidth().height(48.dp),
          colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
          shape = RoundedCornerShape(12.dp),
        ) {
          Text("Sign In to Admin Portal", color = DarkBackground, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

/**
 * Requirement 7:
 * Admin Panel to accept and reject:
 * - KYC submissions
 * - P2P disputes
 * - Withdrawals
 * - Deposits
 */
@Composable
fun AdminPanelScreen(
  viewModel: RealCoinViewModel,
  uiState: RealCoinUiState,
  onExit: () -> Unit,
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Deposits, 1: Withdrawals, 2: KYC, 3: P2P Disputes

  val pendingDeposits = uiState.pendingDeposits
  val pendingWithdrawals = uiState.pendingWithdrawals
  val kycList = uiState.kycSubmissions
  val disputedOrders = uiState.activeEscrowOrders.filter { it.status == EscrowStatus.DISPUTED }

  LazyColumn(
    modifier =
      Modifier.fillMaxSize()
        .background(DarkBackground)
        .padding(horizontal = 16.dp),
  ) {
    // Top Bar
    item {
      Spacer(modifier = Modifier.height(16.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onExit) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Exit", tint = TextPrimary)
          }
          Spacer(modifier = Modifier.width(6.dp))
          Column {
            Text("Admin Dashboard", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("RealCoin Network Supervisor", fontSize = 11.sp, color = GoldPrimary)
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = RedLoss.copy(alpha = 0.2f),
          modifier = Modifier.clickable { viewModel.adminLogout() },
        ) {
          Text(
            text = "Logout Admin",
            color = RedLoss,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          )
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // 5 Management Tabs (including RealCoin Price setting)
    item {
      ScrollableTabRow(
        selectedTabIndex = selectedTab,
        containerColor = DarkSurface,
        contentColor = GoldPrimary,
        edgePadding = 4.dp,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = GoldPrimary,
          )
        },
        modifier = Modifier.clip(RoundedCornerShape(12.dp)),
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("Deposits (${pendingDeposits.count { it.status == TransactionStatus.PENDING }})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("Withdraw (${pendingWithdrawals.count { it.status == TransactionStatus.PENDING }})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = { Text("KYC (${kycList.count { it.status == KycStatus.PENDING_REVIEW }})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
        )
        Tab(
          selected = selectedTab == 3,
          onClick = { selectedTab = 3 },
          text = { Text("Disputes (${disputedOrders.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
        )
        Tab(
          selected = selectedTab == 4,
          onClick = { selectedTab = 4 },
          text = { Text("REAL Price", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
        )
        Tab(
          selected = selectedTab == 5,
          onClick = { selectedTab = 5 },
          text = { Text("Help Tickets (${uiState.supportTickets.count { it.status == TicketStatus.OPEN }})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
        )
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // TAB 0: DEPOSITS APPROVAL
    if (selectedTab == 0) {
      if (pendingDeposits.isEmpty()) {
        item {
          EmptyAdminCard("No deposit requests submitted yet.")
        }
      } else {
        items(pendingDeposits) { deposit ->
          AdminDepositCard(
            deposit = deposit,
            onApprove = { viewModel.approveDeposit(deposit.id) },
            onReject = { viewModel.rejectDeposit(deposit.id) },
          )
          Spacer(modifier = Modifier.height(12.dp))
        }
      }
    }

    // TAB 1: WITHDRAWALS APPROVAL
    if (selectedTab == 1) {
      if (pendingWithdrawals.isEmpty()) {
        item {
          EmptyAdminCard("No withdrawal requests pending.")
        }
      } else {
        items(pendingWithdrawals) { req ->
          AdminWithdrawCard(
            req = req,
            onApprove = { viewModel.approveWithdrawal(req.id) },
            onReject = { viewModel.rejectWithdrawal(req.id) },
          )
          Spacer(modifier = Modifier.height(12.dp))
        }
      }
    }

    // TAB 2: KYC APPROVAL
    if (selectedTab == 2) {
      if (kycList.isEmpty()) {
        item {
          EmptyAdminCard("No pending KYC verification submissions.")
        }
      } else {
        items(kycList) { kyc ->
          AdminKycCard(
            kyc = kyc,
            onApprove = { viewModel.approveKyc(kyc.id) },
            onReject = { viewModel.rejectKyc(kyc.id) },
          )
          Spacer(modifier = Modifier.height(12.dp))
        }
      }
    }

    // TAB 3: P2P DISPUTES
    if (selectedTab == 3) {
      if (disputedOrders.isEmpty()) {
        item {
          EmptyAdminCard("No active P2P trade disputes. All escrow transactions are running smoothly!")
        }
      } else {
        items(disputedOrders) { order ->
          AdminDisputeCard(
            order = order,
            onReleaseToBuyer = { viewModel.resolveP2PDispute(order.orderId, releaseToBuyer = true) },
            onRefundToSeller = { viewModel.resolveP2PDispute(order.orderId, releaseToBuyer = false) },
          )
          Spacer(modifier = Modifier.height(12.dp))
        }
      }
    }

    // TAB 4: REAL COIN PRICE SETTINGS (Admin Feature)
    if (selectedTab == 4) {
      item {
        AdminPriceSettingsCard(
          uiState = uiState,
          onUpdatePrices = { usd, etb, rate ->
            viewModel.updateRealCoinPrices(usd, etb, rate)
          },
        )
      }
    }

    // TAB 5: SUPPORT & HELP CENTER TICKETS
    if (selectedTab == 5) {
      if (uiState.supportTickets.isEmpty()) {
        item {
          EmptyAdminCard("No user support tickets submitted yet.")
        }
      } else {
        items(uiState.supportTickets) { ticket ->
          AdminTicketCard(
            ticket = ticket,
            onReply = { replyText, resolve ->
              viewModel.adminReplySupportTicket(ticket.id, replyText, resolve)
            },
          )
          Spacer(modifier = Modifier.height(12.dp))
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

@Composable
fun AdminDepositCard(
  deposit: DepositRequest,
  onApprove: () -> Unit,
  onReject: () -> Unit,
) {
  val isPending = deposit.status == TransactionStatus.PENDING

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, if (isPending) GoldPrimary.copy(alpha = 0.5f) else DarkCardBorder),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column {
          Text("Deposit #${deposit.id}", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
          Text("User: ${deposit.username} • Network: ${deposit.network}", color = TextMuted, fontSize = 11.sp)
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (deposit.status == TransactionStatus.COMPLETED) GreenProfit.copy(alpha = 0.2f) else if (deposit.status == TransactionStatus.REJECTED) RedLoss.copy(alpha = 0.2f) else GoldPrimary.copy(alpha = 0.2f),
        ) {
          Text(
            text = deposit.status.name,
            color = if (deposit.status == TransactionStatus.COMPLETED) GreenProfit else if (deposit.status == TransactionStatus.REJECTED) RedLoss else GoldPrimary,
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
          Text("USDT Amount", fontSize = 10.sp, color = TextMuted)
          Text("$%.2f USD".format(deposit.amountUsd), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        Column(horizontalAlignment = Alignment.End) {
          Text("REAL to Credit (+10% Bonus)", fontSize = 10.sp, color = TextMuted)
          Text("%.2f REAL".format(deposit.totalRealToCredit), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GreenProfit)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text("TxHash: ${deposit.txHash}", fontSize = 10.sp, color = TextSecondary)
      Text("Target Address: ${deposit.depositAddress}", fontSize = 10.sp, color = TextMuted)

      if (isPending) {
        Spacer(modifier = Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(
            onClick = onApprove,
            modifier = Modifier.weight(1f).height(38.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenProfit),
            shape = RoundedCornerShape(8.dp),
          ) {
            Text("Approve (+10% Bonus)", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
          Button(
            onClick = onReject,
            modifier = Modifier.weight(1f).height(38.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RedLoss),
            shape = RoundedCornerShape(8.dp),
          ) {
            Text("Reject Deposit", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }
      }
    }
  }
}

@Composable
fun AdminWithdrawCard(
  req: WithdrawRequest,
  onApprove: () -> Unit,
  onReject: () -> Unit,
) {
  val isPending = req.status == TransactionStatus.PENDING

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, if (isPending) GoldPrimary.copy(alpha = 0.5f) else DarkCardBorder),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column {
          Text("Withdrawal #${req.id}", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
          Text("User: ${req.username} • ${req.timestamp}", color = TextMuted, fontSize = 11.sp)
        }
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (req.status == TransactionStatus.COMPLETED) GreenProfit.copy(alpha = 0.2f) else if (req.status == TransactionStatus.REJECTED) RedLoss.copy(alpha = 0.2f) else GoldPrimary.copy(alpha = 0.2f),
        ) {
          Text(
            text = req.status.name,
            color = if (req.status == TransactionStatus.COMPLETED) GreenProfit else if (req.status == TransactionStatus.REJECTED) RedLoss else GoldPrimary,
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
          Text("Amount in REAL", fontSize = 10.sp, color = TextMuted)
          Text("%.0f REAL".format(req.amountReal), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
        }
        Column(horizontalAlignment = Alignment.End) {
          Text("Payout in ETB (Birr)", fontSize = 10.sp, color = TextMuted)
          Text("%.2f ETB ($%.2f USD)".format(req.amountEtb, req.amountUsd), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GreenProfit)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text("Method: ${req.method}", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
      Text("Recipient: ${req.accountDetails}", fontSize = 11.sp, color = TextSecondary)

      if (isPending) {
        Spacer(modifier = Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(
            onClick = onApprove,
            modifier = Modifier.weight(1f).height(38.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenProfit),
            shape = RoundedCornerShape(8.dp),
          ) {
            Text("Approve & Mark Paid", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
          Button(
            onClick = onReject,
            modifier = Modifier.weight(1f).height(38.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RedLoss),
            shape = RoundedCornerShape(8.dp),
          ) {
            Text("Reject & Refund REAL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }
      }
    }
  }
}

@Composable
fun AdminKycCard(
  kyc: KycSubmission,
  onApprove: () -> Unit,
  onReject: () -> Unit,
) {
  val isPending = kyc.status == KycStatus.PENDING_REVIEW

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, if (isPending) GoldPrimary.copy(alpha = 0.5f) else DarkCardBorder),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column {
          Text(kyc.fullName, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
          Text("Submission #${kyc.id} • ${kyc.nationality}", color = TextMuted, fontSize = 11.sp)
        }
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (kyc.status == KycStatus.APPROVED) GreenProfit.copy(alpha = 0.2f) else if (kyc.status == KycStatus.REJECTED) RedLoss.copy(alpha = 0.2f) else GoldPrimary.copy(alpha = 0.2f),
        ) {
          Text(
            text = kyc.status.name,
            color = if (kyc.status == KycStatus.APPROVED) GreenProfit else if (kyc.status == KycStatus.REJECTED) RedLoss else GoldPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      Text("Document: ${kyc.docType} (${kyc.docNumber})", fontSize = 12.sp, color = GoldPrimary, fontWeight = FontWeight.SemiBold)
      Text("DOB: ${kyc.dateOfBirth} • Address: ${kyc.residentialAddress}", fontSize = 11.sp, color = TextSecondary)
      Text("Attachments: Front ID (✓), Back ID (✓), Live Selfie (✓)", fontSize = 11.sp, color = GreenProfit)

      if (kyc.frontPhotoUri != null || kyc.selfiePhotoUri != null) {
        Spacer(modifier = Modifier.height(10.dp))
        Text("Uploaded ID Verification Documents:", fontSize = 10.sp, color = TextMuted)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          if (kyc.frontPhotoUri != null) {
            Box(
              modifier = Modifier
                .weight(1f)
                .height(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(DarkCard)
                .border(1.dp, GreenProfit.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
              contentAlignment = Alignment.Center,
            ) {
              AsyncImage(
                model = kyc.frontPhotoUri,
                contentDescription = "Front ID Document",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
              )
            }
          }
          if (kyc.backPhotoUri != null) {
            Box(
              modifier = Modifier
                .weight(1f)
                .height(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(DarkCard)
                .border(1.dp, GreenProfit.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
              contentAlignment = Alignment.Center,
            ) {
              AsyncImage(
                model = kyc.backPhotoUri,
                contentDescription = "Back ID Document",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
              )
            }
          }
          if (kyc.selfiePhotoUri != null) {
            Box(
              modifier = Modifier
                .weight(1f)
                .height(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(DarkCard)
                .border(1.dp, GreenProfit.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
              contentAlignment = Alignment.Center,
            ) {
              AsyncImage(
                model = kyc.selfiePhotoUri,
                contentDescription = "Live Selfie Holding ID",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
              )
            }
          }
        }
      }

      if (isPending) {
        Spacer(modifier = Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(
            onClick = onApprove,
            modifier = Modifier.weight(1f).height(38.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenProfit),
            shape = RoundedCornerShape(8.dp),
          ) {
            Text("Approve Tier 3 (Pro)", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
          Button(
            onClick = onReject,
            modifier = Modifier.weight(1f).height(38.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RedLoss),
            shape = RoundedCornerShape(8.dp),
          ) {
            Text("Reject Submission", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }
      }
    }
  }
}

@Composable
fun AdminDisputeCard(
  order: EscrowOrder,
  onReleaseToBuyer: () -> Unit,
  onRefundToSeller: () -> Unit,
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, RedLoss),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text("Dispute Order #${order.orderId}", fontWeight = FontWeight.Bold, color = RedLoss, fontSize = 14.sp)
        Surface(shape = RoundedCornerShape(6.dp), color = RedLoss.copy(alpha = 0.2f)) {
          Text("DISPUTE OPEN", color = RedLoss, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp))
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text("Trader / Counterparty: ${order.traderName}", color = TextPrimary, fontSize = 12.sp)
      Text("Locked Crypto: %.2f REAL (%.2f ETB)".format(order.cryptoAmount, order.fiatAmount), color = GoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      Text("Dispute Reason: ${order.disputeReason ?: "Buyer states payment made via Telebirr; seller hasn't released."}", color = TextSecondary, fontSize = 11.sp)

      Spacer(modifier = Modifier.height(14.dp))
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
          onClick = onReleaseToBuyer,
          modifier = Modifier.weight(1f).height(38.dp),
          colors = ButtonDefaults.buttonColors(containerColor = GreenProfit),
          shape = RoundedCornerShape(8.dp),
        ) {
          Text("Release to Buyer", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
        Button(
          onClick = onRefundToSeller,
          modifier = Modifier.weight(1f).height(38.dp),
          colors = ButtonDefaults.buttonColors(containerColor = RedLoss),
          shape = RoundedCornerShape(8.dp),
        ) {
          Text("Refund to Seller", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
      }
    }
  }
}

@Composable
fun EmptyAdminCard(message: String) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
  ) {
    Column(
      modifier = Modifier.padding(24.dp).fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenProfit, modifier = Modifier.size(32.dp))
      Spacer(modifier = Modifier.height(8.dp))
      Text(text = message, color = TextSecondary, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
  }
}

/**
 * Requirement: Admin feature to add and update price of RealCoin in USD and ETB,
 * and the USD to ETB exchange rate.
 */
@Composable
fun AdminPriceSettingsCard(
  uiState: RealCoinUiState,
  onUpdatePrices: (Double, Double, Double) -> Unit,
) {
  var priceUsdText by remember(uiState.realPriceUsd) { mutableStateOf(uiState.realPriceUsd.toString()) }
  var priceEtbText by remember(uiState.realPriceEtb) { mutableStateOf(uiState.realPriceEtb.toString()) }
  var usdToEtbText by remember(uiState.usdToEtbRate) { mutableStateOf(uiState.usdToEtbRate.toString()) }
  var savedSuccess by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "RealCoin Live Price Management",
          fontWeight = FontWeight.Bold,
          color = TextPrimary,
          fontSize = 16.sp,
        )
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "As Admin, set or update the RealCoin token price in USD and ETB (Birr), and the USD/ETB exchange rate across the app.",
        fontSize = 12.sp,
        color = TextSecondary,
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Current values summary
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkCard,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Column {
            Text("Active USD Price", fontSize = 10.sp, color = TextMuted)
            Text("$%.4f USD".format(uiState.realPriceUsd), color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
          Column {
            Text("Active ETB Price", fontSize = 10.sp, color = TextMuted)
            Text("%.2f Birr".format(uiState.realPriceEtb), color = GreenProfit, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
          Column {
            Text("USD/ETB Rate", fontSize = 10.sp, color = TextMuted)
            Text("%.2f ETB".format(uiState.usdToEtbRate), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 1. RealCoin USD Price Input
      Text("RealCoin Price in USD ($)", fontSize = 12.sp, color = TextSecondary)
      Spacer(modifier = Modifier.height(6.dp))
      OutlinedTextField(
        value = priceUsdText,
        onValueChange = { 
          priceUsdText = it 
          savedSuccess = false
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = GoldPrimary,
          unfocusedBorderColor = DarkCardBorder,
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary,
        ),
      )

      Spacer(modifier = Modifier.height(12.dp))

      // 2. RealCoin ETB Price Input
      Text("RealCoin Price in ETB (Birr)", fontSize = 12.sp, color = TextSecondary)
      Spacer(modifier = Modifier.height(6.dp))
      OutlinedTextField(
        value = priceEtbText,
        onValueChange = { 
          priceEtbText = it 
          savedSuccess = false
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = GoldPrimary,
          unfocusedBorderColor = DarkCardBorder,
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary,
        ),
      )

      Spacer(modifier = Modifier.height(12.dp))

      // 3. USD to ETB Rate Input
      Text("USD to ETB Exchange Rate", fontSize = 12.sp, color = TextSecondary)
      Spacer(modifier = Modifier.height(6.dp))
      OutlinedTextField(
        value = usdToEtbText,
        onValueChange = { 
          usdToEtbText = it 
          savedSuccess = false
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = GoldPrimary,
          unfocusedBorderColor = DarkCardBorder,
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary,
        ),
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Quick Multiplier Preset Chips
      Text("Quick Multiplier Presets", fontSize = 11.sp, color = TextMuted)
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        listOf(
          "+5%" to 1.05,
          "+10%" to 1.10,
          "+20%" to 1.20,
          "Reset Defaults" to 0.0,
        ).forEach { (label, mult) ->
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = DarkCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.clickable {
              if (mult == 0.0) {
                priceUsdText = "0.0027"
                priceEtbText = "5.0"
                usdToEtbText = "186.0"
              } else {
                val curUsd = priceUsdText.toDoubleOrNull() ?: 0.0027
                val curEtb = priceEtbText.toDoubleOrNull() ?: 5.0
                priceUsdText = "%.4f".format(curUsd * mult)
                priceEtbText = "%.2f".format(curEtb * mult)
              }
              savedSuccess = false
            },
          ) {
            Text(
              text = label,
              fontSize = 10.sp,
              color = GoldPrimary,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Button(
        onClick = {
          val usd = priceUsdText.toDoubleOrNull() ?: uiState.realPriceUsd
          val etb = priceEtbText.toDoubleOrNull() ?: uiState.realPriceEtb
          val rate = usdToEtbText.toDoubleOrNull() ?: uiState.usdToEtbRate
          onUpdatePrices(usd, etb, rate)
          savedSuccess = true
        },
        modifier = Modifier.fillMaxWidth().height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
        shape = RoundedCornerShape(12.dp),
      ) {
        Icon(Icons.Default.Check, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Save & Apply New RealCoin Price", color = DarkBackground, fontWeight = FontWeight.Bold)
      }

      if (savedSuccess) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "✓ RealCoin prices successfully updated across the platform!",
          color = GreenProfit,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
        )
      }
    }
  }
}

@Composable
fun AdminTicketCard(
  ticket: SupportTicket,
  onReply: (String, Boolean) -> Unit,
) {
  var replyInput by remember { mutableStateOf("") }
  var isReplying by remember { mutableStateOf(false) }

  val (statusColor, statusLabel) = when (ticket.status) {
    TicketStatus.OPEN -> Pair(Color(0xFFF59E0B), "OPEN / UNANSWERED")
    TicketStatus.ANSWERED -> Pair(GreenProfit, "ANSWERED")
    TicketStatus.RESOLVED -> Pair(Color(0xFF60A5FA), "RESOLVED")
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, if (ticket.status == TicketStatus.OPEN) GoldPrimary.copy(alpha = 0.5f) else DarkCardBorder),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column {
          Text(text = "Ticket #${ticket.id}", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
          Text(text = "From: ${ticket.username} (${ticket.email}) • ${ticket.createdAt}", color = TextMuted, fontSize = 11.sp)
        }
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = statusColor.copy(alpha = 0.15f),
          border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.4f)),
        ) {
          Text(
            text = statusLabel,
            color = statusColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(text = "Category: ${ticket.category}", color = GoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
      Text(text = "Subject: ${ticket.subject}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)

      Spacer(modifier = Modifier.height(6.dp))
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = DarkCard,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(
          text = ticket.message,
          color = TextSecondary,
          fontSize = 12.sp,
          lineHeight = 17.sp,
          modifier = Modifier.padding(10.dp),
        )
      }

      // Previous reply
      if (!ticket.adminReply.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = GreenProfit.copy(alpha = 0.08f),
          border = androidx.compose.foundation.BorderStroke(1.dp, GreenProfit.copy(alpha = 0.3f)),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text("Admin Reply (${ticket.repliedAt ?: "Sent"}):", color = GreenProfit, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(ticket.adminReply, color = TextPrimary, fontSize = 12.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      if (!isReplying) {
        Button(
          onClick = { isReplying = true },
          modifier = Modifier.fillMaxWidth().height(38.dp),
          colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
          shape = RoundedCornerShape(8.dp),
        ) {
          Text(
            text = if (ticket.adminReply.isNullOrBlank()) "Reply to User Inquiry" else "Send Additional Follow-up Reply",
            color = DarkBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
          )
        }
      } else {
        Column {
          OutlinedTextField(
            value = replyInput,
            onValueChange = { replyInput = it },
            label = { Text("Type Admin Response") },
            placeholder = { Text("Write response to user...", color = TextMuted) },
            minLines = 2,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = GoldPrimary,
              unfocusedBorderColor = DarkCardBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
            ),
            shape = RoundedCornerShape(10.dp),
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = {
                if (replyInput.isNotBlank()) {
                  onReply(replyInput, false)
                  replyInput = ""
                  isReplying = false
                }
              },
              modifier = Modifier.weight(1f).height(38.dp),
              colors = ButtonDefaults.buttonColors(containerColor = GreenProfit),
              shape = RoundedCornerShape(8.dp),
            ) {
              Text("Send Reply", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            Button(
              onClick = {
                if (replyInput.isNotBlank()) {
                  onReply(replyInput, true)
                  replyInput = ""
                  isReplying = false
                }
              },
              modifier = Modifier.weight(1f).height(38.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF60A5FA)),
              shape = RoundedCornerShape(8.dp),
            ) {
              Text("Reply & Resolve", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            OutlinedButton(
              onClick = { isReplying = false },
              modifier = Modifier.height(38.dp),
              shape = RoundedCornerShape(8.dp),
            ) {
              Text("Cancel", fontSize = 11.sp)
            }
          }
        }
      }
    }
  }
}

