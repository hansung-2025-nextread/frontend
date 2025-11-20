package com.nextread.readpick.presentation.chatbot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * 챗봇 화면 UI (Compose)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen(
    viewModel: ChatbotViewModel = hiltViewModel()
) {
    // ViewModel의 UI 상태를 관찰
    val uiState by viewModel.uiState.collectAsState()

    // 사용자가 입력한 텍스트 상태
    var inputMessage by remember { mutableStateOf(TextFieldValue("")) }

    // 메인 화면 구조
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Next Read Chatbot") })
        },
        bottomBar = {
            // 메시지 입력창 컴포넌트
            MessageInput(
                inputMessage = inputMessage,
                onInputChanged = { inputMessage = it },
                onSendClicked = {
                    if (inputMessage.text.isNotBlank()) {
                        viewModel.sendMessage(inputMessage.text)
                        inputMessage = TextFieldValue("") // 메시지 전송 후 입력창 비우기
                    }
                },
                // 로딩 중이거나 메시지가 비어 있으면 버튼 비활성화
                isSendEnabled = inputMessage.text.isNotBlank() && uiState !is ChatbotUiState.Loading && (uiState as? ChatbotUiState.Success)?.isLoading != true
            )
        }
    ) { paddingValues ->
        // 상태에 따라 콘텐츠 렌더링
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                // 1. 로딩 상태 처리: 초기 데이터 로드 중일 때
                ChatbotUiState.Loading -> {
                    LoadingScreen()
                }

                // 2. 성공 상태 처리: 대화 이력 표시
                is ChatbotUiState.Success -> {
                    ChatContent(
                        messages = state.messages,
                        isLoading = state.isLoading
                    )
                }

                // 3. 에러 상태 처리: 오류 메시지 표시
                is ChatbotUiState.Error -> {
                    ErrorScreen(state.message)
                }
            }
        }
    }
}

/**
 * 로딩 상태일 때 표시되는 화면 (스피너)
 */
@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text("데이터를 불러오는 중...")
    }
}

/**
 * 에러 상태일 때 표시되는 화면
 */
@Composable
fun ErrorScreen(errorMessage: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "오류 발생: $errorMessage",
            color = MaterialTheme.colorScheme.error
        )
        // 재시도 버튼 등 추가 가능
    }
}

/**
 * 메시지 목록을 보여주는 부분
 */
@Composable
fun ChatContent(
    messages: List<ChatMessage>,
    isLoading: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        reverseLayout = true, // 최신 메시지가 아래로 오도록 역순 레이아웃 사용
        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
    ) {
        // 현재 챗봇 응답 대기 중이라면 로딩 인디케이터 표시 (가장 아래에)
        if (isLoading) {
            item {
                BotMessageBubble(message = ChatMessage(text = "...", isFromUser = false), isTyping = true)
            }
        }

        // 실제 메시지 목록 표시
        items(messages.reversed(), key = { it.id }) { message ->
            MessageBubble(message)
        }
    }
}

/**
 * 단일 메시지 버블 컴포넌트
 */
@Composable
fun MessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start // 사용자 메시지는 오른쪽 정렬
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isFromUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier.widthIn(max = 300.dp) // 최대 너비 제한
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(10.dp),
                color = if (message.isFromUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

/**
 * 챗봇 타이핑 표시용 버블 (ChatContent에서 isLoading일 때 사용)
 */
@Composable
fun BotMessageBubble(message: ChatMessage, isTyping: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            if (isTyping) {
                // 단순 텍스트 대신 로딩 인디케이터 사용 (선택 사항)
                LinearProgressIndicator(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(0.5f)
                )
            } else {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(10.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}


/**
 * 메시지 입력 및 전송 버튼
 */
@Composable
fun MessageInput(
    inputMessage: TextFieldValue,
    onInputChanged: (TextFieldValue) -> Unit,
    onSendClicked: () -> Unit,
    isSendEnabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = inputMessage,
            onValueChange = onInputChanged,
            label = { Text("메시지 입력") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { onSendClicked() }),
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onSendClicked,
            enabled = isSendEnabled,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.height(56.dp)
        ) {
            Icon(Icons.Filled.Send, contentDescription = "메시지 전송")
        }
    }
}