package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KycStatus
import com.example.data.model.SupportTicket
import com.example.data.model.TicketStatus
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GreenProfit
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.RealCoinUiState
import com.example.ui.viewmodel.RealCoinViewModel

@Composable
fun HelpCenterScreen(
  viewModel: RealCoinViewModel,
  uiState: RealCoinUiState,
  onBack: () -> Unit,
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  var category by remember { mutableStateOf("Withdrawal (USDT BEP-20)") }
  var subject by remember { mutableStateOf("") }
  var message by remember { mutableStateOf("") }
  var contactInfo by remember { mutableStateOf(uiState.userProfile.email) }
  var formError by remember { mutableStateOf<String?>(null) }
  var expandedFaqIndex by remember { mutableIntStateOf(-1) }

  val categories = listOf(
    "Withdrawal (USDT BEP-20)",
    "Deposit & 10% Bonus",
    "KYC Verification",
    "P2P Escrow",
    "Lucky Spin Wheel",
    "Account Security",
  )

  val faqs = listOf(
    Pair(
      "What are the withdrawal rules and minimum limits?",
      "Withdrawals are strictly processed via USDT on the BNB Smart Chain (BEP-20) to your designated wallet address (0x...). The minimum withdrawal is strictly $50 USDT, automatically converted to REAL coin at today's live market value (e.g. at $0.0027/REAL ≈ 18,519 REAL). All withdrawals undergo compliance review before network broadcast.",
    ),
    Pair(
      "Why must I wait for KYC approval before using the app?",
      "To prevent fraud, multiple account abuse, and comply with regulatory financial mandates, all new accounts are gated until an administrator approves your submitted ID documents. Once approved, you gain full access to the Main App with no Landing page roadblock.",
    ),
    Pair(
      "How does the 10% Deposit Bonus work?",
      "Every time you deposit USDT via our official BEP-20 smart contract address, you automatically receive a +10% bonus in RealCoin credited directly into your wallet upon admin confirmation.",
    ),
    Pair(
      "What are the odds on the Lucky Spin Wheel?",
      "Users receive 1 Free Spin every 24 hours. Additional spins cost 10 REAL coin. To protect ecosystem treasury liquidity, the win rate for prizes above 10 REAL coin is calibrated at 20%, while prizes of 5 to 10 REAL account for 80%.",
    ),
  )

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
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
          text = "Help Center & Support",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary,
        )
      }
      Spacer(modifier = Modifier.height(16.dp))

      // 24/7 Support Banner
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f)),
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(GoldPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
          ) {
            Icon(Icons.Default.HeadsetMic, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
          }
          Spacer(modifier = Modifier.width(14.dp))
          Column {
            Text("24/7 Official Support", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("Integrated with RealCoin Admin Portal", color = GreenProfit, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text("Telegram: @RealCoinOfficial | Telegram Bot: @RealCoinSupportBot", color = TextMuted, fontSize = 11.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Tab selector
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = DarkSurface,
        contentColor = GoldPrimary,
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
          text = { Text("Contact & Ticket", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Text(
              "My Inquiries (${uiState.supportTickets.size})",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
            )
          },
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = { Text("FAQs", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
        )
      }

      Spacer(modifier = Modifier.height(16.dp))
    }

    // TAB 0: SUBMIT TICKET
    if (selectedTab == 0) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurface),
          border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.SupportAgent, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Submit Support Inquiry", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text("Select Inquiry Category", color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))

            // Categories Chips
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                categories.take(2).forEach { cat ->
                  CategoryChip(cat, selected = category == cat, onClick = { category = cat }, modifier = Modifier.weight(1f))
                }
              }
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                categories.drop(2).take(2).forEach { cat ->
                  CategoryChip(cat, selected = category == cat, onClick = { category = cat }, modifier = Modifier.weight(1f))
                }
              }
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                categories.drop(4).forEach { cat ->
                  CategoryChip(cat, selected = category == cat, onClick = { category = cat }, modifier = Modifier.weight(1f))
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Subject
            OutlinedTextField(
              value = subject,
              onValueChange = {
                subject = it
                formError = null
              },
              label = { Text("Subject / Issue Title") },
              placeholder = { Text("e.g. USDT withdrawal verification delay", color = TextMuted) },
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

            // Message
            OutlinedTextField(
              value = message,
              onValueChange = {
                message = it
                formError = null
              },
              label = { Text("Detailed Description") },
              placeholder = { Text("Please describe your issue with transaction hash or account details...", color = TextMuted) },
              minLines = 3,
              maxLines = 6,
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

            // Contact Info
            OutlinedTextField(
              value = contactInfo,
              onValueChange = { contactInfo = it },
              label = { Text("Your Email or Telegram Username") },
              placeholder = { Text("@yourusername or email@example.com", color = TextMuted) },
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

            if (formError != null) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(text = formError!!, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
              onClick = {
                if (subject.isBlank()) {
                  formError = "Please enter a subject"
                } else if (message.isBlank()) {
                  formError = "Please describe your issue"
                } else {
                  val ok = viewModel.submitSupportTicket(category, subject, message, contactInfo)
                  if (ok) {
                    subject = ""
                    message = ""
                    selectedTab = 1 // Switch to view tickets
                  }
                }
              },
              modifier = Modifier.fillMaxWidth().height(48.dp),
              colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
              shape = RoundedCornerShape(12.dp),
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Send, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send Ticket to Admin Desk", color = DarkBackground, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
        Spacer(modifier = Modifier.height(40.dp))
      }
    }

    // TAB 1: MY INQUIRIES & REPLIES
    if (selectedTab == 1) {
      if (uiState.supportTickets.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
          ) {
            Column(
              modifier = Modifier.padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              Icon(Icons.Default.Chat, contentDescription = null, tint = TextMuted, modifier = Modifier.size(44.dp))
              Spacer(modifier = Modifier.height(12.dp))
              Text("No Tickets Submitted Yet", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
              Text("Use the Contact & Ticket tab to submit an inquiry to the Admin team.", color = TextMuted, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
          }
        }
      } else {
        items(uiState.supportTickets) { ticket ->
          TicketCard(ticket)
          Spacer(modifier = Modifier.height(12.dp))
        }
        item {
          Spacer(modifier = Modifier.height(30.dp))
        }
      }
    }

    // TAB 2: FAQS
    if (selectedTab == 2) {
      items(faqs.indices.toList()) { index ->
        val (q, a) = faqs[index]
        val isExpanded = expandedFaqIndex == index

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { expandedFaqIndex = if (isExpanded) -1 else index },
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurface),
          border = androidx.compose.foundation.BorderStroke(1.dp, if (isExpanded) GoldPrimary.copy(alpha = 0.5f) else DarkCardBorder),
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = q,
                color = if (isExpanded) GoldPrimary else TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f),
              )
              Icon(
                if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = if (isExpanded) GoldPrimary else TextMuted,
              )
            }

            AnimatedVisibility(visible = isExpanded) {
              Column {
                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DarkCardBorder))
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = a, color = TextSecondary, fontSize = 12.sp, lineHeight = 18.sp)
              }
            }
          }
        }
        Spacer(modifier = Modifier.height(10.dp))
      }
      item {
        Spacer(modifier = Modifier.height(40.dp))
      }
    }
  }
}

