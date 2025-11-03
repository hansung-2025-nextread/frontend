package com.nextread.readpick.presentation.auth.login

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

/**
 * 로그인 화면
 *
 * Google One Tap 로그인 구현
 *
 * 구조:
 * - UI: LoginScreen (이 파일)
 * - ViewModel: LoginViewModel (상태 관리 및 로그인 로직)
 * - Repository: AuthRepository (API 호출)
 *
 * 사용 예시 (MainActivity):
 * ```kotlin
 * LoginScreen(
 *     onLoginSuccess = { /* 홈 화면으로 이동 */ }
 * )
 * ```
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    // Credential Manager 초기화
    val credentialManager = remember { CredentialManager.create(context) }

    // 로그인 성공 시 화면 전환
    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            onLoginSuccess()
        }
    }

    // UI 렌더링
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 로고 & 타이틀
            Text(
                text = "📚 NextRead",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "AI 기반 도서 추천 서비스",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Google 로그인 버튼
            Button(
                onClick = {
                    scope.launch {
                        try {
                            // Google One Tap 로그인 시작
                            val googleIdOption = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId("947170076178-1ngihde1ku92danequokomnn9vcpo2te.apps.googleusercontent.com")
                                .setAutoSelectEnabled(true)
                                .build()

                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()

                            Log.d("LoginScreen", "Requesting credential from CredentialManager...")
                            val result = credentialManager.getCredential(
                                request = request,
                                context = context
                            )

                            Log.d("LoginScreen", "Credential received: ${result.credential}")
                            Log.d("LoginScreen", "Credential type: ${result.credential.type}")

                            // Google ID Token 추출
                            val credential = result.credential

                            // CustomCredential을 GoogleIdTokenCredential로 변환
                            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                            val idToken = googleIdTokenCredential.idToken

                            Log.d("LoginScreen", "✅ Google ID Token received: ${idToken.take(50)}...")

                            // ViewModel을 통해 로그인
                            viewModel.loginWithGoogle(idToken)
                        } catch (e: GetCredentialException) {
                            Log.e("LoginScreen", "Google Sign-In failed", e)
                            // UI에 에러 메시지 표시
                            viewModel.clearError() // 먼저 초기화
                            kotlinx.coroutines.delay(100)

                            // 에러 타입에 따른 상세 메시지
                            val errorMessage = when {
                                e.message?.contains("No credentials available") == true ->
                                    "Google 계정을 찾을 수 없습니다.\n\n해결 방법:\n1. Firebase Console에 SHA-1 등록\n2. 디바이스에 Google 계정 추가"
                                e.message?.contains("canceled") == true ->
                                    "로그인이 취소되었습니다"
                                else ->
                                    "로그인 실패: ${e.message}"
                            }

                            // 임시로 SnackBar 대신 Log로 출력 (추후 UI 개선)
                            Log.e("LoginScreen", "Error: $errorMessage")
                        } catch (e: Exception) {
                            Log.e("LoginScreen", "Unexpected error", e)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = uiState !is LoginUiState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (uiState is LoginUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🔐 Google로 로그인",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 에러 메시지
            if (uiState is LoginUiState.Error) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = (uiState as LoginUiState.Error).message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
