package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.RealCoinConstants
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GreenProfit
import com.example.ui.theme.RedLoss
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.abs

@Composable
fun ReceiveDialog(
  walletAddress: String,
  onDismiss: () -> Unit,
) {
  val context = LocalContext.current

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = DarkSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "Receive REAL",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Custom QR Code
        Box(
          modifier =
            Modifier.size(190.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(Color.White)
              .padding(14.dp),
          contentAlignment = Alignment.Center,
        ) {
          Canvas(modifier = Modifier.size(162.dp)) {
            val qrSize = size.width
            val cellSize = qrSize / 21f
            drawRect(Color.White, size = size)

            val hash = abs(walletAddress.hashCode())
            for (row in 0 until 21) {
              for (col in 0 until 21) {
                val isCorner =
                  (row < 7 && col < 7) ||
                    (row < 7 && col >= 14) ||
                    (row >= 14 && col < 7)
                var isBlack = false

                if (isCorner) {
                  val r = if (row >= 14) row - 14 else row
                  val c = if (col >= 14) col - 14 else col
                  if (r == 0 || r == 6 || c == 0 || c == 6 || (r in 2..4 && c in 2..4)) {
                    isBlack = true
                  }
                } else {
                  val bit = ((hash xor (row * 31 + col * 17)) % 7) < 4
                  isBlack = bit
                }

                if (isBlack) {
                  drawRect(
                    color = Color.Black,
                    topLeft = Offset(col * cellSize, row * cellSize),
                    size = Size(cellSize, cellSize),
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("RealChain Network Address", fontSize = 12.sp, color = TextMuted)
        Spacer(modifier = Modifier.height(6.dp))

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = DarkCard,
          border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
          modifier =
            Modifier.clickable {
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              clipboard.setPrimaryClip(ClipData.newPlainText("Address", walletAddress))
              Toast.makeText(context, "Address copied to clipboard!", Toast.LENGTH_SHORT).show()
            },
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = walletAddress.take(14) + "..." + walletAddress.takeLast(10),
              color = GoldPrimary,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = GoldPrimary, modifier = Modifier.size(16.dp))
          }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(
          onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Address", walletAddress))
            Toast.makeText(context, "Address copied!", Toast.LENGTH_SHORT).show()
            onDismiss()
          },
          modifier = Modifier.fillMaxWidth().height(48.dp),
          colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
          shape = RoundedCornerShape(12.dp),
        ) {
          Text("Copy Wallet Address", color = DarkBackground, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun SendDialog(
  availableReal: Double,
  onDismiss: () -> Unit,
  onSendConfirm: (String, Double, String) -> Unit,
) {
  var recipient by remember { mutableStateOf("") }
  var amountText by remember { mutableStateOf("") }
  var memo by remember { mutableStateOf("") }
  var errorText by remember { mutableStateOf<String?>(null) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = DarkSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
    ) {
      Column(modifier = Modifier.padding(24.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "Send REAL",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Available: %.2f REAL (%.2f ETB)".format(availableReal, availableReal * RealCoinConstants.REAL_PRICE_ETB),
          fontSize = 12.sp,
          color = GoldPrimary,
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = recipient,
          onValueChange = { recipient = it },
          label = { Text("Recipient Address or Username") },
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

        OutlinedTextField(
          value = amountText,
          onValueChange = {
            amountText = it
            errorText = null
          },
          label = { Text("Amount (REAL)") },
          trailingIcon = {
            Text(
              text = "MAX",
              color = GoldPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              modifier =
                Modifier.clickable { amountText = availableReal.toString() }.padding(end = 8.dp),
            )
          },
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

        OutlinedTextField(
          value = memo,
          onValueChange = { memo = it },
          label = { Text("Memo / Note (Optional)") },
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

        if (errorText != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = errorText!!, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text("Network Fee:", fontSize = 12.sp, color = TextMuted)
          Text("0.00 REAL (Zero gas promo)", fontSize = 12.sp, color = GreenProfit)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            val amt = amountText.toDoubleOrNull()
            if (recipient.isBlank()) {
              errorText = "Please enter recipient address"
            } else if (amt == null || amt <= 0) {
              errorText = "Please enter a valid amount"
            } else if (amt > availableReal) {
              errorText = "Amount exceeds available balance"
            } else {
              onSendConfirm(recipient, amt, memo)
              onDismiss()
            }
          },
          modifier = Modifier.fillMaxWidth().height(48.dp),
          colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
          shape = RoundedCornerShape(12.dp),
        ) {
          Text("Confirm & Send", color = DarkBackground, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

/**
 * Requirement 4 & 5:
 * Deposit page: ONLY USDT by BEP-20 address: 0x8e54105bed3243e1ca44a0cccc6b62cf2bff9df4
 * Minimum deposit: $25 USD
 * 10% Real Coin bonus balance display
 * Today's market value: 0.0027 USD, in ETB 5 Birr (Rate: 186 ETB per USD)
 * Shows user balance in Real Coin, USD, and ETB!
 */
@Composable
fun DepositDialog(
  currentBalanceReal: Double,
  realPriceUsd: Double = RealCoinConstants.REAL_PRICE_USD,
  realPriceEtb: Double = RealCoinConstants.REAL_PRICE_ETB,
  usdToEtbRate: Double = RealCoinConstants.USD_TO_ETB_RATE,
  onDismiss: () -> Unit,
  onSubmitDepositTx: (Double, String) -> Unit,
  onInstantDemoCredit: (Double) -> Unit,
) {
  val context = LocalContext.current
  var amountUsdText by remember { mutableStateOf("25") }
  var txHashText by remember { mutableStateOf("") }
  var errorText by remember { mutableStateOf<String?>(null) }

  val enteredUsd = amountUsdText.toDoubleOrNull() ?: 0.0
  val baseRealCoins = if (realPriceUsd > 0) enteredUsd / realPriceUsd else 0.0
  val bonusRealCoins = baseRealCoins * (RealCoinConstants.DEPOSIT_BONUS_PERCENTAGE / 100.0)
  val totalRealCoins = baseRealCoins + bonusRealCoins
  val totalEtbValue = totalRealCoins * realPriceEtb

  val currentBalUsd = currentBalanceReal * realPriceUsd
  val currentBalEtb = currentBalanceReal * realPriceEtb

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier.fillMaxWidth().padding(12.dp),
      shape = RoundedCornerShape(24.dp),
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
                text = "Deposit USDT (BEP-20)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
              )
              Text(
                text = "Binance Smart Chain (BSC)",
                fontSize = 11.sp,
                color = GoldPrimary,
              )
            }
            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // User's Current Balance in 3 Currencies
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = DarkCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text("Your Current Balance", fontSize = 11.sp, color = TextMuted)
              Spacer(modifier = Modifier.height(4.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Column {
                  Text("RealCoin", fontSize = 10.sp, color = TextMuted)
                  Text("%.2f REAL".format(currentBalanceReal), color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Column {
                  Text("USD Value", fontSize = 10.sp, color = TextMuted)
                  Text("$%.2f USD".format(currentBalUsd), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Column {
                  Text("ETB Value", fontSize = 10.sp, color = TextMuted)
                  Text("%.2f ETB".format(currentBalEtb), color = GreenProfit, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Market Value & 10% Bonus Header
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = GoldPrimary.copy(alpha = 0.12f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "+10% RealCoin Bonus on Every Deposit!",
                  color = GoldPrimary,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Today's Rate: 1 REAL = $0.0027 USD | 5.00 ETB (1 USD = 186 ETB)",
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Deposit Address Box
          Text("Official USDT (BEP-20) Deposit Address", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
          Spacer(modifier = Modifier.height(6.dp))

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = DarkCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f)),
            modifier =
              Modifier.fillMaxWidth().clickable {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("BEP20 Address", RealCoinConstants.USDT_BEP20_DEPOSIT_ADDRESS))
                Toast.makeText(context, "BEP-20 Address copied!", Toast.LENGTH_SHORT).show()
              },
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = RealCoinConstants.USDT_BEP20_DEPOSIT_ADDRESS,
                  color = GoldPrimary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  lineHeight = 14.sp,
                )
                Text(
                  text = "Only send USDT via BSC / BEP-20 network",
                  color = GreenProfit,
                  fontSize = 10.sp,
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = GoldPrimary, modifier = Modifier.size(20.dp))
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Amount to Deposit
          OutlinedTextField(
            value = amountUsdText,
            onValueChange = {
              amountUsdText = it
              errorText = null
            },
            label = { Text("Deposit Amount (Min $25 USD)") },
            trailingIcon = { Text("USDT", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(end = 8.dp)) },
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

          // Conversion Calculation Card
          if (enteredUsd >= 25.0) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = DarkCard,
              border = androidx.compose.foundation.BorderStroke(1.dp, GreenProfit.copy(alpha = 0.5f)),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("Base Tokens:", fontSize = 11.sp, color = TextMuted)
                  Text("%.2f REAL".format(baseRealCoins), fontSize = 11.sp, color = TextPrimary)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("+ 10% Extra Bonus:", fontSize = 11.sp, color = GreenProfit, fontWeight = FontWeight.Bold)
                  Text("+%.2f REAL".format(bonusRealCoins), fontSize = 11.sp, color = GreenProfit, fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DarkCardBorder).padding(vertical = 4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("Total You Receive:", fontSize = 12.sp, color = GoldPrimary, fontWeight = FontWeight.Bold)
                  Text("%.2f REAL (%.2f ETB)".format(totalRealCoins, totalEtbValue), fontSize = 12.sp, color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Transaction Hash Input
          OutlinedTextField(
            value = txHashText,
            onValueChange = {
              txHashText = it
              errorText = null
            },
            label = { Text("BEP-20 Transaction Hash (TxID)") },
            placeholder = { Text("0x...", color = TextMuted) },
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

          if (errorText != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = errorText!!, color = Color.Red, fontSize = 12.sp)
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Submit for Admin Review Button
          Button(
            onClick = {
              val amt = amountUsdText.toDoubleOrNull()
              if (amt == null || amt < RealCoinConstants.MIN_DEPOSIT_USD) {
                errorText = "Minimum deposit is $25.00 USD"
              } else if (txHashText.isBlank() || txHashText.length < 8) {
                errorText = "Please enter your BSC transaction hash"
              } else {
                onSubmitDepositTx(amt, txHashText)
                onDismiss()
              }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
            shape = RoundedCornerShape(12.dp),
          ) {
            Text("Submit Deposit for Approval", color = DarkBackground, fontWeight = FontWeight.Bold)
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Instant Demo Test Button
          Button(
            onClick = {
              val amt = amountUsdText.toDoubleOrNull()
              if (amt == null || amt < RealCoinConstants.MIN_DEPOSIT_USD) {
                errorText = "Minimum deposit is $25.00 USD"
              } else {
                onInstantDemoCredit(amt)
                onDismiss()
              }
            },
            modifier = Modifier.fillMaxWidth().height(42.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
          ) {
            Text("⚡ Instant Test Credit (+10% Bonus)", color = GreenProfit, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          }
        }
      }
    }
  }
}

@Composable
fun WithdrawDialog(
  availableReal: Double,
  realPriceUsd: Double = RealCoinConstants.REAL_PRICE_USD,
  realPriceEtb: Double = RealCoinConstants.REAL_PRICE_ETB,
  onDismiss: () -> Unit,
  onWithdrawConfirm: (String, Double, String) -> Unit,
) {
  val payoutMethod = "USDT (BEP-20)"
  var bep20Address by remember { mutableStateOf("") }
  var amountText by remember { mutableStateOf("") }
  var errorText by remember { mutableStateOf<String?>(null) }

  val enteredReal = amountText.toDoubleOrNull() ?: 0.0
  val enteredUsdt = enteredReal * realPriceUsd
  val minWithdrawUsd = RealCoinConstants.MIN_WITHDRAW_USD
  val minWithdrawReal = RealCoinConstants.minWithdrawReal(realPriceUsd)
  val minWithdrawUsdt = minWithdrawUsd

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier.fillMaxWidth().padding(14.dp),
      shape = RoundedCornerShape(24.dp),
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
              text = "Withdraw USDT (BEP-20)",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary,
            )
            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Available balance summary
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
                Text("Available Balance", fontSize = 10.sp, color = TextMuted)
                Text("%.0f REAL".format(availableReal), color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              }
              Column {
                Text("In USDT Value", fontSize = 10.sp, color = TextMuted)
                Text("$%.2f USDT".format(availableReal * realPriceUsd), color = GreenProfit, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              }
              Column {
                Text("Network", fontSize = 10.sp, color = TextMuted)
                Text("BEP-20", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Withdrawal Network Notice & Minimum Limit
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = GoldPrimary.copy(alpha = 0.08f),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Withdrawal Rules & Limits", color = GoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text("• Method: Exclusively via USDT on BNB Smart Chain (BEP-20)", fontSize = 11.sp, color = TextSecondary)
              Text("• Minimum Withdrawal: $50.00 USDT (≈ %,.0f REAL at today's rate of $%.4f/REAL)".format(minWithdrawReal, realPriceUsd), fontSize = 11.sp, color = GoldPrimary, fontWeight = FontWeight.SemiBold)
              Text("• Processing: Reviewed & approved by Admin to guarantee network security", fontSize = 11.sp, color = TextMuted)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // BEP-20 Address Input
          OutlinedTextField(
            value = bep20Address,
            onValueChange = {
              bep20Address = it
              errorText = null
            },
            label = { Text("Your USDT (BEP-20) Address") },
            placeholder = { Text("0x...", color = TextMuted) },
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

          // Amount in REAL
          OutlinedTextField(
            value = amountText,
            onValueChange = {
              amountText = it
              errorText = null
            },
            label = { Text("Amount in REAL (Min %,.0f ≈ $50)".format(minWithdrawReal)) },
            placeholder = { Text("%.0f".format(minWithdrawReal), color = TextMuted) },
            trailingIcon = {
              Text(
                text = "MAX",
                color = GoldPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.clickable { amountText = availableReal.toString() }.padding(end = 12.dp),
              )
            },
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

          if (enteredReal > 0) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Estimated Payout: $%.4f USDT (BEP-20)".format(enteredUsdt),
              color = if (enteredReal >= minWithdrawReal) GreenProfit else RedLoss,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
            )
          }

          if (errorText != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = errorText!!, color = Color.Red, fontSize = 12.sp)
          }

          Spacer(modifier = Modifier.height(18.dp))

          Button(
            onClick = {
              val amt = amountText.toDoubleOrNull()
              val addressTrimmed = bep20Address.trim()
              if (addressTrimmed.isBlank()) {
                errorText = "Please enter your USDT (BEP-20) address"
              } else if (!addressTrimmed.startsWith("0x") || addressTrimmed.length < 20) {
                errorText = "Please enter a valid BEP-20 address starting with 0x"
              } else if (amt == null || amt <= 0) {
                errorText = "Please enter a valid withdrawal amount"
              } else if (amt < minWithdrawReal) {
                errorText = "Minimum withdrawal is $50.00 USDT (≈ %,.0f REAL at today's rate)".format(minWithdrawReal)
              } else if (amt > availableReal) {
                errorText = "Amount exceeds your available balance (%.0f REAL)".format(availableReal)
              } else {
                onWithdrawConfirm(payoutMethod, amt, addressTrimmed)
                onDismiss()
              }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
            shape = RoundedCornerShape(12.dp),
          ) {
            Text("Request USDT Withdrawal (Admin Approved)", color = DarkBackground, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun MethodChip(
  title: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier =
      modifier
        .clip(RoundedCornerShape(10.dp))
        .background(if (isSelected) GoldPrimary.copy(alpha = 0.18f) else DarkCard)
        .border(
          width = 1.dp,
          color = if (isSelected) GoldPrimary else DarkCardBorder,
          shape = RoundedCornerShape(10.dp),
        )
        .clickable { onClick() }
        .padding(vertical = 10.dp, horizontal = 6.dp),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = title,
      color = if (isSelected) GoldPrimary else TextSecondary,
      fontSize = 11.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
      textAlign = TextAlign.Center,
      maxLines = 1,
    )
  }
}