@Composable
private fun CategoryChip(title: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = if (selected) GoldPrimary.copy(alpha = 0.2f) else DarkCard,
    border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) GoldPrimary else DarkCardBorder),
    modifier = modifier.clickable { onClick() },
  ) {
    Text(
      text = title,
      color = if (selected) GoldPrimary else TextSecondary,
      fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
      fontSize = 11.sp,
      maxLines = 1,
      modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
      textAlign = androidx.compose.ui.text.style.TextAlign.Center,
    )
  }
}

@Composable
private fun TicketCard(ticket: SupportTicket) {
  val (statusColor, statusLabel) = when (ticket.status) {
    TicketStatus.OPEN -> Pair(Color(0xFFF59E0B), "PENDING ADMIN REVIEW")
    TicketStatus.ANSWERED -> Pair(GreenProfit, "ADMIN ANSWERED ✓")
    TicketStatus.RESOLVED -> Pair(Color(0xFF60A5FA), "RESOLVED ✓")
  }

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
        Text(text = ticket.createdAt, color = TextMuted, fontSize = 11.sp)
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(text = ticket.subject, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
      Text(text = "Category: ${ticket.category}", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)

      Spacer(modifier = Modifier.height(6.dp))

      Text(text = ticket.message, color = TextSecondary, fontSize = 12.sp, lineHeight = 17.sp)

      // Admin Reply section
      if (!ticket.adminReply.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = DarkCard,
          border = androidx.compose.foundation.BorderStroke(1.dp, GreenProfit.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SupportAgent, contentDescription = null, tint = GreenProfit, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Admin Desk Reply", color = GreenProfit, fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
              ticket.repliedAt?.let {
                Text(it, color = TextMuted, fontSize = 10.sp)
              }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = ticket.adminReply, color = TextPrimary, fontSize = 12.sp, lineHeight = 18.sp)
          }
        }
      }
    }
  }
}
