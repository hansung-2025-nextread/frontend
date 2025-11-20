import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState // 누락되어 추가했습니다.
import androidx.compose.foundation.shape.RoundedCornerShape // RoundedCornerShape 사용을 위해 추가
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel // ChatbotScreen에 ViewMode 연결이 필요하면 필요함
import kotlinx.coroutines.launch

// 요청하신 색상 정의
val LightGrayishWhite = Color(0xFFECE6F0) // ECE6F0
val PaleLavender = Color(0xFFFBF4FF)       // FBF4FF
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen() {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // 오른쪽 사이드바 (Drawer) 내용
            DrawerContent(
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                }
            )
        },
        gesturesEnabled = drawerState.isOpen // Drawer가 열려있을 때만 제스처 허용
    ) {
        // 메인 화면 (Scaffold) 내용
        Scaffold(
            topBar = {
                // 왼쪽 상단 TopAppBar 구현
                ChatTopAppBar(
                    onMenuClicked = {
                        scope.launch {
                            drawerState.open() // 햄버거 버튼 클릭 시 Drawer 열기
                        }
                    }
                )
            },
            bottomBar = {
                // 하단 입력 창 구현
                ChatInputBar()
            },
            containerColor = PaleLavender // 메인 화면 전체 배경색 (FBF4FF)
        ) { paddingValues ->
            // 중앙 콘텐츠 영역
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp), // 좌우 패딩 추가
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 중앙 메시지 텍스트
                Text(
                    text = "원하는 책이 있으신가요?\n무엇이든 물어 봐 주세요!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 40.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

// --- Composable Components ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopAppBar(onMenuClicked: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                "Next Read",
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
        },
        navigationIcon = {
            // 왼쪽 햄버거 아이콘 (메뉴 아이콘)
            IconButton(onClick = onMenuClicked) {
                Icon(
                    Icons.Filled.Menu,
                    contentDescription = "Menu",
                    tint = Color.DarkGray
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PaleLavender // AppBar 배경색 (FBF4FF)
        )
    )
}

@Composable
fun ChatInputBar() {
    // 1. 입력 텍스트 상태를 관리하기 위해 remember/mutableStateOf 사용
    var inputText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(PaleLavender), // 입력 바 영역 배경색
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = inputText, // ★★★ 실제 입력 상태 변수 사용
            onValueChange = { inputText = it }, // 입력이 변경될 때 상태 업데이트
            modifier = Modifier
                .weight(1f) // 남은 공간 모두 차지
                .heightIn(min = 56.dp, max = 56.dp),
            shape = RoundedCornerShape(12.dp),

            // ★★★ 여기를 Placeholder로 변경 ★★★
            placeholder = { Text("책을 추천 받아보세요.") },

            // OutlinedTextField 스타일
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                cursorColor = Color.Black
            ),
            trailingIcon = {
                // 오른쪽 반짝이 아이콘
                Box(modifier = Modifier.padding(end = 8.dp)) {
                    Text(
                        "✨",
                        fontSize = 20.sp
                    )
                }
            }
            // readOnly 속성은 입력 가능하도록 제거된 상태 유지
        )

        // 전송 버튼 (오른쪽 화살표)
        IconButton(
            onClick = {
                if (inputText.isNotBlank()) {
                    // TODO: 실제 메시지 전송 로직 구현
                    println("메시지 전송: $inputText")
                    inputText = "" // 전송 후 입력창 비우기
                }
            },
            modifier = Modifier.padding(start = 8.dp),
            enabled = inputText.isNotBlank() // 텍스트가 있을 때만 활성화
        ) {
            Icon(
                Icons.Filled.Send,
                contentDescription = "Send",
                tint = Color.DarkGray
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent(onCloseDrawer: () -> Unit) {
    // 1. 검색어 상태 관리를 위한 상태 변수 추가
    var searchText by remember { mutableStateOf("") }

    // 사이드바 내부 구성
    val previousChats = listOf(
        "인기 있는 SF 소설 추천해줘",
        "요즘 날씨에 어울리는 에세이나 소설 있을까?",
        "컴퓨터 공학부 3학년이 추천하는 책 추천해줘"
    )

    ModalDrawerSheet(
        modifier = Modifier.fillMaxWidth(0.8f), // 화면의 80% 정도 사용
        drawerContainerColor = LightGrayishWhite // 사이드바 배경색 (ECE6F0)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 상단 검색 바
            OutlinedTextField(
                value = searchText, // ★★★ 상태 변수 연결
                onValueChange = { searchText = it }, // ★★★ 입력 값으로 상태 업데이트
                placeholder = { Text("채팅 검색") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 새 채팅 버튼
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("✨", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "새 채팅",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.DarkGray
                )
            }

            // 이전 채팅 목록 제목
            Text(
                "이전 채팅",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            Divider(color = Color.Gray.copy(alpha = 0.3f))

            // 이전 채팅 목록
            LazyColumn {
                items(previousChats) { chat ->
                    Text(
                        text = chat,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // 채팅 항목 클릭 시 처리
                                onCloseDrawer() // 드로어 닫기
                                // 해당 채팅 내용 로드
                            }
                            .padding(vertical = 12.dp),
                        fontSize = 16.sp
                    )
                    Divider(color = Color.Gray.copy(alpha = 0.3f))
                }
                // 목록에 없는 나머지 항목을 "채팅 목록 예시..."로 채움
                items(3) {
                    Text(
                        text = "채팅 목록 예시...",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Divider(color = Color.Gray.copy(alpha = 0.3f))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatBotScreen() {
    ChatBotScreen()
}