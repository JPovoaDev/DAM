// FILE: ui/LoginScreen.kt
package com.example.splitexpenses.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.example.splitexpenses.viewmodel.LoginViewModel

@Composable
fun LoginScreenComp(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onLoginSuccess()
            viewModel.resetSuccess()
        }
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(96.dp),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 8.dp
        ) {
            Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = null,
                modifier = Modifier.padding(24.dp),
                tint = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        Text("Dividir", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text("Divide despesas, multiplica boas recordações.", textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = MaterialTheme.colorScheme.outline)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChanged(it) },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            isError = uiState.emailError != null,
            supportingText = { uiState.emailError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChanged(it) },
            label = { Text("Palavra-passe") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            isError = uiState.passwordError != null,
            supportingText = { uiState.passwordError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
        
        uiState.loginError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { viewModel.login() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Entrar com Email", fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = {
                coroutineScope.launch {
                    try {
                        val credentialManager = CredentialManager.create(context)
                        
                        // NOTA: Para usar GoogleIdOption de forma completa, precisa da dependency 
                        // com.google.android.libraries.identity.googleid:googleid:1.1.0 (ou similar)
                        // Para simplificar se não houver, vamos usar um web client id falso,
                        // mas idealmente colocas o web client ID real.
                        val webClientIdResId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
                        if (webClientIdResId == 0) {
                            viewModel.onGoogleLoginError("Google Sign-In não configurado (Falta Web Client ID no google-services.json). Certifica-te de que ativaste o Google Provider na Firebase e adicionaste o SHA-1.")
                            return@launch
                        }
                        val webClientId = context.getString(webClientIdResId)
                        
                        val googleIdOption = GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(webClientId)
                            .setAutoSelectEnabled(true)
                            .build()

                        val request = GetCredentialRequest.Builder()
                            .addCredentialOption(googleIdOption)
                            .build()

                        val result = credentialManager.getCredential(
                            request = request,
                            context = context
                        )

                        val credential = result.credential
                        if (credential is androidx.credentials.CustomCredential &&
                            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            
                            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                            val idToken = googleIdTokenCredential.idToken
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            
                            FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        viewModel.onGoogleLoginSuccess()
                                    } else {
                                        viewModel.onGoogleLoginError(task.exception?.message ?: "Erro no Google Login")
                                    }
                                }
                        } else {
                            viewModel.onGoogleLoginError("Credencial inválida")
                        }
                    } catch (e: Exception) {
                        viewModel.onGoogleLoginError(e.message ?: "Erro ao iniciar Google Login")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = !uiState.isLoading
        ) {
            Text("Continuar com Google")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Não tens conta?", color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "Regista-te agora",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}

