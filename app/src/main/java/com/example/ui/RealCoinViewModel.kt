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
import com.example.data.model.UserLevel
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

enum class AppDestination { LANDING, LOGIN, REGISTER, MAIN, KYC_SUBMISSION, KYC_WAITING, HELP_CENTER, ADMIN_LOGIN, ADMIN_PANEL }

data class RealCoinUiState(
  val currentScreen: AppDestination = AppDestination.LANDING,
  val realBalance: Double = 0.0,
  val realPriceUsd: Double = RealCoinConstants.REAL_PRICE_USD,
  val realPriceEtb: Double = RealCoinConstants.REAL_PRICE_ETB,
  val usdToEtbRate: Double = RealCoinConstants.USD_TO_ETB_RATE,
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
  val balanceUsd: Double get() = realBalance * realPriceUsd
  val balanceEtb: Double get() = realBalance * realPriceEtb
}

class RealCoinViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(RealCoinUiState())
  val uiState: StateFlow<RealCoinUiState> = _uiState.asStateFlow()

  // Session-only credentials. A real deployment must replace this with server authentication.
  private var registeredUsername: String? = null
  private var registeredEmail: String? = null
  private var registeredPassword: String? = null

  init {
    loadInitialData()
  }

  private fun now(pattern: String = "MMM dd, yyyy HH:mm"): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(Date())

  private fun newId(prefix: String): String = "$prefix-${UUID.randomUUID()}"

  private fun requireLoggedIn(): Boolean {
    if (!_uiState.value.userProfile.isLoggedIn) {
      showToast("Please sign in first")
      return false
    }
    return true
  }

  private fun requireAdmin(): Boolean {
    if (!_uiState.value.isAdminLoggedIn) {
      showToast("Admin authentication required")
      return false
    }
    return true
  }

  fun navigateTo(destination: AppDestination) {
    val state = _uiState.value
    if (destination == AppDestination.MAIN) {
      if (!state.userProfile.isLoggedIn) {
        _uiState.update { it.copy(currentScreen = AppDestination.LOGIN, notificationMessage = "Please sign in to continue.") }
        return
      }
      when (state.userProfile.kycStatus) {
        KycStatus.PENDING_REVIEW -> {
          _uiState.update { it.copy(currentScreen = AppDestination.KYC_WAITING, notificationMessage = "Your verification is under review. Please wait for Admin approval.") }
          return
        }
        KycStatus.NOT_SUBMITTED -> {
          _uiState.update { it.copy(currentScreen = AppDestination.KYC_SUBMISSION, notificationMessage = "Please submit KYC verification to access Main App.") }
          return
        }
        KycStatus.REJECTED -> {
          _uiState.update { it.copy(currentScreen = AppDestination.KYC_SUBMISSION, notificationMessage = "KYC rejected. Please resubmit valid ID.") }
          return
        }
        KycStatus.APPROVED -> Unit
      }
    }
    _uiState.update { it.copy(currentScreen = destination) }
  }

  private fun loadInitialData() {
    val rates = listOf(CryptoRate("REAL", "RealCoin", RealCoinConstants.REAL_PRICE_USD, 0.0))
    val ads = listOf(
      P2PAd("ad-1", "AddisExpress", P2PTradeType.BUY, pricePerUnit = 5.00, availableCrypto = 12000.0, minLimit = 500.0, maxLimit = 60000.0, paymentMethods = listOf("Telebirr", "CBE", "Awash Bank")),
      P2PAd("ad-2", "NileTrader_VIP", P2PTradeType.BUY, pricePerUnit = 5.02, availableCrypto = 8500.0, minLimit = 1000.0, maxLimit = 42500.0, paymentMethods = listOf("Telebirr", "Bank of Abyssinia", "CBE Birr")),
      P2PAd("ad-3", "HabeshaExchange", P2PTradeType.SELL, pricePerUnit = 4.98, availableCrypto = 15000.0, minLimit = 500.0, maxLimit = 75000.0, paymentMethods = listOf("CBE", "Dashen Bank", "Telebirr")),
      P2PAd("ad-4", "ShegerCrypto", P2PTradeType.SELL, pricePerUnit = 4.95, availableCrypto = 6200.0, minLimit = 250.0, maxLimit = 31000.0, paymentMethods = listOf("Awash Bank", "Bank of Abyssinia")),
    )
    _uiState.update { it.copy(cryptoRates = rates, p2pAds = ads, transactions = emptyList(), kycSubmissions = emptyList(), pendingDeposits = emptyList(), pendingWithdrawals = emptyList(), supportTickets = emptyList()) }
  }

  fun updateRealCoinPrices(priceUsd: Double, priceEtb: Double, usdToEtbRate: Double) {
    if (priceUsd <= 0.0 || priceEtb <= 0.0 || usdToEtbRate <= 0.0) { showToast("Invalid market price"); return }
    _uiState.update { state ->
      state.copy(
        realPriceUsd = priceUsd,
        realPriceEtb = priceEtb,
        usdToEtbRate = usdToEtbRate,
        cryptoRates = state.cryptoRates.map { if (it.symbol == "REAL") it.copy(priceUsd = priceUsd) else it },
        notificationMessage = "RealCoin price updated: $%.4f USD / %.2f ETB".format(priceUsd, priceEtb),
      )
    }
  }

  fun registerUser(username: String, email: String, phone: String, password: String, referralCode: String? = null): Boolean {
    val cleanUser = username.trim()
    val cleanEmail = email.trim()
    if (!cleanUser.matches(Regex("^[A-Za-z0-9_.-]{3,32}$")) || !cleanEmail.contains("@") || password.length < 6) {
      showToast("Please enter a valid username, email and password (minimum 6 characters)")
      return false
    }
    if (registeredUsername != null || registeredEmail != null) {
      showToast("An account is already registered in this session")
      return false
    }
    registeredUsername = cleanUser
    registeredEmail = cleanEmail
    registeredPassword = password
    _uiState.update { it.copy(realBalance = 0.0, userProfile = UserProfile(username = cleanUser, email = cleanEmail, phone = phone.trim(), isLoggedIn = true, currentKycTier = KycTier.TIER_1, kycStatus = KycStatus.NOT_SUBMITTED), currentScreen = AppDestination.KYC_SUBMISSION, notificationMessage = "Account created! Please submit your KYC identity verification.") }
    return true
  }

  fun loginUser(identifier: String, password: String): Boolean {
    val id = identifier.trim()
    val valid = registeredPassword != null && password == registeredPassword && (id.equals(registeredUsername, true) || id.equals(registeredEmail, true))
    if (!valid) { showToast("Invalid username/email or password"); return false }
    _uiState.update { state ->
      val profile = state.userProfile.copy(username = registeredUsername!!, email = registeredEmail!!, isLoggedIn = true)
      state.copy(userProfile = profile, currentScreen = when (profile.kycStatus) { KycStatus.APPROVED -> AppDestination.MAIN; KycStatus.PENDING_REVIEW -> AppDestination.KYC_WAITING; else -> AppDestination.KYC_SUBMISSION }, notificationMessage = "Welcome back, ${profile.username}!")
    }
    return true
  }

  fun enterAsGuest() {
    _uiState.update { it.copy(userProfile = UserProfile(username = "GuestUser", isLoggedIn = true, kycStatus = KycStatus.NOT_SUBMITTED), currentScreen = AppDestination.KYC_SUBMISSION, notificationMessage = "Guest access requires identity verification.") }
  }

  fun logout() {
    _uiState.update { it.copy(userProfile = it.userProfile.copy(isLoggedIn = false), currentScreen = AppDestination.LANDING, isAdminLoggedIn = false, notificationMessage = "You have logged out.") }
  }

  fun adminLogin(password: String): Boolean {
    if (password.isBlank() || password != RealCoinConstants.ADMIN_DEFAULT_PASSWORD) { showToast("Invalid admin password"); return false }
    _uiState.update { it.copy(isAdminLoggedIn = true, currentScreen = AppDestination.ADMIN_PANEL, notificationMessage = "Admin access granted.") }
    return true
  }

  fun adminLogout() { _uiState.update { it.copy(isAdminLoggedIn = false, currentScreen = if (it.userProfile.isLoggedIn) AppDestination.MAIN else AppDestination.LANDING) } }

  fun approveDeposit(depositId: String) {
    if (!requireAdmin()) return
    _uiState.update { state ->
      val deposit = state.pendingDeposits.firstOrNull { it.id == depositId }
      if (deposit == null) return@update state.copy(notificationMessage = "Deposit not found")
      if (deposit.status != TransactionStatus.PENDING) return@update state.copy(notificationMessage = "Deposit already processed")
      val ownerIsCurrent = state.userProfile.username.equals(deposit.username, true)
      val tx = TransactionItem(newId("tx"), TransactionType.DEPOSIT, "USDT BEP-20 Deposit (+10% Bonus)", deposit.totalRealToCredit, deposit.amountUsd, deposit.totalRealToCredit * state.realPriceEtb, deposit.depositAddress.take(6) + "..." + deposit.depositAddress.takeLast(4), "Just now", TransactionStatus.COMPLETED, deposit.txHash)
      state.copy(
        realBalance = if (ownerIsCurrent) state.realBalance + deposit.totalRealToCredit else state.realBalance,
        pendingDeposits = state.pendingDeposits.map { if (it.id == depositId) it.copy(status = TransactionStatus.COMPLETED) else it },
        transactions = if (ownerIsCurrent) listOf(tx) + state.transactions else state.transactions,
        userProfile = if (ownerIsCurrent) state.userProfile.copy(totalDepositVolumeUsd = state.userProfile.totalDepositVolumeUsd + deposit.amountUsd) else state.userProfile,
        notificationMessage = if (ownerIsCurrent) "Deposit approved and credited %.2f REAL.".format(deposit.totalRealToCredit) else "Deposit approved for ${deposit.username}.",
      )
    }
  }

  fun rejectDeposit(depositId: String) {
    if (!requireAdmin()) return
    _uiState.update { state ->
      val d = state.pendingDeposits.firstOrNull { it.id == depositId } ?: return@update state.copy(notificationMessage = "Deposit not found")
      if (d.status != TransactionStatus.PENDING) return@update state.copy(notificationMessage = "Deposit already processed")
      state.copy(pendingDeposits = state.pendingDeposits.map { if (it.id == depositId) it.copy(status = TransactionStatus.REJECTED) else it }, notificationMessage = "Deposit $depositId rejected.")
    }
  }

  fun approveWithdrawal(withdrawId: String) {
    if (!requireAdmin()) return
    _uiState.update { state ->
      val req = state.pendingWithdrawals.firstOrNull { it.id == withdrawId } ?: return@update state.copy(notificationMessage = "Withdrawal not found")
      if (req.status != TransactionStatus.PENDING) return@update state.copy(notificationMessage = "Withdrawal already processed")
      state.copy(pendingWithdrawals = state.pendingWithdrawals.map { if (it.id == withdrawId) it.copy(status = TransactionStatus.COMPLETED) else it }, transactions = state.transactions.map { if (it.txHash == withdrawId && it.status == TransactionStatus.PENDING) it.copy(status = TransactionStatus.COMPLETED) else it }, notificationMessage = "Withdrawal $withdrawId approved.")
    }
  }

  fun rejectWithdrawal(withdrawId: String) {
    if (!requireAdmin()) return
    _uiState.update { state ->
      val req = state.pendingWithdrawals.firstOrNull { it.id == withdrawId } ?: return@update state.copy(notificationMessage = "Withdrawal not found")
      if (req.status != TransactionStatus.PENDING) return@update state.copy(notificationMessage = "Withdrawal already processed")
      val isCurrent = state.userProfile.username.equals(req.username, true)
      state.copy(
        realBalance = if (isCurrent) state.realBalance + req.amountReal else state.realBalance,
        pendingWithdrawals = state.pendingWithdrawals.map { if (it.id == withdrawId) it.copy(status = TransactionStatus.REJECTED) else it },
        transactions = state.transactions.map { if (it.txHash == withdrawId && it.status == TransactionStatus.PENDING) it.copy(status = TransactionStatus.REJECTED) else it },
        notificationMessage = if (isCurrent) "Withdrawal rejected. %.2f REAL refunded.".format(req.amountReal) else "Withdrawal $withdrawId rejected.",
      )
    }
  }

  fun approveKyc(kycId: String) {
    if (!requireAdmin()) return
    _uiState.update { state ->
      val target = state.kycSubmissions.firstOrNull { it.id == kycId } ?: return@update state.copy(notificationMessage = "KYC submission not found")
      if (target.status != KycStatus.PENDING_REVIEW) return@update state.copy(notificationMessage = "KYC submission already processed")
      val isCurrent = state.userProfile.kycSubmission?.id == kycId
      state.copy(
        kycSubmissions = state.kycSubmissions.map { if (it.id == kycId) it.copy(status = KycStatus.APPROVED) else it },
        userProfile = if (isCurrent) state.userProfile.copy(currentKycTier = KycTier.TIER_3, kycStatus = KycStatus.APPROVED) else state.userProfile,
        currentScreen = if (isCurrent && (state.currentScreen == AppDestination.KYC_WAITING || state.currentScreen == AppDestination.KYC_SUBMISSION)) AppDestination.MAIN else state.currentScreen,
        notificationMessage = "KYC approved. Identity verified.",
      )
    }
  }

  fun rejectKyc(kycId: String, reason: String = "ID image unclear or mismatched legal name") {
    if (!requireAdmin()) return
    _uiState.update { state ->
      val target = state.kycSubmissions.firstOrNull { it.id == kycId } ?: return@update state.copy(notificationMessage = "KYC submission not found")
      if (target.status != KycStatus.PENDING_REVIEW) return@update state.copy(notificationMessage = "KYC submission already processed")
      val isCurrent = state.userProfile.kycSubmission?.id == kycId
      state.copy(
        kycSubmissions = state.kycSubmissions.map { if (it.id == kycId) it.copy(status = KycStatus.REJECTED, rejectionReason = reason) else it },
        userProfile = if (isCurrent) state.userProfile.copy(kycStatus = KycStatus.REJECTED) else state.userProfile,
        currentScreen = if (isCurrent && (state.currentScreen == AppDestination.KYC_WAITING || state.currentScreen == AppDestination.KYC_SUBMISSION || state.currentScreen == AppDestination.MAIN)) AppDestination.KYC_SUBMISSION else state.currentScreen,
        notificationMessage = if (isCurrent) "KYC rejected: $reason" else "KYC submission rejected.",
      )
    }
  }

  fun resolveP2PDispute(orderId: String, releaseToBuyer: Boolean) {
    if (!requireAdmin()) return
    _uiState.update { state ->
      val order = state.activeEscrowOrders.firstOrNull { it.orderId == orderId } ?: return@update state.copy(notificationMessage = "Order not found")
      if (order.status != EscrowStatus.DISPUTED) return@update state.copy(notificationMessage = "Order is not in dispute")
      val currentIsBuyer = order.type == P2PTradeType.BUY
      val currentIsSeller = order.type == P2PTradeType.SELL
      val refund = !releaseToBuyer && currentIsSeller
      val credit = releaseToBuyer && currentIsBuyer
      val tx = if (credit) TransactionItem(newId("tx"), TransactionType.P2P_BUY, "P2P Buy Complete", order.cryptoAmount, order.cryptoAmount * state.realPriceUsd, order.cryptoAmount * state.realPriceEtb, order.traderName, "Just now", TransactionStatus.COMPLETED, "ESC-$orderId") else null
      state.copy(activeEscrowOrders = state.activeEscrowOrders.map { if (it.orderId == orderId) it.copy(status = if (releaseToBuyer) EscrowStatus.COMPLETED else EscrowStatus.CANCELLED, disputeReason = "Resolved by Admin") else it }, realBalance = state.realBalance + if (credit || refund) order.cryptoAmount else 0.0, transactions = if (tx != null) listOf(tx) + state.transactions else state.transactions, notificationMessage = "Dispute $orderId resolved.")
    }
  }

  fun toggleBalanceVisibility() { _uiState.update { it.copy(isBalanceVisible = !it.isBalanceVisible) } }

  fun submitUsdtBep20Deposit(usdAmount: Double, txHash: String): Boolean {
    if (!requireLoggedIn()) return false
    val hash = txHash.trim()
    if (usdAmount < RealCoinConstants.MIN_DEPOSIT_USD || !hash.matches(Regex("^0x[a-fA-F0-9]{20,}$"))) { showToast("Minimum deposit is $25.00 USD and a valid BEP-20 transaction hash is required"); return false }
    if (_uiState.value.pendingDeposits.any { it.txHash.equals(hash, true) }) { showToast("This transaction hash has already been submitted"); return false }
    val real = usdAmount / _uiState.value.realPriceUsd
    val bonus = real * RealCoinConstants.DEPOSIT_BONUS_PERCENTAGE / 100.0
    val req = DepositRequest(newId("DEP"), _uiState.value.userProfile.username, usdAmount, real, bonus, real + bonus, txHash = hash, status = TransactionStatus.PENDING, timestamp = now("HH:mm"))
    _uiState.update { it.copy(pendingDeposits = listOf(req) + it.pendingDeposits, notificationMessage = "Deposit submitted for Admin verification.") }
    return true
  }

  fun submitInstantDemoDeposit(usdAmount: Double): Boolean {
    showToast("Instant demo deposits are disabled. Submit a real BEP-20 transaction for Admin verification.")
    return false
  }

  fun submitWithdrawal(method: String = "USDT (BEP-20)", realAmount: Double, accountDetails: String): Boolean {
    if (!requireLoggedIn()) return false
    val state = _uiState.value
    val address = accountDetails.trim()
    val min = RealCoinConstants.minWithdrawReal(state.realPriceUsd)
    if (realAmount < min) { showToast("Minimum withdrawal is $50.00 USDT (≈ %,.0f REAL)".format(min)); return false }
    if (realAmount > state.realBalance) { showToast("Insufficient REAL balance"); return false }
    if (!address.matches(Regex("^0x[a-fA-F0-9]{40}$"))) { showToast("Please enter a valid USDT BEP-20 wallet address"); return false }
    if (state.pendingWithdrawals.any { it.username.equals(state.userProfile.username, true) && it.status == TransactionStatus.PENDING }) { showToast("You already have a pending withdrawal"); return false }
    val usd = realAmount * state.realPriceUsd
    val etb = realAmount * state.realPriceEtb
    val req = WithdrawRequest(newId("WTH"), state.userProfile.username, realAmount, usd, etb, "USDT (BEP-20)", address, TransactionStatus.PENDING, now("HH:mm"))
    val tx = TransactionItem(newId("tx"), TransactionType.WITHDRAW, "USDT BEP-20 Withdrawal ($%.2f USDT)".format(usd), realAmount, usd, etb, address.take(6) + "..." + address.takeLast(4), "Just now", TransactionStatus.PENDING, req.id)
    _uiState.update { it.copy(realBalance = it.realBalance - realAmount, pendingWithdrawals = listOf(req) + it.pendingWithdrawals, transactions = listOf(tx) + it.transactions, notificationMessage = "Withdrawal submitted for Admin review.") }
    return true
  }

  fun submitKycVerification(fullName: String, dateOfBirth: String, nationality: String, residentialAddress: String, docType: String, docNumber: String, frontPhotoUri: String? = null, backPhotoUri: String? = null, selfiePhotoUri: String? = null): Boolean {
    if (!requireLoggedIn()) return false
    val state = _uiState.value
    if (state.userProfile.kycStatus == KycStatus.PENDING_REVIEW) { showToast("Your KYC is already under review"); return false }
    if (fullName.trim().length < 3 || dateOfBirth.isBlank() || nationality.isBlank() || residentialAddress.isBlank() || docType.isBlank() || docNumber.trim().length < 4) { showToast("Please complete all KYC fields"); return false }
    if (frontPhotoUri.isNullOrBlank() || selfiePhotoUri.isNullOrBlank()) { showToast("Please attach the required ID front photo and selfie"); return false }
    if (docNumber.trim().equals(state.userProfile.kycSubmission?.docNumber, true) && state.userProfile.kycStatus != KycStatus.REJECTED) { showToast("This document has already been submitted"); return false }
    val submission = KycSubmission(newId("KYC"), fullName.trim(), state.userProfile.email, dateOfBirth.trim(), nationality.trim(), residentialAddress.trim(), docType.trim(), docNumber.trim(), frontPhotoAttached = true, backPhotoAttached = !backPhotoUri.isNullOrBlank(), selfiePhotoAttached = true, frontPhotoUri = frontPhotoUri, backPhotoUri = backPhotoUri, selfiePhotoUri = selfiePhotoUri, status = KycStatus.PENDING_REVIEW, submittedAt = now())
    _uiState.update { it.copy(userProfile = it.userProfile.copy(kycStatus = KycStatus.PENDING_REVIEW, kycSubmission = submission), kycSubmissions = listOf(submission) + it.kycSubmissions, currentScreen = AppDestination.KYC_WAITING, notificationMessage = "KYC submitted successfully. Please wait for Admin review.") }
    return true
  }

  fun checkKycStatus() {
    when (_uiState.value.userProfile.kycStatus) {
      KycStatus.APPROVED -> navigateTo(AppDestination.MAIN)
      KycStatus.REJECTED -> navigateTo(AppDestination.KYC_SUBMISSION)
      KycStatus.PENDING_REVIEW -> showToast("Your verification is still under review by Admin.")
      KycStatus.NOT_SUBMITTED -> navigateTo(AppDestination.KYC_SUBMISSION)
    }
  }

  fun submitSupportTicket(category: String, subject: String, message: String, contactInfo: String): Boolean {
    if (!requireLoggedIn()) return false
    if (subject.trim().length < 3 || message.trim().length < 5) { showToast("Please enter a ticket subject and detailed message"); return false }
    val state = _uiState.value
    val ticket = SupportTicket(newId("TCK"), state.userProfile.username.ifBlank { "GuestUser" }, contactInfo.trim().ifBlank { state.userProfile.email }, category, subject.trim(), message.trim(), status = TicketStatus.OPEN, createdAt = now("MMM dd, HH:mm"))
    _uiState.update { it.copy(supportTickets = listOf(ticket) + it.supportTickets, notificationMessage = "Support ticket submitted successfully.") }
    return true
  }

  fun adminReplySupportTicket(ticketId: String, replyText: String, resolve: Boolean = false) {
    if (!requireAdmin()) return
    if (replyText.isBlank()) { showToast("Please enter a reply message"); return }
    _uiState.update { state ->
      if (state.supportTickets.none { it.id == ticketId }) return@update state.copy(notificationMessage = "Ticket not found")
      state.copy(supportTickets = state.supportTickets.map { if (it.id == ticketId) it.copy(adminReply = replyText.trim(), repliedAt = now("MMM dd, HH:mm"), status = if (resolve) TicketStatus.RESOLVED else TicketStatus.ANSWERED else it) }, notificationMessage = "Reply sent to user.")
    }
  }

  fun submitKycUpgrade(fullName: String, docType: String, docNumber: String): Boolean = submitKycVerification(fullName, "", "", "", docType, docNumber, null, null, null)

  fun createMyAd(tradeType: P2PTradeType, cryptoAmount: Double, pricePerUnit: Double, minLimit: Double, maxLimit: Double, paymentMethods: List<String>): Boolean {
    if (!requireLoggedIn()) return false
    val state = _uiState.value
    if (state.userProfile.kycStatus != KycStatus.APPROVED) { showToast("KYC approval is required to create a P2P ad"); return false }
    if (cryptoAmount <= 0 || pricePerUnit <= 0 || minLimit <= 0 || maxLimit < minLimit || paymentMethods.isEmpty()) { showToast("Please enter valid ad limits and payment methods"); return false }
    if (tradeType == P2PTradeType.SELL && cryptoAmount > state.realBalance) { showToast("Insufficient REAL balance to create this sell ad"); return false }
    val ad = P2PAd("my-ad-${UUID.randomUUID().toString().take(8)}", state.userProfile.username, tradeType, pricePerUnit = pricePerUnit, availableCrypto = cryptoAmount, minLimit = minLimit, maxLimit = maxLimit, paymentMethods = paymentMethods.distinct(), isUserAd = true, isActive = true)
    _uiState.update { it.copy(userAds = listOf(ad) + it.userAds, p2pAds = listOf(ad) + it.p2pAds, notificationMessage = "P2P ad published successfully.") }
    return true
  }

  fun toggleAdActive(adId: String) {
    _uiState.update { state ->
      val ad = state.userAds.firstOrNull { it.id == adId } ?: return@update state.copy(notificationMessage = "Ad not found")
      val active = !ad.isActive
      state.copy(userAds = state.userAds.map { if (it.id == adId) it.copy(isActive = active) else it }, p2pAds = state.p2pAds.map { if (it.id == adId) it.copy(isActive = active) else it }, notificationMessage = "Ad status updated.")
    }
  }

  fun hasActiveEscrowOrders(): Boolean = _uiState.value.activeEscrowOrders.any { it.status == EscrowStatus.PENDING_PAYMENT || it.status == EscrowStatus.PAID_PENDING_RELEASE || it.status == EscrowStatus.DISPUTED }

  fun deleteMyAd(adId: String): Boolean {
    if (_uiState.value.activeEscrowOrders.any { it.adId == adId && it.status != EscrowStatus.COMPLETED && it.status != EscrowStatus.CANCELLED }) { showToast("Cannot delete ad while it has an active escrow order"); return false }
    _uiState.update { it.copy(userAds = it.userAds.filterNot { a -> a.id == adId }, p2pAds = it.p2pAds.filterNot { a -> a.id == adId }, notificationMessage = "Ad deleted successfully.") }
    return true
  }

  fun addPaymentAccount(bankName: String, accountNumber: String): Boolean {
    if (!requireLoggedIn()) return false
    if (bankName.isBlank() || accountNumber.trim().length < 4) { showToast("Please enter a valid payment account"); return false }
    val state = _uiState.value
    val name = state.userProfile.kycSubmission?.fullName?.takeIf { it.isNotBlank() } ?: state.userProfile.username
    val account = com.example.data.model.UserPaymentAccount(newId("pay"), bankName.trim(), accountNumber.trim(), name, now("MMM dd, yyyy"))
    _uiState.update { it.copy(userProfile = it.userProfile.copy(paymentAccounts = it.userProfile.paymentAccounts + account), notificationMessage = "Payment account added.") }
    return true
  }

  fun deletePaymentAccount(accountId: String): Boolean {
    if (hasActiveEscrowOrders()) { showToast("Cannot delete payment account while you have an active escrow order"); return false }
    _uiState.update { it.copy(userProfile = it.userProfile.copy(paymentAccounts = it.userProfile.paymentAccounts.filterNot { a -> a.id == accountId }), notificationMessage = "Payment account deleted.") }
    return true
  }

  fun createP2POrder(ad: P2PAd, cryptoAmount: Double, fiatAmount: Double, isTakerBuy: Boolean = ad.tradeType == P2PTradeType.SELL): EscrowOrder? {
    if (!requireLoggedIn()) return null
    val state = _uiState.value
    if (state.userProfile.kycStatus != KycStatus.APPROVED) { showToast("KYC approval is required for P2P trading"); return null }
    val effectiveType = if (isTakerBuy) P2PTradeType.BUY else P2PTradeType.SELL
    if (!ad.isActive || cryptoAmount <= 0 || cryptoAmount > ad.availableCrypto || fiatAmount <= 0 || fiatAmount < ad.minLimit || fiatAmount > ad.maxLimit) { showToast("Invalid P2P order amount or inactive ad"); return null }
    val expectedFiat = cryptoAmount * ad.pricePerUnit
    if (kotlin.math.abs(expectedFiat - fiatAmount) > maxOf(0.01, expectedFiat * 0.02)) { showToast("Fiat amount does not match the ad price"); return null }
    if (!isTakerBuy && state.realBalance < cryptoAmount) { showToast("Insufficient REAL balance to lock in escrow"); return null }
    if (state.activeEscrowOrders.any { it.adId == ad.id && it.status != EscrowStatus.COMPLETED && it.status != EscrowStatus.CANCELLED }) { showToast("This ad already has an active order in this session"); return null }
    val order = EscrowOrder(newId("P2P"), ad.id, ad.traderName, effectiveType, cryptoAmount, fiatAmount, ad.fiatCurrency, ad.paymentMethods.firstOrNull() ?: "Telebirr", EscrowStatus.PENDING_PAYMENT, now("HH:mm"))
    _uiState.update { it.copy(realBalance = if (!isTakerBuy) it.realBalance - cryptoAmount else it.realBalance, activeEscrowOrders = listOf(order) + it.activeEscrowOrders, notificationMessage = "Escrow order ${order.orderId} created.") }
    return order
  }

  private fun currentOrder(orderId: String): EscrowOrder? = _uiState.value.activeEscrowOrders.firstOrNull { it.orderId == orderId }

  fun markOrderAsPaid(orderId: String) {
    val order = currentOrder(orderId) ?: run { showToast("Order not found"); return }
    if (order.status != EscrowStatus.PENDING_PAYMENT) { showToast("Order cannot be marked paid in its current state"); return }
    _uiState.update { it.copy(activeEscrowOrders = it.activeEscrowOrders.map { o -> if (o.orderId == orderId) o.copy(status = EscrowStatus.PAID_PENDING_RELEASE) else o }, notificationMessage = "Payment marked as sent. Seller/admin must verify it.") }
  }

  fun raiseDispute(orderId: String, reason: String) {
    val order = currentOrder(orderId) ?: run { showToast("Order not found"); return }
    if (order.status != EscrowStatus.PAID_PENDING_RELEASE && order.status != EscrowStatus.PENDING_PAYMENT) { showToast("Order cannot be disputed in its current state"); return }
    _uiState.update { it.copy(activeEscrowOrders = it.activeEscrowOrders.map { o -> if (o.orderId == orderId) o.copy(status = EscrowStatus.DISPUTED, disputeReason = reason.ifBlank { "User requested dispute" }) else o }, notificationMessage = "Dispute raised for order $orderId.") }
  }

  fun releaseEscrowCoins(orderId: String) {
    val order = currentOrder(orderId) ?: run { showToast("Order not found"); return }
    if (!requireAdmin()) return
    if (order.status != EscrowStatus.PAID_PENDING_RELEASE) { showToast("Escrow can only be released after payment is marked paid"); return }
    _uiState.update { state ->
      val buyerGetsCoins = order.type == P2PTradeType.BUY
      val tx = if (buyerGetsCoins) TransactionItem(newId("tx"), TransactionType.P2P_BUY, "P2P Buy Complete", order.cryptoAmount, order.cryptoAmount * state.realPriceUsd, order.cryptoAmount * state.realPriceEtb, order.traderName, "Just now", TransactionStatus.COMPLETED, "ESC-$orderId") else null
      state.copy(activeEscrowOrders = state.activeEscrowOrders.map { if (it.orderId == orderId) it.copy(status = EscrowStatus.COMPLETED) else it }, realBalance = state.realBalance + if (buyerGetsCoins) order.cryptoAmount else 0.0, transactions = if (tx != null) listOf(tx) + state.transactions else state.transactions, notificationMessage = "Escrow $orderId released once.")
    }
  }

  fun sendCrypto(recipientAddress: String, amount: Double, memo: String): Boolean {
    if (!requireLoggedIn()) return false
    val state = _uiState.value
    if (!recipientAddress.matches(Regex("^0x[a-fA-F0-9]{40}$"))) { showToast("Please enter a valid REAL wallet address"); return false }
    if (amount <= 0 || amount > state.realBalance) { showToast("Insufficient REAL balance or invalid amount"); return false }
    val tx = TransactionItem(newId("tx"), TransactionType.SEND, "Sent REAL", amount, amount * state.realPriceUsd, amount * state.realPriceEtb, recipientAddress.take(6) + "..." + recipientAddress.takeLast(4), "Just now", TransactionStatus.PENDING, newId("SEND"))
    _uiState.update { it.copy(realBalance = it.realBalance - amount, transactions = listOf(tx) + it.transactions, notificationMessage = "Transfer submitted. Blockchain confirmation is required.") }
    return true
  }

  fun claimDailyLevelReward(): Boolean {
    if (!requireLoggedIn()) return false
    val state = _uiState.value
    val level = state.userProfile.currentLevel
    if (level == UserLevel.NONE) { showToast("Deposit volume must reach $100 to unlock Starter Level daily rewards"); return false }
    val nowMs = System.currentTimeMillis()
    if (state.userProfile.lastDailyRewardClaimTime != 0L && nowMs - state.userProfile.lastDailyRewardClaimTime < 86_400_000L) { showToast("Daily reward already claimed"); return false }
    val amount = level.dailyRewardReal
    val tx = TransactionItem(newId("tx"), TransactionType.REWARD, "Daily ${level.title} Reward", amount, amount * state.realPriceUsd, amount * state.realPriceEtb, "RealCoin VIP Rewards", "Just now", TransactionStatus.COMPLETED, newId("LVL"))
    _uiState.update { it.copy(realBalance = it.realBalance + amount, transactions = listOf(tx) + it.transactions, userProfile = it.userProfile.copy(lastDailyRewardClaimTime = nowMs), notificationMessage = "Daily reward +%.0f REAL claimed.".format(amount)) }
    return true
  }

  fun spinWheel(isPaidSpin: Boolean, prizeAmount: Double, onFinished: (Double) -> Unit) {
    if (!requireLoggedIn() || _uiState.value.isSpinning) return
    val state = _uiState.value
    if (prizeAmount < 0.0 || prizeAmount > 1000.0) { showToast("Invalid spin reward"); return }
    val nowMs = System.currentTimeMillis()
    val freeAvailable = state.userProfile.lastFreeSpinTime == 0L || nowMs - state.userProfile.lastFreeSpinTime >= 86_400_000L
    if (!isPaidSpin && !freeAvailable) { showToast("Daily free spin already used"); return }
    if (isPaidSpin && state.realBalance < 10.0) { showToast("10 REAL is required for a paid spin"); return }
    viewModelScope.launch {
      _uiState.update { it.copy(isSpinning = true, realBalance = it.realBalance - if (isPaidSpin) 10.0 else 0.0, lastSpinWonAmount = null) }
      delay(3200)
      _uiState.update { current ->
        val tx = TransactionItem(newId("tx"), TransactionType.REWARD, "Lucky Wheel Reward", prizeAmount, prizeAmount * current.realPriceUsd, prizeAmount * current.realPriceEtb, "Lucky Spin", "Just now", TransactionStatus.COMPLETED, newId("SPIN"))
        current.copy(isSpinning = false, realBalance = current.realBalance + prizeAmount, lastSpinWonAmount = prizeAmount, transactions = listOf(tx) + current.transactions, userProfile = current.userProfile.copy(lastFreeSpinTime = if (!isPaidSpin) nowMs else current.userProfile.lastFreeSpinTime, spinAvailable = isPaidSpin), notificationMessage = "Congratulations! Won %.0f REAL coins!".format(prizeAmount))
      }
      onFinished(prizeAmount)
    }
  }

  fun clearNotification() { _uiState.update { it.copy(notificationMessage = null) } }
  fun showToast(msg: String) { _uiState.update { it.copy(notificationMessage = msg) } }
}
