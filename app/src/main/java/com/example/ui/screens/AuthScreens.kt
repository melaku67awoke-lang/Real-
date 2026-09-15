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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GreenProfit
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.RealCoinViewModel

@Composable
fun RegisterScreen(
  viewModel: RealCoinViewModel,
) {
  var username by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var referralCode by remember { mutableStateOf("") }
  var isPasswordVisible by remember { mutableStateOf(false) }
  var isTermsAgreed by remember { mutableStateOf(true) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  LazyColumn(
    modifier =
      Modifier.fillMaxSize()
        .background(DarkBackground)
        .padding(horizontal = 24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    item {
      Spacer(modifier = Modifier.height(20.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        IconButton(onClick = { viewModel.navigateTo(AppDestination.LANDING) }) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Create Account",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary,
        )
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    item {
      Box(
        modifier =
          Modifier.size(56.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(GoldPrimary, GoldDark))),
        contentAlignment = Alignment.Center,
      ) {
        Icon(Icons.Default.Person, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(32.dp))
      }
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = "Join RealCoin Network",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
      )
      Text(
        text = "Register to trade P2P in Ethiopian Birr & earn USDT bonuses",
        fontSize = 12.sp,
        color = TextSecondary,
        textAlign = TextAlign.Center,
      )
      Spacer(modifier = Modifier.height(24.dp))
    }

    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          // Username
          OutlinedTextField(
            value = username,
            onValueChange = { username = it; errorMessage = null },
            label = { Text("Username") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GoldPrimary) },
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

          // Email
          OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorMessage = null },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = GoldPrimary) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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

          // Phone Number
          OutlinedTextField(
            value = phone,
            onValueChange = { phone = it; errorMessage = null },
            label = { Text("Phone Number (+251...)") },
            placeholder = { Text("+251 91 234 5678", color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GoldPrimary) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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

          // Password
          OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            label = { Text("Password (min 6 characters)") },
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

          Spacer(modifier = Modifier.height(12.dp))

          // Confirm Password
          OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it; errorMessage = null },
            label = { Text("Confirm Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GoldPrimary) },
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

          Spacer(modifier = Modifier.height(12.dp))

          // Referral Code
          OutlinedTextField(
            value = referralCode,
            onValueChange = { referralCode = it },
            label = { Text("Referral Code (Optional)") },
            placeholder = { Text("e.g. REAL2026", color = TextMuted) },
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

          // Terms Checkbox
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
          ) {
            Checkbox(
              checked = isTermsAgreed,
              onCheckedChange = { isTermsAgreed = it },
              colors = CheckboxDefaults.colors(checkedColor = GoldPrimary, checkmarkColor = DarkBackground),
            )
            Text(
              text = "I agree to RealCoin Terms of Service & Privacy Policy",
              fontSize = 11.sp,
              color = TextSecondary,
            )
          }

          if (errorMessage != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = errorMessage!!, color = Color.Red, fontSize = 12.sp)
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Register Button
          Button(
            onClick = {
              if (username.isBlank()) {
                errorMessage = "Please enter a username"
              } else if (email.isBlank() || !email.contains("@")) {
                errorMessage = "Please enter a valid email address"
              } else if (password.length < 6) {
                errorMessage = "Password must be at least 6 characters"
              } else if (password != confirmPassword) {
                errorMessage = "Passwords do not match"
              } else if (!isTermsAgreed) {
                errorMessage = "Please agree to the Terms of Service"
              } else {
                val success = viewModel.registerUser(username, email, phone, password, referralCode)
                if (!success) {
                  errorMessage = "Registration failed"
                }
              }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
            shape = RoundedCornerShape(12.dp),
          ) {
            Text("Register & Enter Wallet", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 15.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text("Already have an account?", color = TextSecondary, fontSize = 13.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Sign In",
          color = GoldPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          modifier = Modifier.clickable { viewModel.navigateTo(AppDestination.LOGIN) },
        )
      }

      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}

@Composable
fun LoginScreen(
  viewModel: RealCoinViewModel,
) {
  var identifier by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var isPasswordVisible by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  LazyColumn(
    modifier =
      Modifier.fillMaxSize()
        .background(DarkBackground)
        .padding(horizontal = 24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    item {
      Spacer(modifier = Modifier.height(24.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        IconButton(onClick = { viewModel.navigateTo(AppDestination.LANDING) }) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Sign In",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary,
        )
      }
      Spacer(modifier = Modifier.height(24.dp))
    }

    item {
      Box(
        modifier =
          Modifier.size(60.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(GoldPrimary, GoldDark))),
        contentAlignment = Alignment.Center,
      ) {
        Text("RC", color = DarkBackground, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
      }
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "Welcome Back to RealCoin",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
      )
      Text(
        text = "Enter your credentials to access your wallet and P2P trades",
        fontSize = 12.sp,
        color = TextSecondary,
        textAlign = TextAlign.Center,
      )
      Spacer(modifier = Modifier.height(28.dp))
    }

    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          OutlinedTextField(
            value = identifier,
            onValueChange = { identifier = it; errorMessage = null },
            label = { Text("Username or Email") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GoldPrimary) },
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

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            label = { Text("Password") },
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

          Spacer(modifier = Modifier.height(22.dp))

          Button(
            onClick = {
              if (identifier.isBlank() || password.isBlank()) {
                errorMessage = "Please enter both username/email and password"
              } else {
                val success = viewModel.loginUser(identifier, password)
                if (!success) {
                  errorMessage = "Invalid credentials"
                }
              }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
            shape = RoundedCornerShape(12.dp),
          ) {
            Text("Sign In", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 15.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text("Don't have an account?", color = TextSecondary, fontSize = 13.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Sign Up",
          color = GoldPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          modifier = Modifier.clickable { viewModel.navigateTo(AppDestination.REGISTER) },
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "🔐 Admin Portal Login →",
        color = TextMuted,
        fontSize = 12.sp,
        modifier =
          Modifier.clickable { viewModel.navigateTo(AppDestination.ADMIN_LOGIN) }.padding(vertical = 8.dp),
      )

      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}
