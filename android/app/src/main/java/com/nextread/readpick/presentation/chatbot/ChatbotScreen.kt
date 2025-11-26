package com.nextread.readpick.presentation.chatbot

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nextread.readpick.data.model.chatbot.ChatMessageDto
import com.nextread.readpick.data.model.chatbot.ChatSessionDto
import kotlinx.coroutines.launch

// 🎨 디자인 가이드 색상 정의
private val ColorAppBar = Color(0xFFECE6F0)      // 상단 앱바 & 하단 입력창 배경
private val ColorSidebarBg = Color(0xFFFBF4FF)   // 사이드바 배경
private val ColorSelectedSession = Color(0xFFEFD3FF) // 선택된 세션 강조
private val ColorSearchBar = Color(0xFFECE6F0)   // 사이드바 검색창

@Composable
fun ChatbotScreen(
    viewModel: ChatbotViewModel = hiltViewModel()
) {
    // 최신 상태 수집 방식 적용
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Drawer(사이드바) 제어 상태
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // 로딩 처리
    if (uiState is ChatbotUiState.Loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // 성공 상태일 때 데이터 언박싱
    val state = uiState as? ChatbotUiState.Success ?: return

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ChatDrawerSheet(
                sessions = state.sessions,
                currentSessionId = state.currentSessionId,
                onNewChatClick = {
                    viewModel.startNewChat()
                    scope.launch { drawerState.close() }
                },
                onSessionClick = { sessionId ->
                    viewModel.selectSession(sessionId)
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        ChatMainContent(
            messages = state.messages,
            isTyping = state.isAiTyping,
            onMenuClick = { scope.launch { drawerState.open() } },
            onSendMessage = { msg -> viewModel.sendMessage(msg) }
        )
    }
}

// ---------------------------------------------------------
// 1️⃣ 사이드바 (Drawer) UI
// ---------------------------------------------------------
@Composable
fun ChatDrawerSheet(
    sessions: List<ChatSessionDto>,
    currentSessionId: Long?,
    onNewChatClick: () -> Unit,
    onSessionClick: (Long) -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = ColorSidebarBg, // FBF4FF
        modifier = Modifier.width(300.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // 🔍 검색창 (ECE6F0)
            Surface(
                color = ColorSearchBar,
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("채팅 검색", color = Color.Gray, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ➕ 새 채팅 버튼
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNewChatClick() }
                    .padding(vertical = 12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(12.dp))
                Text("새 채팅", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))

            // 📜 이전 채팅 목록
            Text("이전 채팅", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(sessions) { session ->
                    val isSelected = session.sessionId == currentSessionId

                    // 세션 아이템
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) ColorSelectedSession else Color.Transparent) // EFD3FF
                            .clickable { onSessionClick(session.sessionId) }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = session.title.ifBlank { "새로운 대화" },
                            maxLines = 1,
                            fontSize = 14.sp,
                            color = Color.Black,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------
// 2️⃣ 메인 화면 UI
// ---------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatMainContent(
    messages: List<ChatMessageDto>,
    isTyping: Boolean,
    onMenuClick: () -> Unit,
    onSendMessage: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // 메시지 추가 시 스크롤 하단 이동
    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size + (if (isTyping) 0 else -1))
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            // 상단 앱바 (ECE6F0)
            CenterAlignedTopAppBar(
                title = { Text("Next Read", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "메뉴")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ColorAppBar
                ),
                modifier = Modifier.clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            )
        },
        bottomBar = {
            // 하단 입력창 (ECE6F0)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Surface(
                    color = ColorAppBar,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        // 투명한 TextField
                        TextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("책을 추천 받아보세요.", color = Color.Gray) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        IconButton(onClick = {
                            if (inputText.isNotBlank()) {
                                onSendMessage(inputText)
                                inputText = ""
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "전송", tint = Color.Black)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (messages.isEmpty()) {
                // 💬 메시지가 없을 때: 환영 문구
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "원하는 책이 있으신가요?\n무엇이든 물어 봐 주세요!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        lineHeight = 34.sp,
                        color = Color.Black
                    )
                }
            } else {
                // 💬 메시지 리스트
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(messages) { message ->
                        ChatBubble(message)
                    }

                    if (isTyping) {
                        item {
                            Text(
                                text = "AI가 답변을 쓰고 있습니다...",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------
// 3️⃣ 말풍선 UI (책 정보 포함 가능)
// ---------------------------------------------------------
@Composable
fun ChatBubble(message: ChatMessageDto) {
    val isUser = message.sender == "USER"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            // 사용자는 보라색 계열, AI는 회색 계열 (혹은 디자인에 맞게 수정 가능)
            color = if (isUser) ColorSelectedSession else Color(0xFFF5F5F5),
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 0.dp,
                bottomEnd = if (isUser) 0.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // 1. 텍스트 내용
                Text(
                    text = message.content,
                    color = Color.Black,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )

                // 2. 추천 도서가 있다면 표시 (API 응답 활용)
                if (message.books.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = Color.Gray.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))

                    message.books.forEach { book ->
                        Text(
                            text = "📖 ${book.title}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "- ${book.author}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}