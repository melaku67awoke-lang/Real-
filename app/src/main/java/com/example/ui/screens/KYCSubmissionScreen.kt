package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.KycStatus
import com.example.data.model.KycTier
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
fun KYCSubmissionScreen(
  viewModel: RealCoinViewModel,
  uiState: RealCoinUiState,
  onBack: () -> Unit,
) {
  var fullName by remember { mutableStateOf("") }
  var dateOfBirth by remember { mutableStateOf("1996-08-24") }
  var nationality by remember { mutableStateOf("Ethiopian") }
  var residentialAddress by remember { mutableStateOf("") }
  var selectedDocType by remember { mutableStateOf("National ID (Fayda)") }
  var docNumber by remember { mutableStateOf("") }

  LaunchedEffect(uiState.userProfile.kycStatus) {
    if (uiState.userProfile.kycStatus == KycStatus.PENDING_REVIEW) {
      viewModel.navigateTo(AppDestination.KYC_WAITING)
    } else if (uiState.userProfile.kycStatus == KycStatus.APPROVED) {
      viewModel.navigateTo(AppDestination.MAIN)
    }
  }

  var frontPhotoUri by remember { mutableStateOf<String?>(null) }
  var backPhotoUri by remember { mutableStateOf<String?>(null) }
  var selfiePhotoUri by remember { mutableStateOf<String?>(null) }
  var formError by remember { mutableStateOf<String?>(null) }

  val frontPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      frontPhotoUri = uri.toString()
      formError = null
    }
  }

  val backPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      backPhotoUri = uri.toString()
      formError = null
    }
  }

  val selfiePicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      selfiePhotoUri = uri.toString()
      formError = null
    }
  }

  val docTypes = listOf("National ID (Fayda)", "Passport", "Kebele ID", "Driver's License")

  LazyColumn(
    modifier =
      Modifier.fillMaxSize()
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
          text = "Identity Verification (KYC)",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary,
        )
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Live KYC Status Banner
    item {
      val (statusColor, statusText, statusIcon) =
        when (uiState.userProfile.kycStatus) {
          KycStatus.APPROVED ->
            Triple(GreenProfit, "VERIFIED: Pro Tier 3 (Unlimited Volume)", Icons.Default.VerifiedUser)
          KycStatus.PENDING_REVIEW ->
            Triple(GoldPrimary, "UNDER REVIEW: Admin verification in progress", Icons.Default.HourglassEmpty)
          KycStatus.REJECTED ->
            Triple(RedLoss, "VERIFICATION FAILED: Please re-submit valid ID", Icons.Default.Error)
          KycStatus.NOT_SUBMITTED ->
            Triple(TextSecondary, "UNVERIFIED: Basic Tier 1 ($500 USD limit)", Icons.Default.Security)
        }

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f)),
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Box(
            modifier =
              Modifier.size(44.dp)
                .clip(CircleShape)
                .background(statusColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
          ) {
            Icon(statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(24.dp))
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "Account Verification Status",
              color = TextMuted,
              fontSize = 11.sp,
            )
            Text(
              text = statusText,
              color = statusColor,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(20.dp))
    }

    // Verification Form Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text(
            text = "Submit KYC Documents",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
          )
          Text(
            text = "Required for P2P limits above 25,000 USD / Day and zero-fee trading",
            fontSize = 12.sp,
            color = TextSecondary,
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Full Name
          OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it; formError = null },
            label = { Text("Full Legal Name (as on ID)") },
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

          // Date of Birth & Nationality Row
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = dateOfBirth,
              onValueChange = { dateOfBirth = it },
              label = { Text("Date of Birth") },
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
              value = nationality,
              onValueChange = { nationality = it },
              label = { Text("Nationality") },
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

          Spacer(modifier = Modifier.height(12.dp))

          // Residential Address
          OutlinedTextField(
            value = residentialAddress,
            onValueChange = { residentialAddress = it },
            label = { Text("Residential Address (City, Subcity, Woreda)") },
            placeholder = { Text("e.g. Addis Ababa, Bole, Woreda 03", color = TextMuted) },
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

          Spacer(modifier = Modifier.height(16.dp))

          Text("Document Type", fontSize = 13.sp, color = TextSecondary)
          Spacer(modifier = Modifier.height(8.dp))

          // Document Type Chips
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            docTypes.take(2).forEach { doc ->
              DocChip(
                title = doc,
                isSelected = selectedDocType == doc,
                onClick = { selectedDocType = doc },
                modifier = Modifier.weight(1f),
              )
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            docTypes.drop(2).forEach { doc ->
              DocChip(
                title = doc,
                isSelected = selectedDocType == doc,
                onClick = { selectedDocType = doc },
                modifier = Modifier.weight(1f),
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Document Number
          OutlinedTextField(
            value = docNumber,
            onValueChange = { docNumber = it; formError = null },
            label = { Text("Document ID Number") },
            placeholder = { Text("e.g. ET-ID-982144", color = TextMuted) },
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

          Spacer(modifier = Modifier.height(18.dp))

          Text("Upload ID Documents & Live Selfie", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
          Spacer(modifier = Modifier.height(10.dp))

          // Front Upload Box
          UploadBox(
            title = "Front Side of $selectedDocType",
            imageUri = frontPhotoUri,
            onPickFromGallery = {
              frontPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            onClear = { frontPhotoUri = null },
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Back Upload Box
          UploadBox(
            title = "Back Side of $selectedDocType (Optional)",
            imageUri = backPhotoUri,
            onPickFromGallery = {
              backPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            onClear = { backPhotoUri = null },
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Live Selfie Box
          UploadBox(
            title = "Live Selfie holding ID & 'RealCoin'",
            imageUri = selfiePhotoUri,
            onPickFromGallery = {
              selfiePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            onClear = { selfiePhotoUri = null },
          )

          if (formError != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = formError!!, color = Color.Red, fontSize = 12.sp)
          }

          Spacer(modifier = Modifier.height(22.dp))

          Button(
            onClick = {
              if (fullName.isBlank()) {
                formError = "Please enter your full legal name"
              } else if (docNumber.isBlank()) {
                formError = "Please enter your document ID number"
              } else if (frontPhotoUri == null) {
                formError = "Please select the front photo of your ID from your gallery"
              } else if (selfiePhotoUri == null) {
                formError = "Please select your selfie photo from your gallery"
              } else {
                val success =
                  viewModel.submitKycVerification(
                    fullName = fullName,
                    dateOfBirth = dateOfBirth,
                    nationality = nationality,
                    residentialAddress = residentialAddress,
                    docType = selectedDocType,
                    docNumber = docNumber,
                    frontPhotoUri = frontPhotoUri,
                    backPhotoUri = backPhotoUri,
                    selfiePhotoUri = selfiePhotoUri,
                  )
                if (success) {
                  onBack()
                }
              }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
            shape = RoundedCornerShape(12.dp),
          ) {
            Text("Submit Verification for Review", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 15.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}

@Composable
fun DocChip(
  title: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier =
      modifier
        .clip(RoundedCornerShape(10.dp))
        .background(if (isSelected) GoldPrimary.copy(alpha = 0.2f) else DarkCard)
        .border(
          width = 1.dp,
          color = if (isSelected) GoldPrimary else DarkCardBorder,
          shape = RoundedCornerShape(10.dp),
        )
        .clickable { onClick() }
        .padding(vertical = 10.dp, horizontal = 8.dp),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = title,
      color = if (isSelected) GoldPrimary else TextSecondary,
      fontSize = 11.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
      maxLines = 1,
    )
  }
}

@Composable
fun UploadBox(
  title: String,
  imageUri: String?,
  onPickFromGallery: () -> Unit,
  onClear: () -> Unit,
) {
  val hasImage = imageUri != null

  Box(
    modifier =
      Modifier.fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(DarkCard)
        .border(
          1.dp,
          if (hasImage) GreenProfit else DarkCardBorder,
          RoundedCornerShape(14.dp),
        )
        .clickable { onPickFromGallery() }
        .padding(12.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        if (hasImage) {
          Box(
            modifier =
              Modifier.size(54.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(DarkSurface)
                .border(1.5.dp, GreenProfit, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
          ) {
            AsyncImage(
              model = imageUri,
              contentDescription = title,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize(),
            )
          }
        } else {
          Box(
            modifier =
              Modifier.size(46.dp)
                .clip(CircleShape)
                .background(GoldPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              Icons.Default.AddPhotoAlternate,
              contentDescription = null,
              tint = GoldPrimary,
              modifier = Modifier.size(22.dp),
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(text = title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
          Spacer(modifier = Modifier.height(2.dp))
          if (hasImage) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenProfit, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "ID Photo Selected from Gallery",
                color = GreenProfit,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
              )
            }
          } else {
            Text(
              text = "Tap to choose photo from your Gallery",
              color = TextMuted,
              fontSize = 11.sp,
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (hasImage) GreenProfit.copy(alpha = 0.15f) else GoldPrimary.copy(alpha = 0.15f),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            if (hasImage) Icons.Default.CheckCircle else Icons.Default.PhotoLibrary,
            contentDescription = null,
            tint = if (hasImage) GreenProfit else GoldPrimary,
            modifier = Modifier.size(14.dp),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (hasImage) "Change" else "Choose",
            color = if (hasImage) GreenProfit else GoldPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
          )
        }
      }
    }
  }
}
