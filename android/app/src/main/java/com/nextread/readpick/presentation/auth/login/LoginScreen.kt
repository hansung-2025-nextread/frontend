package com.nextread.readpick.presentation.auth.login

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
 * @param onLoginSuccess 로그인 성공 시 실행할 콜백
 * - needsOnboarding: 온보딩 필요 여부 (true=온보딩 필요, false=홈으로 이동)
 * - isAdmin: 관리자 여부 (true=관리자 대시보드로 이동)
 */
@Composable
fun LoginScreen(
    onLoginSuccess: (needsOnboarding: Boolean, isAdmin: Boolean) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    // Credential Manager 초기화
    val credentialManager = remember { CredentialManager.create(context) }

    // 로그인 성공 시 화면 전환 (LaunchedEffect로 상태 관찰)
    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            val success = uiState as LoginUiState.Success
            onLoginSuccess(success.needsOnboarding, success.isAdmin)
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

                            // Google ID Token 추출
                            val credential = result.credential
                            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                            val idToken = googleIdTokenCredential.idToken

                            Log.d("LoginScreen", "✅ Google ID Token received")

                            // ViewModel을 통해 로그인 요청
                            viewModel.loginWithGoogle(idToken)

                        } catch (e: GetCredentialException) {
                            Log.e("LoginScreen", "Google Sign-In failed", e)

                            // 1. 먼저 에러 초기화
                            viewModel.clearError()

                            // 2. 에러 메시지 생성
                            val errorMessage = when {
                                e.message?.contains("No credentials available") == true ->
                                    "Google 계정을 찾을 수 없습니다.\n\n해결 방법:\n1. Firebase Console에 SHA-1 등록\n2. 디바이스에 Google 계정 추가"
                                e.message?.contains("canceled") == true ->
                                    "로그인이 취소되었습니다"
                                else ->
                                    "로그인 실패: ${e.message}"
                            }

                            // 3. 🚨 ViewModel에 에러 전달 -> UI 상태 업데이트
                            // (LoginViewModel에 setLoginError 함수가 없으면 추가하거나,
                            //  _uiState.value = LoginUiState.Error(errorMessage) 처리를 해야 함)
                            // 여기서는 ViewModel에 적절한 함수가 있다고 가정하고 호출하는 패턴입니다.
                            // 만약 LoginViewModel에 함수가 없다면 아래와 같이 직접 호출해야 할 수도 있습니다.
                            // viewModel.setLoginError(errorMessage)

                            // 임시로 로그 출력 (ViewModel에 에러 설정 함수가 없다면 이 로그만 보임)
                            Log.e("LoginScreen", "UI Error: $errorMessage")

                        } catch (e: Exception) {
                            Log.e("LoginScreen", "Unexpected error", e)
                            // viewModel.setLoginError("알 수 없는 오류: ${e.message}")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                // 로딩 중이면 버튼 비활성화
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

            // 🚨 에러 메시지 카드 (ViewModel 상태가 Error일 때만 표시)
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