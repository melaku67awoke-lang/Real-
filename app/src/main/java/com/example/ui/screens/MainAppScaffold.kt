package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.KycStatus
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.RealCoinViewModel

enum class NavigationScreen(val label: String, val icon: ImageVector) {
  WALLET("Wallet", Icons.Default.AccountBalanceWallet),
  P2P("P2P", Icons.Default.CurrencyExchange),
  SPIN("Spin", Icons.Default.Casino),
  SETTINGS("Settings", Icons.Default.Settings),
}

@Composable
fun MainAppScaffold(
  viewModel: RealCoinViewModel = viewModel(),
) {
  val uiState by viewModel.uiState.collectAsState()
  var currentTabIndex by remember { mutableIntStateOf(0) }
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(uiState.notificationMessage) {
    uiState.notificationMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearNotification()
    }
  }

  // Top-level navigation based on currentScreen state
  when (uiState.currentScreen) {
    AppDestination.LANDING -> {
      LandingScreen(viewModel = viewModel)
    }

    AppDestination.LOGIN -> {
      LoginScreen(viewModel = viewModel)
    }

    AppDestination.REGISTER -> {
      RegisterScreen(viewModel = viewModel)
    }

    AppDestination.ADMIN_LOGIN -> {
      AdminLoginScreen(
        viewModel = viewModel,
        onBack = { viewModel.navigateTo(AppDestination.LANDING) },
      )
    }

    AppDestination.ADMIN_PANEL -> {
      AdminPanelScreen(
        viewModel = viewModel,
        uiState = uiState,
        onExit = {
          if (uiState.userProfile.kycStatus == KycStatus.APPROVED) {
            viewModel.navigateTo(AppDestination.MAIN)
          } else if (uiState.userProfile.kycStatus == KycStatus.PENDING_REVIEW) {
            viewModel.navigateTo(AppDestination.KYC_WAITING)
          } else {
            viewModel.navigateTo(AppDestination.LANDING)
          }
        },
      )
    }

    AppDestination.KYC_SUBMISSION -> {
      KYCSubmissionScreen(
        viewModel = viewModel,
        uiState = uiState,
        onBack = {
          if (uiState.userProfile.kycStatus == KycStatus.APPROVED) {
            viewModel.navigateTo(AppDestination.MAIN)
          } else if (uiState.userProfile.kycStatus == KycStatus.PENDING_REVIEW) {
            viewModel.navigateTo(AppDestination.KYC_WAITING)
          } else {
            viewModel.navigateTo(AppDestination.LANDING)
          }
        },
      )
    }

    AppDestination.KYC_WAITING -> {
      KycWaitingScreen(
        viewModel = viewModel,
        uiState = uiState,
      )
    }

    AppDestination.HELP_CENTER -> {
      HelpCenterScreen(
        viewModel = viewModel,
        uiState = uiState,
        onBack = {
          if (uiState.userProfile.kycStatus == KycStatus.APPROVED) {
            viewModel.navigateTo(AppDestination.MAIN)
          } else if (uiState.userProfile.kycStatus == KycStatus.PENDING_REVIEW) {
            viewModel.navigateTo(AppDestination.KYC_WAITING)
          } else {
            viewModel.navigateTo(AppDestination.LANDING)
          }
        },
      )
    }

    AppDestination.MAIN -> {
      if (uiState.userProfile.kycStatus == KycStatus.PENDING_REVIEW) {
        KycWaitingScreen(
          viewModel = viewModel,
          uiState = uiState,
        )
      } else if (uiState.userProfile.kycStatus == KycStatus.REJECTED) {
        LandingScreen(viewModel = viewModel)
      } else {
        Scaffold(
          modifier = Modifier.fillMaxSize(),
          containerColor = DarkBackground,
          snackbarHost = { SnackbarHost(snackbarHostState) },
          bottomBar = {
            NavigationBar(
              containerColor = DarkSurface,
              modifier = Modifier.border(width = 1.dp, color = DarkCardBorder),
            ) {
              NavigationScreen.values().forEachIndexed { index, screen ->
                NavigationBarItem(
                  icon = { Icon(screen.icon, contentDescription = screen.label) },
                  label = { Text(screen.label) },
                  selected = currentTabIndex == index,
                  onClick = { currentTabIndex = index },
                  colors =
                    NavigationBarItemDefaults.colors(
                      selectedIconColor = DarkBackground,
                      selectedTextColor = GoldPrimary,
                      indicatorColor = GoldPrimary,
                      unselectedIconColor = TextMuted,
                      unselectedTextColor = TextMuted,
                    ),
                )
              }
            }
          },
        ) { paddingValues ->
          Box(
            modifier =
              Modifier.fillMaxSize()
                .background(DarkBackground)
                .padding(paddingValues),
          ) {
            when (currentTabIndex) {
              0 ->
                HomeScreen(
                  viewModel = viewModel,
                  uiState = uiState,
                  onNavigateToSpin = { currentTabIndex = 2 },
                  onNavigateToP2P = { currentTabIndex = 1 },
                  onNavigateToSettings = { currentTabIndex = 3 },
                )
              1 ->
                P2PScreen(
                  viewModel = viewModel,
                  uiState = uiState,
                )
              2 ->
                SpinWheelScreen(
                  viewModel = viewModel,
                  uiState = uiState,
                  onBack = { currentTabIndex = 0 },
                )
              3 ->
                SettingsScreen(
                  viewModel = viewModel,
                  uiState = uiState,
                )
            }
          }
        }
      }
    }
  }
}
