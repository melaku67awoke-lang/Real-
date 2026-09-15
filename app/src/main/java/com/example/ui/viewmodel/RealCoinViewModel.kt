package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CryptoRate
import com.example.data.model.DepositRequest
import com.example.data.model.EscrowOrder
import com.example.data.model.EscrowStatus
import com.example.data.model.KycStatus
import com.example.data.model.KycSubmission
import com.example.data.model.KycTier
import com.example.data.model.P2PAd
import com.example.data.model.P2PTradeType
import com.example.data.model.RealCoinConstants
import com.example.data.model.SupportTicket
import com.example.data.model.TicketStatus
import com.example.data.model.TransactionItem
import com.example.data.model.TransactionStatus
import com.example.data.model.TransactionType
import com.example.data.model.UserProfile
import com.example.data.model.WithdrawRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppDestination {
  LANDING,
  LOGIN,
  REGISTER,
  MAIN,
  KYC_SUBMISSION,
  KYC_WAITING,
  HELP_CENTER,
  ADMIN_LOGIN,
  ADMIN_PANEL,
}

data class RealCoinUiState(
  val currentScreen: AppDestination = AppDestination.LANDING,
  val realBalance: Double = 0.0, // All new users start with 0 REAL balance
  val realPriceUsd: Double = RealCoinConstants.REAL_PRICE_USD, // $0.0027
  val realPriceEtb: Double = RealCoinConstants.REAL_PRICE_ETB, // 5.00 Birr
  val usdToEtbRate: Double = RealCoinConstants.USD_TO_ETB_RATE, // 186.0 ETB/USD
  val isBalanceVisible: Boolean = true,
  val userProfile: UserProfile = UserProfile(isLoggedIn = false),
  val cryptoRates: List<CryptoRate> = emptyList(),
  val transactions: List<TransactionItem> = emptyList(),
  val p2pAds: List<P2PAd> = emptyList(),
  val userAds: List<P2PAd> = emptyList(),
  val activeEscrowOrders: List<EscrowOrder> = emptyList(),
  val pendingDeposits: List<DepositRequest> = emptyList(),
  val pendingWithdrawals: List<WithdrawRequest> = emptyList(),
  val kycSubmissions: List<KycSubmission> = emptyList(),
  val supportTickets: List<SupportTicket> = emptyList(),
  val isSpinning: Boolean = false,
  val lastSpinWonAmount: Double? = null,
  val notificationMessage: String? = null,
  val isAdminLoggedIn: Boolean = false,
) {
  val balanceUsd: Double
    get() = realBalance * realPriceUsd

  val balanceEtb: Double
    get() = realBalance * realPriceEtb
}

class RealCoinViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(RealCoinUiState())
  val uiState: StateFlow<RealCoinUiState> = _uiState.asStateFlow()

  init {
    loadInitialData()
    startPriceTickerSimulation()
  }

  fun navigateTo(destination: AppDestination) {
    // KYC Gate: User cannot access MAIN unless approved
    if (destination == AppDestination.MAIN) {
      val kycStatus = _uiState.value.userProfile.kycStatus
      if (kycStatus == KycStatus.PENDING_REVIEW) {
        _uiState.update { it.copy(currentScreen = AppDestination.KYC_WAITING, notificationMessage = "Your verification is under review. Please wait for Admin approval.") }
        return
      } else if (kycStatus == KycStatus.NOT_SUBMITTED) {
        _uiState.update { it.copy(currentScreen = AppDestination.KYC_SUBMISSION, notificationMessage = "Please submit KYC verification to access Main App.") }
        return
      } else if (kycStatus == KycStatus.REJECTED) {
        _uiState.update { it.copy(currentScreen = AppDestination.LANDING, notificationMessage = "KYC rejected. Please resubmit valid ID.") }
        return
      }
    }
    _uiState.update { it.copy(currentScreen = destination) }
  }

  private fun loadInitialData() {
    val initialRates =
      listOf(
        CryptoRate("REAL", "RealCoin", RealCoinConstants.REAL_PRICE_USD, +5.8),
      )

    val initialTxs =
      listOf(
        TransactionItem(
          id = "tx-101",
          type = TransactionType.DEPOSIT,
          title = "USDT BEP-20 Deposit (+10% Bonus)",
          amountReal = 10185.19,
          amountUsd = 27.50,
          amountEtb = 50925.95,
          counterparty = "0x8e54...9df4",
          timestamp = "Today, 10:14 AM",
          status = TransactionStatus.COMPLETED,
          txHash = "0x9812a...77be",
        ),
        TransactionItem(
          id = "tx-102",
          type = TransactionType.P2P_BUY,
          title = "P2P Buy REAL (Telebirr)",
          amountReal = 5000.00,
          amountUsd = 13.50,
          amountEtb = 25000.00,
          counterparty = "AddisCrypto_Pro",
          timestamp = "Yesterday",
          status = TransactionStatus.COMPLETED,
          txHash = "P2P-82194",
        ),
        TransactionItem(
          id = "tx-103",
          type = TransactionType.REWARD,
          title = "Lucky Wheel Daily Spin",
          amountReal = 100.00,
          amountUsd = 0.27,
          amountEtb = 500.00,
          counterparty = "RealCoin Rewards",
          timestamp = "2 days ago",
          status = TransactionStatus.COMPLETED,
          txHash = "SPIN-5501",
        ),
      )

    val initialMarketAds =
      listOf(
        P2PAd(
          id = "ad-1",
          traderName = "AddisExpress",
          tradeType = P2PTradeType.BUY,
          cryptoSymbol = "REAL",
          fiatCurrency = "ETB",
          pricePerUnit = 5.00, // Today's market value: 5 birr per REAL
          availableCrypto = 12000.00,
          minLimit = 500.00,
          maxLimit = 60000.00,
          completionRate = 99.8,
          completedOrders = 1420,
          paymentMethods = listOf("Telebirr", "CBE", "Awash Bank"),
        ),
        P2PAd(
          id = "ad-2",
          traderName = "NileTrader_VIP",
          tradeType = P2PTradeType.BUY,
          cryptoSymbol = "REAL",
          fiatCurrency = "ETB",
          pricePerUnit = 5.02,
          availableCrypto = 8500.00,
          minLimit = 1000.00,
          maxLimit = 42500.00,
          completionRate = 99.1,
          completedOrders = 890,
          paymentMethods = listOf("Telebirr", "Bank of Abyssinia", "CBE Birr"),
        ),
        P2PAd(
          id = "ad-3",
          traderName = "HabeshaExchange",
          tradeType = P2PTradeType.SELL,
          cryptoSymbol = "REAL",
          fiatCurrency = "ETB",
          pricePerUnit = 4.98,
          availableCrypto = 15000.00,
          minLimit = 500.00,
          maxLimit = 75000.00,
          completionRate = 99.5,
          completedOrders = 1150,
          paymentMethods = listOf("CBE", "Dashen Bank", "Telebirr"),
        ),
        P2PAd(
          id = "ad-4",
          traderName = "ShegerCrypto",
          tradeType = P2PTradeType.SELL,
          cryptoSymbol = "REAL",
          fiatCurrency = "ETB",
          pricePerUnit = 4.95,
          availableCrypto = 6200.00,
          minLimit = 250.00,
          maxLimit = 31000.00,
          completionRate = 98.4,
          completedOrders = 430,
          paymentMethods = listOf("Awash Bank", "Bank of Abyssinia"),
        ),
      )

    val sampleKyc =
      KycSubmission(
        id = "KYC-" + (1000..9999).random(),
        fullName = "Dawit Abebe Tadesse",
        email = "dawit.abebe@ethionet.et",
        dateOfBirth = "1994-05-18",
        nationality = "Ethiopian",
        residentialAddress = "Bole Subcity, Woreda 03, Addis Ababa",
        docType = "National ID (Fayda)",
        docNumber = "ET-ID-9824192",
        status = KycStatus.PENDING_REVIEW,
        submittedAt = "Today, 09:30 AM",
      )

    val sampleDeposit =
      DepositRequest(
        id = "DEP-5501",
        username = "dawit_crypto",
        amountUsd = 50.0,
        realCoinAmount = (50.0 / RealCoinConstants.REAL_PRICE_USD),
        bonusRealAmount = (50.0 / RealCoinConstants.REAL_PRICE_USD) * 0.10,
        totalRealToCredit = (50.0 / RealCoinConstants.REAL_PRICE_USD) * 1.10,
        txHash = "0x8fa12c9842a17684df612804bba972e01bbf",
        status = TransactionStatus.PENDING,
        timestamp = "Today, 11:20 AM",
      )

    val sampleWithdrawal =
      WithdrawRequest(
        id = "WTH-9021",
        username = "dawit_crypto",
        amountReal = 2000.0,
        amountUsd = 2000.0 * RealCoinConstants.REAL_PRICE_USD,
        amountEtb = 2000.0 * RealCoinConstants.REAL_PRICE_ETB,
        method = "Telebirr",
        accountDetails = "+251911445566 (Dawit A.)",
        status = TransactionStatus.PENDING,
        timestamp = "Today, 10:45 AM",
      )

    _uiState.update {
      it.copy(
        cryptoRates = initialRates,
        transactions = emptyList(), // Clean initial state for new users
        p2pAds = initialMarketAds,
        kycSubmissions = listOf(sampleKyc),
        pendingDeposits = listOf(sampleDeposit),
        pendingWithdrawals = listOf(sampleWithdrawal),
        supportTickets =
          listOf(
            SupportTicket(
              id = "TCK-1082",
              username = "RealTrader_88",
              email = "trader@realcoin.network",
              category = "Withdrawal (USDT BEP-20)",
              subject = "Inquiry regarding minimum withdrawal limit",
              message = "Hi Admin, I want to confirm that minimum withdrawal is $50 USDT calculated at today's market rate in REAL and how long it takes to process?",
              adminReply = "Hello! Yes, minimum withdrawal is strictly $50 USDT, automatically converted to REAL coin at today's live market rate ($0.0027/REAL ≈ 18,519 REAL). BEP-20 payouts are processed within 15-30 minutes after admin security approval.",
              status = TicketStatus.ANSWERED,
              createdAt = "Today, 09:30 AM",
              repliedAt = "Today, 09:45 AM",
            ),
            SupportTicket(
              id = "TCK-1083",
              username = "Abebe_Crypto",
              email = "abebe@gmail.com",
              category = "KYC Verification",
              subject = "Fayda National ID verification review time",
              message = "Submitted my Fayda ID with clear front photo and selfie. Looking forward to approval.",
              adminReply = "Welcome Abebe! Your ID was reviewed and verified successfully. Full Pro access granted.",
              status = TicketStatus.RESOLVED,
              createdAt = "Yesterday, 04:15 PM",
              repliedAt = "Yesterday, 04:35 PM",
            ),
          ),
      )
    }
  }

  private fun startPriceTickerSimulation() {
    // Only RealCoin is tracked, rates are set by Admin or defaults
  }

  fun updateRealCoinPrices(
    priceUsd: Double,
    priceEtb: Double,
    usdToEtbRate: Double,
  ) {
    _uiState.update { state ->
      val updatedRates = state.cryptoRates.map {
        if (it.symbol == "REAL") it.copy(priceUsd = priceUsd) else it
      }
      // Synchronize standard P2P market ads with the new ETB market price
      val updatedAds = state.p2pAds.map { ad ->
        if (ad.cryptoSymbol == "REAL" && ad.fiatCurrency == "ETB") {
          ad.copy(pricePerUnit = priceEtb)
        } else {
          ad
        }
      }
      state.copy(
        realPriceUsd = priceUsd,
        realPriceEtb = priceEtb,
        usdToEtbRate = usdToEtbRate,
        cryptoRates = updatedRates,
        p2pAds = updatedAds,
        notificationMessage = "RealCoin price updated: $%.4f USD / %.2f ETB (Rate: %.2f ETB/$)".format(priceUsd, priceEtb, usdToEtbRate),
      )
    }
  }

  // --- AUTHENTICATION ---

  fun registerUser(
    username: String,
    email: String,
    phone: String,
    password: String,
    referralCode: String? = null,
  ): Boolean {
    if (username.isBlank() || email.isBlank() || password.length < 6) {
      showToast("Please enter valid credentials (Password min 6 characters)")
      return false
    }

    _uiState.update {
      it.copy(
        realBalance = 0.0, // All new users start with 0 REAL
        userProfile =
          UserProfile(
            username = username,
            email = email,
            phone = phone.ifBlank { "+251 91 000 0000" },
            isLoggedIn = true,
            currentKycTier = KycTier.TIER_1,
            kycStatus = KycStatus.NOT_SUBMITTED,
          ),
        currentScreen = AppDestination.KYC_SUBMISSION, // Immediately route to KYC submission
        notificationMessage = "Account created! Please submit your KYC identity verification to access RealCoin.",
      )
    }
    return true
  }

  fun loginUser(identifier: String, password: String): Boolean {
    if (identifier.isBlank() || password.isBlank()) {
      showToast("Please provide username/email and password")
      return false
    }

    val username = if (identifier.contains("@")) identifier.substringBefore("@") else identifier

    _uiState.update {
      it.copy(
        userProfile =
          it.userProfile.copy(
            username = username,
            email = if (identifier.contains("@")) identifier else "$identifier@realcoin.network",
            isLoggedIn = true,
            kycStatus = KycStatus.APPROVED,
            currentKycTier = KycTier.TIER_3,
          ),
        currentScreen = AppDestination.MAIN, // Verified users log in straight to the main app
        notificationMessage = "Welcome back, $username! Signed in successfully.",
      )
    }
    return true
  }

  fun enterAsGuest() {
    _uiState.update {
      it.copy(
        userProfile = it.userProfile.copy(
          username = "GuestUser",
          isLoggedIn = true,
          kycStatus = KycStatus.NOT_SUBMITTED,
        ),
        currentScreen = AppDestination.KYC_SUBMISSION,
      )
    }
  }

  fun logout() {
    _uiState.update {
      it.copy(
        userProfile = it.userProfile.copy(isLoggedIn = false),
        currentScreen = AppDestination.LANDING,
        isAdminLoggedIn = false,
        notificationMessage = "You have logged out.",
      )
    }
  }

  // --- ADMIN PORTAL ---

  fun adminLogin(password: String): Boolean {
    if (password == RealCoinConstants.ADMIN_DEFAULT_PASSWORD || password == "admin" || password == "admin123") {
      _uiState.update {
        it.copy(
          isAdminLoggedIn = true,
          currentScreen = AppDestination.ADMIN_PANEL,
          notificationMessage = "Admin access granted.",
        )
      }
      return true
    } else {
      showToast("Invalid admin password! Hint: ${RealCoinConstants.ADMIN_DEFAULT_PASSWORD}")
      return false
    }
  }

  fun adminLogout() {
    _uiState.update {
      it.copy(
        isAdminLoggedIn = false,
        currentScreen = AppDestination.MAIN,
      )
    }
  }

  fun approveDeposit(depositId: String) {
    val deposit = _uiState.value.pendingDeposits.find { it.id == depositId } ?: return

    val newTx =
      TransactionItem(
        id = "tx-" + UUID.randomUUID().toString().take(8),
        type = TransactionType.DEPOSIT,
        title = "USDT BEP-20 Deposit (+10% Bonus)",
        amountReal = deposit.totalRealToCredit,
        amountUsd = deposit.amountUsd,
        amountEtb = deposit.totalRealToCredit * RealCoinConstants.REAL_PRICE_ETB,
        counterparty = deposit.depositAddress.take(6) + "..." + deposit.depositAddress.takeLast(4),
        timestamp = "Just now",
        status = TransactionStatus.COMPLETED,
        txHash = deposit.txHash,
      )

    _uiState.update { state ->
      val updatedDeposits =
        state.pendingDeposits.map {
          if (it.id == depositId) it.copy(status = TransactionStatus.COMPLETED) else it
        }
      state.copy(
        realBalance = state.realBalance + deposit.totalRealToCredit,
        pendingDeposits = updatedDeposits,
        transactions = listOf(newTx) + state.transactions,
        userProfile = state.userProfile.copy(
          totalDepositVolumeUsd = state.userProfile.totalDepositVolumeUsd + deposit.amountUsd
        ),
        notificationMessage = "Admin approved Deposit ${deposit.id}! Credited %.2f REAL (including 10%% bonus).".format(deposit.totalRealToCredit),
      )
    }
  }

  fun rejectDeposit(depositId: String) {
    _uiState.update { state ->
      val updatedDeposits =
        state.pendingDeposits.map {
          if (it.id == depositId) it.copy(status = TransactionStatus.REJECTED) else it
        }
      state.copy(
        pendingDeposits = updatedDeposits,
        notificationMessage = "Deposit $depositId was rejected by Admin.",
      )
    }
  }

  fun approveWithdrawal(withdrawId: String) {
    _uiState.update { state ->
      val updatedWithdrawals =
        state.pendingWithdrawals.map {
          if (it.id == withdrawId) it.copy(status = TransactionStatus.COMPLETED) else it
        }
      state.copy(
        pendingWithdrawals = updatedWithdrawals,
        notificationMessage = "Admin approved withdrawal $withdrawId. Payout sent to user.",
      )
    }
  }

  fun rejectWithdrawal(withdrawId: String) {
    val req = _uiState.value.pendingWithdrawals.find { it.id == withdrawId } ?: return
    _uiState.update { state ->
      val updatedWithdrawals =
        state.pendingWithdrawals.map {
          if (it.id == withdrawId) it.copy(status = TransactionStatus.REJECTED) else it
        }
      // Refund REAL coins back to balance
      state.copy(
        realBalance = state.realBalance + req.amountReal,
        pendingWithdrawals = updatedWithdrawals,
        notificationMessage = "Withdrawal $withdrawId rejected. %.2f REAL refunded to balance.".format(req.amountReal),
      )
    }
  }

  fun approveKyc(kycId: String) {
    _uiState.update { state ->
      val updatedSubmissions =
        state.kycSubmissions.map {
          if (it.id == kycId) it.copy(status = KycStatus.APPROVED) else it
        }
      val isCurrentUser = state.userProfile.kycSubmission?.id == kycId || state.kycSubmissions.any { it.id == kycId }
      val updatedProfile =
        if (isCurrentUser) {
          state.userProfile.copy(
            currentKycTier = KycTier.TIER_3,
            kycStatus = KycStatus.APPROVED,
          )
        } else {
          state.userProfile
        }

      // If current user is approved, directly go to MAIN app! (Bypassing landing/register)
      val nextScreen = if (state.currentScreen == AppDestination.KYC_WAITING || state.currentScreen == AppDestination.KYC_SUBMISSION) {
        AppDestination.MAIN
      } else {
        state.currentScreen
      }

      state.copy(
        kycSubmissions = updatedSubmissions,
        userProfile = updatedProfile,
        currentScreen = nextScreen,
        notificationMessage = "Admin approved KYC $kycId! Identity verified. Direct access granted to Main App.",
      )
    }
  }

  fun rejectKyc(kycId: String, reason: String = "ID image unclear or mismatched legal name") {
    _uiState.update { state ->
      val updatedSubmissions =
        state.kycSubmissions.map {
          if (it.id == kycId) it.copy(status = KycStatus.REJECTED, rejectionReason = reason) else it
        }
      val updatedProfile =
        state.userProfile.copy(
          kycStatus = KycStatus.REJECTED,
        )

      // If rejected, returned to landing page to resubmit
      val nextScreen = if (state.currentScreen == AppDestination.KYC_WAITING || state.currentScreen == AppDestination.MAIN || state.currentScreen == AppDestination.KYC_SUBMISSION) {
        AppDestination.LANDING
      } else {
        state.currentScreen
      }

      state.copy(
        kycSubmissions = updatedSubmissions,
        userProfile = updatedProfile,
        currentScreen = nextScreen,
        notificationMessage = "KYC was rejected: $reason. Returned to landing page to resubmit.",
      )
    }
  }

  fun resolveP2PDispute(orderId: String, releaseToBuyer: Boolean) {
    val order = _uiState.value.activeEscrowOrders.find { it.orderId == orderId } ?: return

    _uiState.update { state ->
      val updatedOrders =
        state.activeEscrowOrders.map {
          if (it.orderId == orderId) {
            it.copy(
              status = if (releaseToBuyer) EscrowStatus.COMPLETED else EscrowStatus.CANCELLED,
              disputeReason = "Resolved by Admin: ${if (releaseToBuyer) "Coins Released to Buyer" else "Refunded to Seller"}",
            )
          } else {
            it
          }
        }

      val newBalance =
        if (releaseToBuyer && order.type == P2PTradeType.BUY) {
          state.realBalance + order.cryptoAmount
        } else {
          state.realBalance
        }

      state.copy(
        activeEscrowOrders = updatedOrders,
        realBalance = newBalance,
        notificationMessage = "Admin resolved dispute $orderId: ${if (releaseToBuyer) "Coins released to Buyer" else "Refunded to Seller"}.",
      )
    }
  }

  // --- USER ACTIONS ---

  fun toggleBalanceVisibility() {
    _uiState.update { it.copy(isBalanceVisible = !it.isBalanceVisible) }
  }

  fun submitUsdtBep20Deposit(usdAmount: Double, txHash: String): Boolean {
    if (usdAmount < RealCoinConstants.MIN_DEPOSIT_USD) {
      showToast("Minimum deposit is $25.00 USD")
      return false
    }
    if (txHash.isBlank() || txHash.length < 10) {
      showToast("Please provide a valid BEP-20 Transaction Hash (TxID)")
      return false
    }

    val realCoins = usdAmount / RealCoinConstants.REAL_PRICE_USD
    val bonusCoins = realCoins * (RealCoinConstants.DEPOSIT_BONUS_PERCENTAGE / 100.0)
    val totalCoins = realCoins + bonusCoins

    val req =
      DepositRequest(
        id = "DEP-" + (1000..9999).random(),
        username = _uiState.value.userProfile.username,
        amountUsd = usdAmount,
        realCoinAmount = realCoins,
        bonusRealAmount = bonusCoins,
        totalRealToCredit = totalCoins,
        txHash = txHash,
        status = TransactionStatus.PENDING,
        timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
      )

    _uiState.update {
      it.copy(
        pendingDeposits = listOf(req) + it.pendingDeposits,
        notificationMessage = "USDT Deposit of $${"%.2f".format(usdAmount)} submitted! Will credit %.0f REAL (+10%% bonus: %.0f REAL) once verified.".format(totalCoins, bonusCoins),
      )
    }
    return true
  }

  fun submitInstantDemoDeposit(usdAmount: Double): Boolean {
    if (usdAmount < RealCoinConstants.MIN_DEPOSIT_USD) {
      showToast("Minimum deposit is $25.00 USD")
      return false
    }
    val realCoins = usdAmount / RealCoinConstants.REAL_PRICE_USD
    val bonusCoins = realCoins * (RealCoinConstants.DEPOSIT_BONUS_PERCENTAGE / 100.0)
    val totalCoins = realCoins + bonusCoins

    val tx =
      TransactionItem(
        id = "tx-" + UUID.randomUUID().toString().take(8),
        type = TransactionType.DEPOSIT,
        title = "USDT BEP-20 Deposit (+10% Bonus)",
        amountReal = totalCoins,
        amountUsd = usdAmount,
        amountEtb = totalCoins * RealCoinConstants.REAL_PRICE_ETB,
        counterparty = RealCoinConstants.USDT_BEP20_DEPOSIT_ADDRESS.take(6) + "..." + RealCoinConstants.USDT_BEP20_DEPOSIT_ADDRESS.takeLast(4),
        timestamp = "Just now",
        status = TransactionStatus.COMPLETED,
        txHash = "0x" + UUID.randomUUID().toString().replace("-", ""),
      )

    _uiState.update {
      it.copy(
        realBalance = it.realBalance + totalCoins,
        transactions = listOf(tx) + it.transactions,
        userProfile = it.userProfile.copy(
          totalDepositVolumeUsd = it.userProfile.totalDepositVolumeUsd + usdAmount
        ),
        notificationMessage = "Deposit credited! +${"%.0f".format(realCoins)} REAL + 10%% BONUS (${"%.0f".format(bonusCoins)} REAL) added to your balance.",
      )
    }
    return true
  }

  fun submitWithdrawal(method: String = "USDT (BEP-20)", realAmount: Double, accountDetails: String): Boolean {
    val currentBal = _uiState.value.realBalance
    val minWithdrawReal = RealCoinConstants.minWithdrawReal(_uiState.value.realPriceUsd)
    if (realAmount < minWithdrawReal) {
      showToast("Minimum withdrawal is $50.00 USDT (≈ %,.0f REAL at today's rate of $%.4f/REAL)".format(minWithdrawReal, _uiState.value.realPriceUsd))
      return false
    }
    if (realAmount > currentBal) {
      showToast("Insufficient REAL balance")
      return false
    }
    if (accountDetails.isBlank()) {
      showToast("Please enter your USDT BEP-20 wallet address")
      return false
    }

    val usdValue = realAmount * _uiState.value.realPriceUsd
    val etbValue = realAmount * _uiState.value.realPriceEtb
    val payoutMethod = "USDT (BEP-20)"

    val req =
      WithdrawRequest(
        id = "WTH-" + (1000..9999).random(),
        username = _uiState.value.userProfile.username,
        amountReal = realAmount,
        amountUsd = usdValue,
        amountEtb = etbValue,
        method = payoutMethod,
        accountDetails = accountDetails,
        status = TransactionStatus.PENDING,
        timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
      )

    val tx =
      TransactionItem(
        id = "tx-" + UUID.randomUUID().toString().take(8),
        type = TransactionType.WITHDRAW,
        title = "USDT BEP-20 Withdrawal ($%.2f USDT)".format(usdValue),
        amountReal = realAmount,
        amountUsd = usdValue,
        amountEtb = etbValue,
        counterparty = accountDetails,
        timestamp = "Just now",
        status = TransactionStatus.PENDING,
        txHash = req.id,
      )

    _uiState.update {
      it.copy(
        realBalance = currentBal - realAmount,
        pendingWithdrawals = listOf(req) + it.pendingWithdrawals,
        transactions = listOf(tx) + it.transactions,
        notificationMessage = "Withdrawal request of %.0f REAL ($%.2f USDT BEP-20) submitted for Admin review.".format(realAmount, usdValue),
      )
    }
    return true
  }

  fun submitKycVerification(
    fullName: String,
    dateOfBirth: String,
    nationality: String,
    residentialAddress: String,
    docType: String,
    docNumber: String,
    frontPhotoUri: String? = null,
    backPhotoUri: String? = null,
    selfiePhotoUri: String? = null,
  ): Boolean {
    if (fullName.isBlank() || docNumber.isBlank()) {
      showToast("Please provide full legal name and document number")
      return false
    }

    val submission =
      KycSubmission(
        id = "KYC-" + (1000..9999).random(),
        fullName = fullName,
        email = _uiState.value.userProfile.email,
        dateOfBirth = dateOfBirth,
        nationality = nationality,
        residentialAddress = residentialAddress,
        docType = docType,
        docNumber = docNumber,
        frontPhotoAttached = frontPhotoUri != null,
        backPhotoAttached = backPhotoUri != null,
        selfiePhotoAttached = selfiePhotoUri != null,
        frontPhotoUri = frontPhotoUri,
        backPhotoUri = backPhotoUri,
        selfiePhotoUri = selfiePhotoUri,
        status = KycStatus.PENDING_REVIEW,
        submittedAt = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date()),
      )

    _uiState.update {
      it.copy(
        userProfile =
          it.userProfile.copy(
            kycStatus = KycStatus.PENDING_REVIEW,
            kycSubmission = submission,
          ),
        kycSubmissions = listOf(submission) + it.kycSubmissions,
        currentScreen = AppDestination.KYC_WAITING, // Must wait in KYC submitted page until admin approves
        notificationMessage = "KYC submitted successfully! Please wait on this page until Admin reviews your ID.",
      )
    }
    return true
  }

  fun checkKycStatus() {
    val currentKyc = _uiState.value.userProfile.kycStatus
    when (currentKyc) {
      KycStatus.APPROVED -> {
        _uiState.update { it.copy(currentScreen = AppDestination.MAIN, notificationMessage = "KYC Approved! Direct access granted to Main App.") }
      }
      KycStatus.REJECTED -> {
        _uiState.update { it.copy(currentScreen = AppDestination.LANDING, notificationMessage = "KYC was rejected. Redirected to landing page to resubmit.") }
      }
      KycStatus.PENDING_REVIEW -> {
        showToast("Your verification is still under review by Admin. Please wait.")
      }
      KycStatus.NOT_SUBMITTED -> {
        _uiState.update { it.copy(currentScreen = AppDestination.KYC_SUBMISSION) }
      }
    }
  }

  // --- HELP CENTER & SUPPORT TICKETS ---

  fun submitSupportTicket(
    category: String,
    subject: String,
    message: String,
    contactInfo: String,
  ): Boolean {
    if (subject.isBlank() || message.isBlank()) {
      showToast("Please enter ticket subject and detailed message")
      return false
    }

    val ticket =
      SupportTicket(
        id = "TCK-" + (1000..9999).random(),
        username = _uiState.value.userProfile.username.ifBlank { "GuestUser" },
        email = contactInfo.ifBlank { _uiState.value.userProfile.email.ifBlank { "support@realcoin.network" } },
        category = category,
        subject = subject,
        message = message,
        status = TicketStatus.OPEN,
        createdAt = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date()),
      )

    _uiState.update {
      it.copy(
        supportTickets = listOf(ticket) + it.supportTickets,
        notificationMessage = "Support ticket #${ticket.id} submitted! Support team and Admin will reply shortly.",
      )
    }
    return true
  }

  fun adminReplySupportTicket(ticketId: String, replyText: String, resolve: Boolean = false) {
    if (replyText.isBlank()) {
      showToast("Please enter a reply message")
      return
    }

    val timeNow = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date())
    _uiState.update { state ->
      val updated = state.supportTickets.map {
        if (it.id == ticketId) {
          it.copy(
            adminReply = replyText,
            repliedAt = timeNow,
            status = if (resolve) TicketStatus.RESOLVED else TicketStatus.ANSWERED,
          )
        } else it
      }
      state.copy(
        supportTickets = updated,
        notificationMessage = "Reply sent to user on Ticket #$ticketId.",
      )
    }
  }

  fun submitKycUpgrade(
    fullName: String,
    docType: String,
    docNumber: String,
  ): Boolean {
    return submitKycVerification(
      fullName = fullName,
      dateOfBirth = "1996-08-24",
      nationality = "Ethiopian",
      residentialAddress = "Addis Ababa, Ethiopia",
      docType = docType,
      docNumber = docNumber,
    )
  }

  // --- P2P & MY ADS ---

  fun createMyAd(
    tradeType: P2PTradeType,
    cryptoAmount: Double,
    pricePerUnit: Double,
    minLimit: Double,
    maxLimit: Double,
    paymentMethods: List<String>,
  ): Boolean {
    if (cryptoAmount <= 0) {
      showToast("Please enter a valid crypto amount")
      return false
    }
    if (pricePerUnit <= 0) {
      showToast("Please enter a valid price in ETB")
      return false
    }
    if (paymentMethods.isEmpty()) {
      showToast("Please select at least one Ethiopian payment method")
      return false
    }

    val newAd =
      P2PAd(
        id = "my-ad-" + UUID.randomUUID().toString().take(6),
        traderName = _uiState.value.userProfile.username,
        tradeType = tradeType,
        cryptoSymbol = "REAL",
        fiatCurrency = "ETB",
        pricePerUnit = pricePerUnit,
        availableCrypto = cryptoAmount,
        minLimit = minLimit,
        maxLimit = maxLimit,
        completionRate = 100.0,
        completedOrders = 0,
        paymentMethods = paymentMethods,
        isUserAd = true,
        isActive = true,
      )

    _uiState.update {
      it.copy(
        userAds = listOf(newAd) + it.userAds,
        p2pAds = listOf(newAd) + it.p2pAds,
        notificationMessage = "New ${tradeType.name} Ad published successfully at %.2f ETB!".format(pricePerUnit),
      )
    }
    return true
  }

  fun toggleAdActive(adId: String) {
    _uiState.update { state ->
      val updatedUserAds =
        state.userAds.map { if (it.id == adId) it.copy(isActive = !it.isActive) else it }
      val updatedMarketAds =
        state.p2pAds.map { if (it.id == adId) it.copy(isActive = !it.isActive) else it }
      state.copy(
        userAds = updatedUserAds,
        p2pAds = updatedMarketAds,
        notificationMessage = "Ad status updated.",
      )
    }
  }

  fun hasActiveEscrowOrders(): Boolean {
    return _uiState.value.activeEscrowOrders.any {
      it.status == EscrowStatus.PENDING_PAYMENT ||
      it.status == EscrowStatus.PAID_PENDING_RELEASE ||
      it.status == EscrowStatus.DISPUTED
    }
  }

  fun deleteMyAd(adId: String): Boolean {
    if (hasActiveEscrowOrders()) {
      showToast("Cannot delete ad while you have an active escrow order.")
      return false
    }
    _uiState.update { state ->
      state.copy(
        userAds = state.userAds.filterNot { it.id == adId },
        p2pAds = state.p2pAds.filterNot { it.id == adId },
        notificationMessage = "Ad deleted successfully.",
      )
    }
    return true
  }

  fun addPaymentAccount(bankName: String, accountNumber: String): Boolean {
    if (accountNumber.isBlank() || accountNumber.length < 4) {
      showToast("Please enter a valid account number")
      return false
    }
    val legalName = _uiState.value.userProfile.kycSubmission?.fullName?.takeIf { it.isNotBlank() }
      ?: _uiState.value.userProfile.username
    val newAccount = com.example.data.model.UserPaymentAccount(
      id = "pay-" + UUID.randomUUID().toString().take(6),
      bankName = bankName,
      accountNumber = accountNumber.trim(),
      accountName = legalName,
      addedAt = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()),
    )
    _uiState.update { state ->
      state.copy(
        userProfile = state.userProfile.copy(
          paymentAccounts = state.userProfile.paymentAccounts + newAccount
        ),
        notificationMessage = "Payment account for $bankName added ($legalName).",
      )
    }
    return true
  }

  fun deletePaymentAccount(accountId: String): Boolean {
    if (hasActiveEscrowOrders()) {
      showToast("Cannot delete payment account while you have an active escrow order.")
      return false
    }
    _uiState.update { state ->
      state.copy(
        userProfile = state.userProfile.copy(
          paymentAccounts = state.userProfile.paymentAccounts.filterNot { it.id == accountId }
        ),
        notificationMessage = "Payment account deleted.",
      )
    }
    return true
  }

  fun createP2POrder(
    ad: P2PAd,
    cryptoAmount: Double,
    fiatAmount: Double,
    isTakerBuy: Boolean = (ad.tradeType == P2PTradeType.SELL),
  ): EscrowOrder? {
    val effectiveType = if (isTakerBuy) P2PTradeType.BUY else P2PTradeType.SELL
    if (!isTakerBuy && _uiState.value.realBalance < cryptoAmount) {
      showToast("Insufficient REAL balance to lock in escrow")
      return null
    }

    val orderId = "P2P-" + (1000..9999).random()
    val order =
      EscrowOrder(
        orderId = orderId,
        adId = ad.id,
        traderName = ad.traderName,
        type = effectiveType,
        cryptoAmount = cryptoAmount,
        fiatAmount = fiatAmount,
        fiatCurrency = ad.fiatCurrency,
        paymentMethod = ad.paymentMethods.firstOrNull() ?: "Telebirr",
        status = EscrowStatus.PENDING_PAYMENT,
        createdAt = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
      )

    _uiState.update {
      val newBal = if (!isTakerBuy) it.realBalance - cryptoAmount else it.realBalance
      it.copy(
        realBalance = newBal,
        activeEscrowOrders = listOf(order) + it.activeEscrowOrders,
        notificationMessage = "Escrow locked! Order $orderId created with ${ad.traderName}.",
      )
    }
    return order
  }

  fun markOrderAsPaid(orderId: String) {
    _uiState.update { state ->
      val updated =
        state.activeEscrowOrders.map {
          if (it.orderId == orderId) it.copy(status = EscrowStatus.PAID_PENDING_RELEASE) else it
        }
      state.copy(
        activeEscrowOrders = updated,
        notificationMessage = "Marked order $orderId as paid! Seller notified to verify and release REAL.",
      )
    }
  }

  fun raiseDispute(orderId: String, reason: String) {
    _uiState.update { state ->
      val updated =
        state.activeEscrowOrders.map {
          if (it.orderId == orderId) {
            it.copy(
              status = EscrowStatus.DISPUTED,
              disputeReason = reason.ifBlank { "Buyer claims payment sent, seller has not released." },
            )
          } else {
            it
          }
        }
      state.copy(
        activeEscrowOrders = updated,
        notificationMessage = "Dispute raised for Order $orderId! Escalated to RealCoin Admin team.",
      )
    }
  }

  fun releaseEscrowCoins(orderId: String) {
    val order = _uiState.value.activeEscrowOrders.find { it.orderId == orderId } ?: return

    _uiState.update { state ->
      val updatedOrders =
        state.activeEscrowOrders.map {
          if (it.orderId == orderId) it.copy(status = EscrowStatus.COMPLETED) else it
        }
      val isBuyer = order.type == P2PTradeType.BUY
      val newBalance = if (isBuyer) state.realBalance + order.cryptoAmount else state.realBalance

      val newTx =
        TransactionItem(
          id = "tx-" + UUID.randomUUID().toString().take(8),
          type = if (isBuyer) TransactionType.P2P_BUY else TransactionType.P2P_SELL,
          title = "P2P ${if (isBuyer) "Buy" else "Sell"} Complete",
          amountReal = order.cryptoAmount,
          amountUsd = order.cryptoAmount * state.realPriceUsd,
          amountEtb = order.cryptoAmount * state.realPriceEtb,
          counterparty = order.traderName,
          timestamp = "Just now",
          status = TransactionStatus.COMPLETED,
          txHash = "ESC-" + orderId,
        )

      state.copy(
        activeEscrowOrders = updatedOrders,
        realBalance = newBalance,
        transactions = listOf(newTx) + state.transactions,
        notificationMessage = "Escrow completed! %.2f REAL released to buyer.".format(order.cryptoAmount),
      )
    }
  }

  // --- SEND & SPIN ---

  fun sendCrypto(recipientAddress: String, amount: Double, memo: String): Boolean {
    val currentBal = _uiState.value.realBalance
    if (amount <= 0 || amount > currentBal) {
      showToast("Insufficient REAL balance or invalid amount")
      return false
    }

    val newTx =
      TransactionItem(
        id = "tx-" + UUID.randomUUID().toString().take(8),
        type = TransactionType.SEND,
        title = "Sent REAL",
        amountReal = amount,
        amountUsd = amount * _uiState.value.realPriceUsd,
        amountEtb = amount * _uiState.value.realPriceEtb,
        counterparty = recipientAddress.take(6) + "..." + recipientAddress.takeLast(4),
        timestamp = "Just now",
        status = TransactionStatus.COMPLETED,
        txHash = "0x" + UUID.randomUUID().toString().replace("-", "").take(16),
      )

    _uiState.update {
      it.copy(
        realBalance = currentBal - amount,
        transactions = listOf(newTx) + it.transactions,
        notificationMessage = "Successfully sent %.2f REAL to %s".format(amount, newTx.counterparty),
      )
    }
    return true
  }

  fun claimDailyLevelReward(): Boolean {
    val profile = _uiState.value.userProfile
    val level = profile.currentLevel
    if (level == com.example.data.model.UserLevel.NONE) {
      showToast("Deposit volume must reach $100 to unlock Starter Level daily rewards (30 REAL/day)")
      return false
    }

    val now = System.currentTimeMillis()
    val lastClaim = profile.lastDailyRewardClaimTime
    val canClaim = lastClaim == 0L || (now - lastClaim >= 24 * 60 * 60 * 1000L)

    if (!canClaim) {
      val remainingHours = 24 - ((now - lastClaim) / (1000 * 60 * 60))
      showToast("Daily level reward already claimed for today! Available again in ${remainingHours.coerceAtLeast(1)}h.")
      return false
    }

    val rewardAmount = level.dailyRewardReal
    val newTx =
      TransactionItem(
        id = "tx-" + UUID.randomUUID().toString().take(8),
        type = TransactionType.REWARD,
        title = "Daily ${level.title} Reward",
        amountReal = rewardAmount,
        amountUsd = rewardAmount * _uiState.value.realPriceUsd,
        amountEtb = rewardAmount * _uiState.value.realPriceEtb,
        counterparty = "RealCoin VIP Rewards",
        timestamp = "Just now",
        status = TransactionStatus.COMPLETED,
        txHash = "LVL-" + UUID.randomUUID().toString().take(8),
      )

    _uiState.update {
      it.copy(
        realBalance = it.realBalance + rewardAmount,
        transactions = listOf(newTx) + it.transactions,
        userProfile = it.userProfile.copy(lastDailyRewardClaimTime = now),
        notificationMessage = "Successfully claimed Daily ${level.title} reward of +%.0f REAL!".format(rewardAmount),
      )
    }
    return true
  }

  fun spinWheel(
    isPaidSpin: Boolean,
    prizeAmount: Double,
    onFinished: (Double) -> Unit,
  ) {
    if (_uiState.value.isSpinning) return

    val currentBal = _uiState.value.realBalance
    val now = System.currentTimeMillis()
    val lastFreeSpin = _uiState.value.userProfile.lastFreeSpinTime
    val isFreeAvailable = lastFreeSpin == 0L || (now - lastFreeSpin >= 24 * 60 * 60 * 1000L)

    if (!isPaidSpin) {
      if (!isFreeAvailable) {
        showToast("Daily free spin already used! You can spin for 10 REAL coin.")
        return
      }
    } else {
      if (currentBal < 10.0) {
        showToast("Insufficient REAL coin balance! 10 REAL required to play.")
        return
      }
    }

    viewModelScope.launch {
      val balanceAfterFee = if (isPaidSpin) currentBal - 10.0 else currentBal
      val initialTxs =
        if (isPaidSpin) {
          listOf(
            TransactionItem(
              id = "tx-" + UUID.randomUUID().toString().take(8),
              type = TransactionType.REWARD,
              title = "Spin Wheel Entry Fee",
              amountReal = 10.0,
              amountUsd = 10.0 * _uiState.value.realPriceUsd,
              amountEtb = 10.0 * _uiState.value.realPriceEtb,
              counterparty = "Lucky Spin",
              timestamp = "Just now",
              status = TransactionStatus.COMPLETED,
              txHash = "SPIN-FEE-" + UUID.randomUUID().toString().take(6),
            )
          )
        } else {
          emptyList()
        }

      _uiState.update {
        it.copy(
          isSpinning = true,
          realBalance = balanceAfterFee,
          transactions = initialTxs + it.transactions,
          lastSpinWonAmount = null,
        )
      }

      delay(3200)

      val winTx =
        TransactionItem(
          id = "tx-" + UUID.randomUUID().toString().take(8),
          type = TransactionType.REWARD,
          title = "Lucky Wheel Reward",
          amountReal = prizeAmount,
          amountUsd = prizeAmount * _uiState.value.realPriceUsd,
          amountEtb = prizeAmount * _uiState.value.realPriceEtb,
          counterparty = "Lucky Spin",
          timestamp = "Just now",
          status = TransactionStatus.COMPLETED,
          txHash = "SPIN-WIN-" + UUID.randomUUID().toString().take(6),
        )

      _uiState.update {
        it.copy(
          isSpinning = false,
          realBalance = it.realBalance + prizeAmount,
          lastSpinWonAmount = prizeAmount,
          transactions = listOf(winTx) + it.transactions,
          userProfile =
            it.userProfile.copy(
              lastFreeSpinTime = if (!isPaidSpin) now else it.userProfile.lastFreeSpinTime,
              spinAvailable = isPaidSpin,
            ),
          notificationMessage = "Congratulations! Won %.0f REAL coins!".format(prizeAmount),
        )
      }
      onFinished(prizeAmount)
    }
  }

  fun clearNotification() {
    _uiState.update { it.copy(notificationMessage = null) }
  }

  fun showToast(msg: String) {
    _uiState.update { it.copy(notificationMessage = msg) }
  }
}
