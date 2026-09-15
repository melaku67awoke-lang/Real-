package com.example.data.model

data class CryptoRate(
  val symbol: String,
  val name: String,
  val priceUsd: Double,
  val change24h: Double,
  val isPositive: Boolean = change24h >= 0,
)

enum class TransactionType {
  SEND,
  RECEIVE,
  DEPOSIT,
  WITHDRAW,
  P2P_BUY,
  P2P_SELL,
  REWARD,
}

enum class TransactionStatus {
  COMPLETED,
  PENDING,
  FAILED,
  REJECTED,
}

data class TransactionItem(
  val id: String,
  val type: TransactionType,
  val title: String,
  val amountReal: Double,
  val amountUsd: Double,
  val amountEtb: Double = 0.0,
  val counterparty: String,
  val timestamp: String,
  val status: TransactionStatus,
  val txHash: String,
)

enum class P2PTradeType {
  BUY,
  SELL,
}

data class P2PAd(
  val id: String,
  val traderName: String,
  val tradeType: P2PTradeType,
  val cryptoSymbol: String = "REAL",
  val fiatCurrency: String = "ETB",
  val pricePerUnit: Double, // Price in ETB
  val availableCrypto: Double,
  val minLimit: Double,
  val maxLimit: Double,
  val completionRate: Double = 99.0,
  val completedOrders: Int = 120,
  val paymentMethods: List<String>,
  val isOnline: Boolean = true,
  val isUserAd: Boolean = false,
  val isActive: Boolean = true,
)

enum class EscrowStatus {
  PENDING_PAYMENT,
  PAID_PENDING_RELEASE,
  COMPLETED,
  DISPUTED,
  CANCELLED,
}

data class EscrowOrder(
  val orderId: String,
  val adId: String,
  val traderName: String,
  val type: P2PTradeType,
  val cryptoAmount: Double,
  val fiatAmount: Double,
  val fiatCurrency: String = "ETB",
  val paymentMethod: String,
  val status: EscrowStatus,
  val createdAt: String,
  val disputeReason: String? = null,
  val expiresAtMinutes: Int = 15,
)

enum class KycTier(val tierLevel: Int, val title: String, val limitDescription: String) {
  TIER_1(1, "Basic Verification", "$500 USD / Day"),
  TIER_2(2, "Advanced Identity", "$25,000 USD / Day"),
  TIER_3(3, "Pro Merchant Tier", "Unlimited Volume"),
}

enum class KycStatus {
  NOT_SUBMITTED,
  PENDING_REVIEW,
  APPROVED,
  REJECTED,
}

data class KycSubmission(
  val id: String,
  val fullName: String,
  val email: String,
  val dateOfBirth: String,
  val nationality: String,
  val residentialAddress: String,
  val docType: String,
  val docNumber: String,
  val frontPhotoAttached: Boolean = true,
  val backPhotoAttached: Boolean = true,
  val selfiePhotoAttached: Boolean = true,
  val frontPhotoUri: String? = null,
  val backPhotoUri: String? = null,
  val selfiePhotoUri: String? = null,
  val status: KycStatus = KycStatus.PENDING_REVIEW,
  val rejectionReason: String? = null,
  val submittedAt: String,
)

data class DepositRequest(
  val id: String,
  val username: String,
  val amountUsd: Double,
  val realCoinAmount: Double,
  val bonusRealAmount: Double,
  val totalRealToCredit: Double,
  val network: String = "BEP-20",
  val depositAddress: String = "0x8e54105bed3243e1ca44a0cccc6b62cf2bff9df4",
  val txHash: String,
  val status: TransactionStatus = TransactionStatus.PENDING,
  val timestamp: String,
)

data class WithdrawRequest(
  val id: String,
  val username: String,
  val amountReal: Double,
  val amountUsd: Double,
  val amountEtb: Double,
  val method: String,
  val accountDetails: String,
  val status: TransactionStatus = TransactionStatus.PENDING,
  val timestamp: String,
)

