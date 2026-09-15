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
  val balanceUsd get() = realBalance * realPriceUsd
  val balanceEtb get() = realBalance * realPriceEtb
}

class RealCoinViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(RealCoinUiState())
  val uiState: StateFlow<RealCoinUiState> = _uiState.asStateFlow()

  // Local credentials only protect the current process. Real production auth must be server-side.
  private var localUsername: String? = null
  private var localEmail: String? = null
  private var localPassword: String? = null

  init { loadInitialData() }

  fun navigateTo(destination: AppDestination) {
    val s = _uiState.value
    if (destination == AppDestination.ADMIN_PANEL && !s.isAdminLoggedIn) {
      _uiState.update { it.copy(currentScreen = AppDestination.ADMIN_LOGIN, notificationMessage = "Admin authentication is required.") }
      return
    }
    if (destination == AppDestination.MAIN) {
      if (!s.userProfile.isLoggedIn) {
        _uiState.update { it.copy(currentScreen = AppDestination.LOGIN, notificationMessage = "Please sign in first.") }
        return
      }
      when (s.userProfile.kycStatus) {
        KycStatus.PENDING_REVIEW -> { _uiState.update { it.copy(currentScreen = AppDestination.KYC_WAITING, notificationMessage = "Your verification is under review. Please wait for Admin approval.") }; return }
        KycStatus.NOT_SUBMITTED -> { _uiState.update { it.copy(currentScreen = AppDestination.KYC_SUBMISSION, notificationMessage = "Please submit KYC verification to access Main App.") }; return }
        KycStatus.REJECTED -> { _uiState.update { it.copy(currentScreen = AppDestination.KYC_SUBMISSION, notificationMessage = "KYC was rejected. Please resubmit valid information.") }; return }
        KycStatus.APPROVED -> Unit
      }
    }
    _uiState.update { it.copy(currentScreen = destination) }
  }

  private fun loadInitialData() {
    val ads = listOf(
      P2PAd("ad-1", "AddisExpress", P2PTradeType.BUY, "REAL", "ETB", 5.00, 12000.0, 500.0, 60000.0, 99.8, 1420, listOf("Telebirr", "CBE", "Awash Bank")),
      P2PAd("ad-2", "NileTrader_VIP", P2PTradeType.BUY, "REAL", "ETB", 5.02, 8500.0, 1000.0, 42500.0, 99.1, 890, listOf("Telebirr", "Bank of Abyssinia", "CBE Birr")),
      P2PAd("ad-3", "HabeshaExchange", P2PTradeType.SELL, "REAL", "ETB", 4.98, 15000.0, 500.0, 75000.0, 99.5, 1150, listOf("CBE", "Dashen Bank", "Telebirr")),
      P2PAd("ad-4", "ShegerCrypto", P2PTradeType.SELL, "REAL", "ETB", 4.95, 6200.0, 250.0, 31000.0, 98.4, 430, listOf("Awash Bank", "Bank of Abyssinia")),
    )
    _uiState.update { it.copy(cryptoRates = listOf(CryptoRate("REAL", "RealCoin", RealCoinConstants.REAL_PRICE_USD, 5.8)), p2pAds = ads) }
  }

  private fun adminRequired(): Boolean {
    if (!_uiState.value.isAdminLoggedIn) { showToast("Admin authentication required."); return false }
    return true
  }

  fun updateRealCoinPrices(priceUsd: Double, priceEtb: Double, usdToEtbRate: Double) {
    if (!priceUsd.isFinite() || !priceEtb.isFinite() || !usdToEtbRate.isFinite() || priceUsd <= 0 || priceEtb <= 0 || usdToEtbRate <= 0) { showToast("Invalid price information."); return }
    _uiState.update { s ->
      s.copy(realPriceUsd = priceUsd, realPriceEtb = priceEtb, usdToEtbRate = usdToEtbRate,
        cryptoRates = s.cryptoRates.map { if (it.symbol == "REAL") it.copy(priceUsd = priceUsd) else it },
        p2pAds = s.p2pAds.map { a -> if (!a.isUserAd && a.cryptoSymbol == "REAL" && a.fiatCurrency == "ETB") a.copy(pricePerUnit = priceEtb) else a },
        notificationMessage = "RealCoin price updated: $%.4f USD / %.2f ETB (Rate: %.2f ETB/$)".format(priceUsd, priceEtb, usdToEtbRate))
    }
  }

  fun registerUser(username: String, email: String, phone: String, password: String, referralCode: String? = null): Boolean {
    val u = username.trim(); val e = email.trim()
    if (!Regex("^[A-Za-z0-9_.-]{3,32}$").matches(u)) { showToast("Username must contain 3-32 valid characters."); return false }
    if (!Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(e)) { showToast("Please enter a valid email address."); return false }
    if (password.length < 6) { showToast("Password must be at least 6 characters."); return false }
    localUsername = u; localEmail = e; localPassword = password
    _uiState.update { it.copy(realBalance = 0.0, userProfile = UserProfile(username = u, email = e, phone = phone.trim(), isLoggedIn = true, currentKycTier = KycTier.TIER_1, kycStatus = KycStatus.NOT_SUBMITTED), currentScreen = AppDestination.KYC_SUBMISSION, notificationMessage = "Account created! Please submit your KYC identity verification to access RealCoin.") }
    return true
  }

  fun loginUser(identifier: String, password: String): Boolean {
    val i = identifier.trim(); val u = localUsername; val e = localEmail; val p = localPassword
    if (i.isBlank() || password.isBlank() || u == null || e == null || p == null || p != password || (!i.equals(u, true) && !i.equals(e, true))) { showToast("Invalid username/email or password. Please register or use your saved account."); return false }
    val status = _uiState.value.userProfile.kycStatus
    val screen = when (status) { KycStatus.APPROVED -> AppDestination.MAIN; KycStatus.PENDING_REVIEW -> AppDestination.KYC_WAITING; KycStatus.REJECTED, KycStatus.NOT_SUBMITTED -> AppDestination.KYC_SUBMISSION }
    _uiState.update { it.copy(userProfile = it.userProfile.copy(isLoggedIn = true), currentScreen = screen, notificationMessage = "Welcome back, ${it.userProfile.username}! Signed in successfully.") }
    return true
  }

  fun enterAsGuest() { _uiState.update { it.copy(userProfile = it.userProfile.copy(username = "GuestUser", email = "", isLoggedIn = true, kycStatus = KycStatus.NOT_SUBMITTED, currentKycTier = KycTier.TIER_1), currentScreen = AppDestination.KYC_SUBMISSION) } }
  fun logout() { _uiState.update { it.copy(userProfile = it.userProfile.copy(isLoggedIn = false), currentScreen = AppDestination.LANDING, isAdminLoggedIn = false, notificationMessage = "You have logged out.") } }

  fun adminLogin(password: String): Boolean {
    if (password.isBlank()) { showToast("Please enter the admin password."); return false }
    if (password != RealCoinConstants.ADMIN_DEFAULT_PASSWORD) { showToast("Invalid admin credentials."); return false }
    _uiState.update { it.copy(isAdminLoggedIn = true, currentScreen = AppDestination.ADMIN_PANEL, notificationMessage = "Admin access granted.") }; return true
  }
  fun adminLogout() { _uiState.update { it.copy(isAdminLoggedIn = false, currentScreen = if (it.userProfile.isLoggedIn) it.currentScreen else AppDestination.LANDING, notificationMessage = "Admin session ended.") } }

  fun approveDeposit(depositId: String) {
    if (!adminRequired()) return
    val d = _uiState.value.pendingDeposits.find { it.id == depositId } ?: return
    if (d.status != TransactionStatus.PENDING) { showToast("Deposit $depositId has already been processed."); return }
    val currentUser = _uiState.value.userProfile.username
    if (d.username.isNotBlank() && currentUser.isNotBlank() && !d.username.equals(currentUser, true)) { showToast("This client cannot credit another user's deposit."); return }
    val tx = TransactionItem("tx-${UUID.randomUUID().toString().take(8)}", TransactionType.DEPOSIT, "USDT BEP-20 Deposit (+10% Bonus)", d.totalRealToCredit, d.amountUsd, d.totalRealToCredit * _uiState.value.realPriceEtb, shortAddress(d.depositAddress.ifBlank { RealCoinConstants.USDT_BEP20_DEPOSIT_ADDRESS }), "Just now", TransactionStatus.COMPLETED, d.txHash)
    _uiState.update { s -> s.copy(realBalance = s.realBalance + d.totalRealToCredit, pendingDeposits = s.pendingDeposits.map { if (it.id == depositId) it.copy(status = TransactionStatus.COMPLETED) else it }, transactions = listOf(tx) + s.transactions, userProfile = s.userProfile.copy(totalDepositVolumeUsd = s.userProfile.totalDepositVolumeUsd + d.amountUsd), notificationMessage = "Admin approved Deposit ${d.id}! Credited %.2f REAL.".format(d.totalRealToCredit)) }
  }

  fun rejectDeposit(depositId: String) {
    if (!adminRequired()) return
    _uiState.update { s -> val d = s.pendingDeposits.find { it.id == depositId }; if (d == null) s.copy(notificationMessage = "Deposit not found.") else if (d.status != TransactionStatus.PENDING) s.copy(notificationMessage = "Deposit already processed.") else s.copy(pendingDeposits = s.pendingDeposits.map { if (it.id == depositId) it.copy(status = TransactionStatus.REJECTED) else it }, notificationMessage = "Deposit $depositId was rejected by Admin.") }
  }

  fun approveWithdrawal(withdrawId: String) {
    if (!adminRequired()) return
    _uiState.update { s -> val w = s.pendingWithdrawals.find { it.id == withdrawId }; if (w == null) s.copy(notificationMessage = "Withdrawal not found.") else if (w.status != TransactionStatus.PENDING) s.copy(notificationMessage = "Withdrawal already processed.") else s.copy(pendingWithdrawals = s.pendingWithdrawals.map { if (it.id == withdrawId) it.copy(status = TransactionStatus.COMPLETED) else it }, transactions = s.transactions.map { if (it.txHash == withdrawId && it.status == TransactionStatus.PENDING) it.copy(status = TransactionStatus.COMPLETED) else it }, notificationMessage = "Admin approved withdrawal $withdrawId.") }
  }

  fun rejectWithdrawal(withdrawId: String) {
    if (!adminRequired()) return
    _uiState.update { s -> val w = s.pendingWithdrawals.find { it.id == withdrawId }; if (w == null) s.copy(notificationMessage = "Withdrawal not found.") else if (w.status != TransactionStatus.PENDING) s.copy(notificationMessage = "Withdrawal already processed.") else s.copy(realBalance = s.realBalance + w.amountReal, pendingWithdrawals = s.pendingWithdrawals.map { if (it.id == withdrawId) it.copy(status = TransactionStatus.REJECTED) else it }, transactions = s.transactions.map { if (it.txHash == withdrawId && it.status == TransactionStatus.PENDING) it.copy(status = TransactionStatus.REJECTED) else it }, notificationMessage = "Withdrawal $withdrawId rejected. %.2f REAL refunded.".format(w.amountReal)) }
  }

  fun approveKyc(kycId: String) {
    if (!adminRequired()) return
    _uiState.update { s ->
      val k = s.kycSubmissions.find { it.id == kycId } ?: return@update s.copy(notificationMessage = "KYC submission not found.")
      if (k.status != KycStatus.PENDING_REVIEW) return@update s.copy(notificationMessage = "KYC already processed.")
      val mine = s.userProfile.kycSubmission?.id == kycId
      s.copy(kycSubmissions = s.kycSubmissions.map { if (it.id == kycId) it.copy(status = KycStatus.APPROVED) else it }, userProfile = if (mine) s.userProfile.copy(kycStatus = KycStatus.APPROVED, currentKycTier = KycTier.TIER_3, kycSubmission = k.copy(status = KycStatus.APPROVED)) else s.userProfile, currentScreen = if (mine && s.userProfile.isLoggedIn && (s.currentScreen == AppDestination.KYC_WAITING || s.currentScreen == AppDestination.KYC_SUBMISSION)) AppDestination.MAIN else s.currentScreen, notificationMessage = "Admin approved KYC $kycId. Identity verified.")
    }
  }

  fun rejectKyc(kycId: String, reason: String = "ID image unclear or mismatched legal name") {
    if (!adminRequired()) return
    val r = reason.trim().ifBlank { "ID image unclear or mismatched legal name" }
    _uiState.update { s ->
      val k = s.kycSubmissions.find { it.id == kycId } ?: return@update s.copy(notificationMessage = "KYC submission not found.")
      if (k.status != KycStatus.PENDING_REVIEW) return@update s.copy(notificationMessage = "KYC already processed.")
      val mine = s.userProfile.kycSubmission?.id == kycId
      val rejected = k.copy(status = KycStatus.REJECTED, rejectionReason = r)
      s.copy(kycSubmissions = s.kycSubmissions.map { if (it.id == kycId) rejected else it }, userProfile = if (mine) s.userProfile.copy(kycStatus = KycStatus.REJECTED, kycSubmission = rejected) else s.userProfile, currentScreen = if (mine) AppDestination.KYC_SUBMISSION else s.currentScreen, notificationMessage = "KYC was rejected: $r")
    }
  }

  fun resolveP2PDispute(orderId: String, releaseToBuyer: Boolean) {
    if (!adminRequired()) return
    val order = _uiState.value.activeEscrowOrders.find { it.orderId == orderId } ?: return
    if (order.status != EscrowStatus.DISPUTED) { showToast("Only disputed orders can be resolved."); return }
    _uiState.update { s ->
      val newStatus = if (releaseToBuyer) EscrowStatus.COMPLETED else EscrowStatus.CANCELLED
      val refund = !releaseToBuyer && order.type == P2PTradeType.SELL
      val creditBuyer = releaseToBuyer && order.type == P2PTradeType.BUY
      s.copy(activeEscrowOrders = s.activeEscrowOrders.map { if (it.orderId == orderId) it.copy(status = newStatus, disputeReason = "Resolved by Admin") else it }, realBalance = when { refund || creditBuyer -> s.realBalance + order.cryptoAmount; else -> s.realBalance }, notificationMessage = "Admin resolved dispute $orderId.")
    }
  }

  fun toggleBalanceVisibility() { _uiState.update { it.copy(isBalanceVisible = !it.isBalanceVisible) } }

  fun submitUsdtBep20Deposit(usdAmount: Double, txHash: String): Boolean {
    val s = _uiState.value
    if (!s.userProfile.isLoggedIn) { showToast("Please sign in before submitting a deposit."); return false }
    if (!usdAmount.isFinite() || usdAmount < RealCoinConstants.MIN_DEPOSIT_USD) { showToast("Minimum deposit is $25.00 USD"); return false }
    val hash = txHash.trim()
    if (!Regex("^0x[a-fA-F0-9]{64}$").matches(hash)) { showToast("Please provide a valid 64-character BEP-20 transaction hash."); return false }
    if (s.pendingDeposits.any { it.txHash.equals(hash, true) } || s.transactions.any { it.txHash.equals(hash, true) }) { showToast("This transaction hash has already been submitted."); return false }
    val real = usdAmount / s.realPriceUsd
    val bonus = real * (RealCoinConstants.DEPOSIT_BONUS_PERCENTAGE / 100.0)
    val req = DepositRequest("DEP-${UUID.randomUUID().toString().take(8)}", s.userProfile.username, usdAmount, real, bonus, real + bonus, hash, TransactionStatus.PENDING, SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
    _uiState.update { it.copy(pendingDeposits = listOf(req) + it.pendingDeposits, notificationMessage = "USDT Deposit of $${"%.2f".format(usdAmount)} submitted for Admin verification.") }
    return true
  }

  fun submitInstantDemoDeposit(usdAmount: Double): Boolean { showToast("Demo deposits are disabled. Submit a real BEP-20 deposit for Admin verification."); return false }

  fun submitWithdrawal(method: String = "USDT (BEP-20)", realAmount: Double, accountDetails: String): Boolean {
    val s = _uiState.value
    if (!s.userProfile.isLoggedIn) { showToast("Please sign in before requesting a withdrawal."); return false }
    if (!realAmount.isFinite() || realAmount <= 0 || realAmount > s.realBalance) { showToast("Insufficient REAL balance or invalid amount."); return false }
    val min = RealCoinConstants.minWithdrawReal(s.realPriceUsd)
    if (realAmount < min) { showToast("Minimum withdrawal is $50.00 USDT (≈ %,.0f REAL at today's rate of $%.4f/REAL)".format(min, s.realPriceUsd)); return false }
    val wallet = accountDetails.trim()
    if (!Regex("^0x[a-fA-F0-9]{40}$").matches(wallet)) { showToast("Please enter a valid USDT BEP-20 wallet address."); return false }
    val usd = realAmount * s.realPriceUsd; val etb = realAmount * s.realPriceEtb
    val req = WithdrawRequest("WTH-${UUID.randomUUID().toString().take(8)}", s.userProfile.username, realAmount, usd, etb, "USDT (BEP-20)", wallet, TransactionStatus.PENDING, SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
    val tx = TransactionItem("tx-${UUID.randomUUID().toString().take(8)}", TransactionType.WITHDRAW, "USDT BEP-20 Withdrawal ($%.2f USDT)".format(usd), realAmount, usd, etb, shortAddress(wallet), "Just now", TransactionStatus.PENDING, req.id)
    _uiState.update { it.copy(realBalance = it.realBalance - realAmount, pendingWithdrawals = listOf(req) + it.pendingWithdrawals, transactions = listOf(tx) + it.transactions, notificationMessage = "Withdrawal request submitted for Admin review.") }
    return true
  }

  fun submitKycVerification(fullName: String, dateOfBirth: String, nationality: String, residentialAddress: String, docType: String, docNumber: String, frontPhotoUri: String? = null, backPhotoUri: String? = null, selfiePhotoUri: String? = null): Boolean {
    val s = _uiState.value
    if (!s.userProfile.isLoggedIn) { showToast("Please register or sign in before submitting KYC."); return false }
    if (listOf(fullName, dateOfBirth, nationality, residentialAddress, docType, docNumber).any { it.isBlank() }) { showToast("Please complete all required KYC information."); return false }
    if (s.userProfile.kycStatus == KycStatus.PENDING_REVIEW) { showToast("Your KYC is already under review."); return false }
    val k = KycSubmission("KYC-${UUID.randomUUID().toString().take(8)}", fullName.trim(), s.userProfile.email, dateOfBirth.trim(), nationality.trim(), residentialAddress.trim(), docType.trim(), docNumber.trim(), !frontPhotoUri.isNullOrBlank(), !backPhotoUri.isNullOrBlank(), !selfiePhotoUri.isNullOrBlank(), frontPhotoUri, backPhotoUri, selfiePhotoUri, KycStatus.PENDING_REVIEW, SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date()))
    _uiState.update { it.copy(userProfile = it.userProfile.copy(kycStatus = KycStatus.PENDING_REVIEW, kycSubmission = k), kycSubmissions = listOf(k) + it.kycSubmissions, currentScreen = AppDestination.KYC_WAITING, notificationMessage = "KYC submitted successfully! Please wait for Admin review.") }
    return true
  }

  fun checkKycStatus() {
    when (_uiState.value.userProfile.kycStatus) {
      KycStatus.APPROVED -> navigateTo(AppDestination.MAIN)
      KycStatus.REJECTED -> _uiState.update { it.copy(currentScreen = AppDestination.KYC_SUBMISSION) }
      KycStatus.PENDING_REVIEW -> _uiState.update { it.copy(currentScreen = AppDestination.KYC_WAITING, notificationMessage = "Your verification is still under review by Admin.") }
      KycStatus.NOT_SUBMITTED -> _uiState.update { it.copy(currentScreen = AppDestination.KYC_SUBMISSION) }
    }
  }

  fun submitSupportTicket(category: String, subject: String, message: String, contactInfo: String): Boolean {
    if (category.isBlank() || subject.isBlank() || message.isBlank()) { showToast("Please enter ticket category, subject and detailed message."); return false }
    val s = _uiState.value
    val t = SupportTicket("TCK-${UUID.randomUUID().toString().take(8)}", s.userProfile.username.ifBlank { "GuestUser" }, contactInfo.trim().ifBlank { s.userProfile.email }, category.trim(), subject.trim(), message.trim(), status = TicketStatus.OPEN, createdAt = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date()))
    _uiState.update { it.copy(supportTickets = listOf(t) + it.supportTickets, notificationMessage = "Support ticket #${t.id} submitted!") }; return true
  }

  fun adminReplySupportTicket(ticketId: String, replyText: String, resolve: Boolean = false) {
    if (!adminRequired()) return
    if (replyText.isBlank()) { showToast("Please enter a reply message."); return }
    _uiState.update { s -> if (s.supportTickets.none { it.id == ticketId }) s.copy(notificationMessage = "Support ticket not found.") else s.copy(supportTickets = s.supportTickets.map { if (it.id == ticketId) it.copy(adminReply = replyText.trim(), repliedAt = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date()), status = if (resolve) TicketStatus.RESOLVED else TicketStatus.ANSWERED) else it }, notificationMessage = "Reply sent to user on Ticket #$ticketId.") }
  }

  fun submitKycUpgrade(fullName: String, docType: String, docNumber: String): Boolean {
    val k = _uiState.value.userProfile.kycSubmission
    return submitKycVerification(fullName, k?.dateOfBirth.orEmpty(), k?.nationality.orEmpty(), k?.residentialAddress.orEmpty(), docType, docNumber, k?.frontPhotoUri, k?.backPhotoUri, k?.selfiePhotoUri)
  }

  fun createMyAd(tradeType: P2PTradeType, cryptoAmount: Double, pricePerUnit: Double, minLimit: Double, maxLimit: Double, paymentMethods: List<String>): Boolean {
    val s = _uiState.value
    if (!s.userProfile.isLoggedIn || s.userProfile.kycStatus != KycStatus.APPROVED) { showToast("KYC approval is required before creating a P2P ad."); return false }
    if (!cryptoAmount.isFinite() || cryptoAmount <= 0 || !pricePerUnit.isFinite() || pricePerUnit <= 0 || !minLimit.isFinite() || !maxLimit.isFinite() || minLimit <= 0 || maxLimit < minLimit) { showToast("Please enter valid ad amounts and limits."); return false }
    if (paymentMethods.isEmpty()) { showToast("Please select at least one payment method."); return false }
    if (tradeType == P2PTradeType.SELL && cryptoAmount > s.realBalance) { showToast("Insufficient REAL balance for this sell ad."); return false }
    val a = P2PAd("my-ad-${UUID.randomUUID().toString().take(8)}", s.userProfile.username, tradeType, "REAL", "ETB", pricePerUnit, cryptoAmount, minLimit, maxLimit, 100.0, 0, paymentMethods.map { it.trim() }.filter { it.isNotBlank() }.distinct(), isUserAd = true, isActive = true)
    _uiState.update { it.copy(userAds = listOf(a) + it.userAds, p2pAds = listOf(a) + it.p2pAds, notificationMessage = "New ${tradeType.name} Ad published successfully at %.2f ETB!".format(pricePerUnit)) }; return true
  }

  fun toggleAdActive(adId: String) {
    _uiState.update { s -> val a = s.userAds.find { it.id == adId }; if (a == null) s.copy(notificationMessage = "You can only change your own ads.") else { val ua = s.userAds.map { if (it.id == adId) it.copy(isActive = !it.isActive) else it }; s.copy(userAds = ua, p2pAds = s.p2pAds.map { if (it.id == adId) it.copy(isActive = !it.isActive) else it }, notificationMessage = "Ad status updated.") } }
  }
  fun hasActiveEscrowOrders() = _uiState.value.activeEscrowOrders.any { it.status == EscrowStatus.PENDING_PAYMENT || it.status == EscrowStatus.PAID_PENDING_RELEASE || it.status == EscrowStatus.DISPUTED }

  fun deleteMyAd(adId: String): Boolean {
    val s = _uiState.value; if (s.userAds.none { it.id == adId }) { showToast("Your ad was not found."); return false }
    if (s.activeEscrowOrders.any { it.adId == adId && (it.status == EscrowStatus.PENDING_PAYMENT || it.status == EscrowStatus.PAID_PENDING_RELEASE || it.status == EscrowStatus.DISPUTED) }) { showToast("Cannot delete ad while it has an active escrow order."); return false }
    _uiState.update { it.copy(userAds = it.userAds.filterNot { a -> a.id == adId }, p2pAds = it.p2pAds.filterNot { a -> a.id == adId }, notificationMessage = "Ad deleted successfully.") }; return true
  }

  fun addPaymentAccount(bankName: String, accountNumber: String): Boolean {
    val b = bankName.trim(); val n = accountNumber.trim(); val s = _uiState.value
    if (b.isBlank() || n.length < 4) { showToast("Please enter a valid payment account."); return false }
    if (s.userProfile.paymentAccounts.any { it.bankName.equals(b, true) && it.accountNumber.equals(n, true) }) { showToast("That payment account is already added."); return false }
    val name = s.userProfile.kycSubmission?.fullName?.takeIf { it.isNotBlank() } ?: s.userProfile.username
    val a = com.example.data.model.UserPaymentAccount("pay-${UUID.randomUUID().toString().take(8)}", b, n, name, SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()))
    _uiState.update { it.copy(userProfile = it.userProfile.copy(paymentAccounts = it.userProfile.paymentAccounts + a), notificationMessage = "Payment account for $b added.") }; return true
  }
  fun deletePaymentAccount(accountId: String): Boolean {
    if (hasActiveEscrowOrders()) { showToast("Cannot delete payment account while you have an active escrow order."); return false }
    _uiState.update { s -> if (s.userProfile.paymentAccounts.none { it.id == accountId }) s.copy(notificationMessage = "Payment account not found.") else s.copy(userProfile = s.userProfile.copy(paymentAccounts = s.userProfile.paymentAccounts.filterNot { it.id == accountId }), notificationMessage = "Payment account deleted.") }; return true
  }

  fun createP2POrder(ad: P2PAd, cryptoAmount: Double, fiatAmount: Double, isTakerBuy: Boolean = (ad.tradeType == P2PTradeType.SELL)): EscrowOrder? {
    val s = _uiState.value
    if (!s.userProfile.isLoggedIn || s.userProfile.kycStatus != KycStatus.APPROVED) { showToast("KYC approval is required before trading."); return null }
    if (!ad.isActive || cryptoAmount <= 0 || fiatAmount <= 0 || cryptoAmount > ad.availableCrypto || fiatAmount < ad.minLimit || fiatAmount > ad.maxLimit) { showToast("Invalid or unavailable P2P order."); return null }
    val expected = cryptoAmount * ad.pricePerUnit; if (kotlin.math.abs(fiatAmount - expected) > maxOf(0.01, expected * 0.001)) { showToast("Fiat amount does not match the ad price."); return null }
    if (ad.isUserAd && ad.traderName.equals(s.userProfile.username, true)) { showToast("You cannot trade against your own ad."); return null }
    val type = if (isTakerBuy) P2PTradeType.BUY else P2PTradeType.SELL
    if (type == P2PTradeType.SELL && s.realBalance < cryptoAmount) { showToast("Insufficient REAL balance to lock in escrow."); return null }
    if (s.activeEscrowOrders.any { it.adId == ad.id && (it.status == EscrowStatus.PENDING_PAYMENT || it.status == EscrowStatus.PAID_PENDING_RELEASE || it.status == EscrowStatus.DISPUTED) }) { showToast("You already have an active escrow order for this ad."); return null }
    val id = "P2P-${UUID.randomUUID().toString().take(8)}"
    val o = EscrowOrder(id, ad.id, ad.traderName, type, cryptoAmount, fiatAmount, ad.fiatCurrency, ad.paymentMethods.firstOrNull() ?: "Telebirr", EscrowStatus.PENDING_PAYMENT, SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
    _uiState.update { c -> c.copy(realBalance = if (type == P2PTradeType.SELL) c.realBalance - cryptoAmount else c.realBalance, p2pAds = c.p2pAds.map { if (it.id == ad.id) it.copy(availableCrypto = it.availableCrypto - cryptoAmount) else it }, userAds = c.userAds.map { if (it.id == ad.id) it.copy(availableCrypto = it.availableCrypto - cryptoAmount) else it }, activeEscrowOrders = listOf(o) + c.activeEscrowOrders, notificationMessage = "Escrow locked! Order $id created with ${ad.traderName}.") }
    return o
  }

  fun markOrderAsPaid(orderId: String) {
    _uiState.update { s -> val o = s.activeEscrowOrders.find { it.orderId == orderId }; when { o == null -> s.copy(notificationMessage = "Escrow order not found."); o.type != P2PTradeType.BUY -> s.copy(notificationMessage = "Only the buyer can mark this order as paid."); o.status != EscrowStatus.PENDING_PAYMENT -> s.copy(notificationMessage = "This order cannot be marked as paid now."); else -> s.copy(activeEscrowOrders = s.activeEscrowOrders.map { if (it.orderId == orderId) it.copy(status = EscrowStatus.PAID_PENDING_RELEASE) else it }, notificationMessage = "Marked order $orderId as paid!") } }
  }
  fun raiseDispute(orderId: String, reason: String) { _uiState.update { s -> val o = s.activeEscrowOrders.find { it.orderId == orderId }; if (o == null) s.copy(notificationMessage = "Escrow order not found.") else if (o.status != EscrowStatus.PAID_PENDING_RELEASE) s.copy(notificationMessage = "This order is not ready for a dispute.") else s.copy(activeEscrowOrders = s.activeEscrowOrders.map { if (it.orderId == orderId) it.copy(status = EscrowStatus.DISPUTED, disputeReason = reason.trim().ifBlank { "Buyer claims payment sent, seller has not released." }) else it }, notificationMessage = "Dispute raised for Order $orderId!") } }

  fun releaseEscrowCoins(orderId: String) {
    val o = _uiState.value.activeEscrowOrders.find { it.orderId == orderId } ?: return
    if (o.status != EscrowStatus.PAID_PENDING_RELEASE) { showToast("This escrow cannot be released in its current state."); return }
    _uiState.update { s -> val buyer = o.type == P2PTradeType.BUY; val tx = TransactionItem("tx-${UUID.randomUUID().toString().take(8)}", if (buyer) TransactionType.P2P_BUY else TransactionType.P2P_SELL, "P2P ${if (buyer) "Buy" else "Sell"} Complete", o.cryptoAmount, o.cryptoAmount * s.realPriceUsd, o.cryptoAmount * s.realPriceEtb, o.traderName, "Just now", TransactionStatus.COMPLETED, "ESC-$orderId"); s.copy(activeEscrowOrders = s.activeEscrowOrders.map { if (it.orderId == orderId) it.copy(status = EscrowStatus.COMPLETED) else it }, realBalance = if (buyer) s.realBalance + o.cryptoAmount else s.realBalance, transactions = listOf(tx) + s.transactions, notificationMessage = "Escrow completed!") }
  }

  fun sendCrypto(recipientAddress: String, amount: Double, memo: String): Boolean {
    val s = _uiState.value
    if (!s.userProfile.isLoggedIn || !amount.isFinite() || amount <= 0 || amount > s.realBalance) { showToast("Insufficient REAL balance or invalid amount."); return false }
    if (!Regex("^0x[a-fA-F0-9]{40}$").matches(recipientAddress.trim())) { showToast("Please enter a valid wallet address."); return false }
    showToast("On-chain REAL transfers require the connected wallet service. No coins were deducted.")
    return false
  }

  fun claimDailyLevelReward(): Boolean {
    val p = _uiState.value.userProfile
    if (!p.isLoggedIn || p.kycStatus != KycStatus.APPROVED) { showToast("KYC approval is required before claiming rewards."); return false }
    val level = p.currentLevel
    if (level == com.example.data.model.UserLevel.NONE) { showToast("Deposit volume must reach $100 to unlock Starter Level daily rewards."); return false }
    val now = System.currentTimeMillis(); val last = p.lastDailyRewardClaimTime
    if (last != 0L && now - last < 24 * 60 * 60 * 1000L) { showToast("Daily level reward already claimed for today."); return false }
    val reward = level.dailyRewardReal
    if (!reward.isFinite() || reward <= 0) { showToast("This reward is not currently available."); return false }
    val tx = TransactionItem("tx-${UUID.randomUUID().toString().take(8)}", TransactionType.REWARD, "Daily ${level.title} Reward", reward, reward * _uiState.value.realPriceUsd, reward * _uiState.value.realPriceEtb, "RealCoin VIP Rewards", "Just now", TransactionStatus.COMPLETED, "LVL-${UUID.randomUUID().toString().take(8)}")
    _uiState.update { it.copy(realBalance = it.realBalance + reward, transactions = listOf(tx) + it.transactions, userProfile = it.userProfile.copy(lastDailyRewardClaimTime = now), notificationMessage = "Successfully claimed Daily ${level.title} reward of +%.0f REAL!".format(reward)) }; return true
  }

  fun spinWheel(isPaidSpin: Boolean, prizeAmount: Double, onFinished: (Double) -> Unit) {
    if (_uiState.value.isSpinning) return
    val s = _uiState.value
    if (!s.userProfile.isLoggedIn || s.userProfile.kycStatus != KycStatus.APPROVED) { showToast("KYC approval is required before using Lucky Spin."); return }
    if (prizeAmount !in setOf(0.0, 10.0, 25.0, 50.0, 100.0, 250.0)) { showToast("Invalid wheel prize."); return }
    val now = System.currentTimeMillis(); val free = s.userProfile.lastFreeSpinTime == 0L || now - s.userProfile.lastFreeSpinTime >= 24 * 60 * 60 * 1000L
    if (!isPaidSpin && !free) { showToast("Daily free spin already used! You can spin for 10 REAL coin."); return }
    if (isPaidSpin && s.realBalance < 10.0) { showToast("Insufficient REAL coin balance! 10 REAL required to play."); return }
    viewModelScope.launch {
      _uiState.update { it.copy(isSpinning = true, realBalance = it.realBalance - if (isPaidSpin) 10.0 else 0.0, lastSpinWonAmount = null) }
      delay(3200)
      val tx = if (prizeAmount > 0) TransactionItem("tx-${UUID.randomUUID().toString().take(8)}", TransactionType.REWARD, "Lucky Wheel Reward", prizeAmount, prizeAmount * _uiState.value.realPriceUsd, prizeAmount * _uiState.value.realPriceEtb, "Lucky Spin", "Just now", TransactionStatus.COMPLETED, "SPIN-WIN-${UUID.randomUUID().toString().take(6)}") else null
      _uiState.update { it.copy(isSpinning = false, realBalance = it.realBalance + prizeAmount, lastSpinWonAmount = prizeAmount, transactions = if (tx != null) listOf(tx) + it.transactions else it.transactions, userProfile = it.userProfile.copy(lastFreeSpinTime = if (!isPaidSpin) now else it.userProfile.lastFreeSpinTime, spinAvailable = !isPaidSpin), notificationMessage = if (prizeAmount > 0) "Congratulations! Won %.0f REAL coins!".format(prizeAmount) else "No REAL won this spin. Better luck next time!") }
      onFinished(prizeAmount)
    }
  }

  fun clearNotification() { _uiState.update { it.copy(notificationMessage = null) } }
  fun showToast(msg: String) { _uiState.update { it.copy(notificationMessage = msg) } }

  private fun shortAddress(value: String): String = if (value.length <= 12) value else "${value.take(6)}...${value.takeLast(4)}"
}