enum class UserLevel(
  val title: String,
  val minDepositUsd: Double,
  val dailyRewardReal: Double,
  val badgeColorHex: Long,
) {
  NONE("Standard Member", 0.0, 0.0, 0xFF757575),
  STARTER("Starter Level", 100.0, 30.0, 0xFF4CAF50), // 100$ total volume -> 30 REAL/day
  SILVER("Silver Level", 150.0, 50.0, 0xFFB0BEC5),   // 150$ total volume -> 50 REAL/day
  BRONZE("Bronze Level", 200.0, 75.0, 0xFFCD7F32),   // 200$ total volume -> 75 REAL/day
  GOLD("Gold Level", 250.0, 100.0, 0xFFFFD700),      // 250$ total volume -> 100 REAL/day
  DIAMOND("Diamond Level", 300.0, 150.0, 0xFF00E5FF), // 300$ total volume -> 150 REAL/day
}

data class UserPaymentAccount(
  val id: String,
  val bankName: String,
  val accountNumber: String,
  val accountName: String,
  val addedAt: String,
)

data class UserProfile(
  val username: String = "RealTrader_88",
  val email: String = "trader@realcoin.network",
  val phone: String = "+251 91 234 5678",
  val walletAddress: String = "0x7F2a89B84bF19cE65e523B06f15E22B08B9fC71",
  val currentKycTier: KycTier = KycTier.TIER_1,
  val kycStatus: KycStatus = KycStatus.NOT_SUBMITTED,
  val kycSubmission: KycSubmission? = null,
  val totalOrders: Int = 0,
  val positiveRating: Double = 100.0,
  val spinAvailable: Boolean = true,
  val isLoggedIn: Boolean = false,
  val totalDepositVolumeUsd: Double = 0.0, // New users start at 0.0 deposit volume
  val lastFreeSpinTime: Long = 0L,
  val lastDailyRewardClaimTime: Long = 0L,
  val paymentAccounts: List<UserPaymentAccount> = emptyList(),
) {
  val currentLevel: UserLevel
    get() = when {
      totalDepositVolumeUsd >= 300.0 -> UserLevel.DIAMOND
      totalDepositVolumeUsd >= 250.0 -> UserLevel.GOLD
      totalDepositVolumeUsd >= 200.0 -> UserLevel.BRONZE
      totalDepositVolumeUsd >= 150.0 -> UserLevel.SILVER
      totalDepositVolumeUsd >= 100.0 -> UserLevel.STARTER
      else -> UserLevel.NONE
    }

  val nextLevel: UserLevel?
    get() = when (currentLevel) {
      UserLevel.NONE -> UserLevel.STARTER
      UserLevel.STARTER -> UserLevel.SILVER
      UserLevel.SILVER -> UserLevel.BRONZE
      UserLevel.BRONZE -> UserLevel.GOLD
      UserLevel.GOLD -> UserLevel.DIAMOND
      UserLevel.DIAMOND -> null
    }
}

object RealCoinConstants {
  const val REAL_PRICE_USD: Double = 0.0027
  const val USD_TO_ETB_RATE: Double = 186.0
  const val REAL_PRICE_ETB: Double = 5.0 // Today's market value: 0.0027 $ in ETB is 5 birr
  const val USDT_BEP20_DEPOSIT_ADDRESS: String = "0x8e54105bed3243e1ca44a0cccc6b62cf2bff9df4"
  const val MIN_DEPOSIT_USD: Double = 25.0
  const val MIN_WITHDRAW_USD: Double = 50.0 // Minimum withdrawal is strictly $50 USDT calculated at today's rate
  fun minWithdrawReal(realPriceUsd: Double): Double = if (realPriceUsd > 0) MIN_WITHDRAW_USD / realPriceUsd else 18518.52
  const val DEPOSIT_BONUS_PERCENTAGE: Double = 10.0
  const val ADMIN_DEFAULT_PASSWORD: String = "Admin@RealCoin2026"

  val ETHIOPIAN_PAYMENT_METHODS = listOf(
    "Telebirr",
    "CBE Birr",
    "CBE",
    "Bank of Abyssinia",
    "Dashen Bank",
    "Awash Bank",
  )
}

enum class TicketStatus {
  OPEN,
  ANSWERED,
  RESOLVED,
}

data class SupportTicket(
  val id: String,
  val username: String,
  val email: String,
  val category: String, // e.g. "Withdrawal (USDT BEP-20)", "Deposit", "KYC Verification", "P2P Escrow", "General Inquiry"
  val subject: String,
  val message: String,
  val adminReply: String? = null,
  val status: TicketStatus = TicketStatus.OPEN,
  val createdAt: String,
  val repliedAt: String? = null,
)
