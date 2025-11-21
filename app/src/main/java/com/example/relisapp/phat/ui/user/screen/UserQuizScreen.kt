package com.example.relisapp.phat.ui.user.screen

import android.media.MediaPlayer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.relisapp.phat.entity.relations.QuestionWithChoices
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserQuizScreen(
    lessonTitle: String?,
    questionsWithChoices: List<QuestionWithChoices>,
    isLoading: Boolean,
    audioPath: String?,
    score: Int,
    onBackClick: () -> Unit,
    onSubmit: (selectedAnswers: Map<Int, String>) -> Unit
) {
    val selectedAnswers = remember { mutableStateMapOf<Int, String>() }
    var isSubmitted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(lessonTitle ?: "Quiz", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (questionsWithChoices.isEmpty()) {
                // ... (Logic hiển thị khi không có câu hỏi giữ nguyên)
                Text(
                    text = "Questions are being updated. Please check back later.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    audioPath?.let {
                        AudioPlayerFromAssets(fileName = it)
                    }
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(questionsWithChoices) { item ->
                            QuestionItem(
                                questionWithChoices = item,
                                answer = selectedAnswers[item.question.questionId],
                                enabled = !isSubmitted,
                                onAnswerChange = { qId, ans ->
                                    selectedAnswers[qId] = ans
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                    QuizFooter(
                        isSubmitted = isSubmitted,
                        score = score,
                        totalQuestions = questionsWithChoices.size,
                        isAllAnswered = selectedAnswers.size == questionsWithChoices.size,
                        onSubmitClick = {
                            onSubmit(selectedAnswers.toMap())
                            isSubmitted = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestionItem(
    questionWithChoices: QuestionWithChoices,
    answer: String?,
    enabled: Boolean,
    onAnswerChange: (questionId: Int, answer: String) -> Unit
) {
    val question = questionWithChoices.question
    val choices = questionWithChoices.choices
    val isFillBlank = question.questionType == "fill_in_the_blank"

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = question.questionText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(12.dp))

        if (isFillBlank) {
            OutlinedTextField(
                value = answer ?: "",
                onValueChange = { if (enabled) onAnswerChange(question.questionId, it) },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Your answer...") },
                singleLine = true
            )
        } else { // Mặc định là multiple_choice
            choices.forEach { choice ->
                val isSelected = answer == choice.choiceId.toString()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = enabled) { onAnswerChange(question.questionId, choice.choiceId.toString()) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onAnswerChange(question.questionId, choice.choiceId.toString()) },
                        enabled = enabled
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = choice.choiceText)
                }
            }
        }
    }
}

@Composable
fun AudioPlayerFromAssets(fileName: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0) }
    var duration by remember { mutableStateOf(0) }

    // ✅ SỬA LẠI: Quản lý vòng đời an toàn bằng DisposableEffect(key)
    DisposableEffect(fileName) {
        val player = MediaPlayer().apply {
            val afd = context.assets.openFd(fileName)
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            prepare()
            duration = this.duration
            setOnCompletionListener {
                isPlaying = false
                currentPosition = 0
            }
        }
        mediaPlayer = player

        // onDispose sẽ chạy khi Composable bị hủy HOẶC khi `fileName` thay đổi
        onDispose {
            player.release()
            mediaPlayer = null
        }
    }

    // ✅ SỬA LẠI: Dùng `isActive` để vòng lặp tự hủy
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isActive) {
                currentPosition = mediaPlayer?.currentPosition ?: 0
                delay(200L)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Slider(
            value = currentPosition.toFloat(),
            onValueChange = { newPosition ->
                currentPosition = newPosition.toInt()
                mediaPlayer?.seekTo(currentPosition)
            },
            valueRange = 0f..duration.toFloat().coerceAtLeast(0f),
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = formatTime(currentPosition), style = MaterialTheme.typography.bodySmall)
            Text(text = formatTime(duration), style = MaterialTheme.typography.bodySmall)
        }
        IconButton(onClick = {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) {
                    mp.pause()
                    isPlaying = false
                } else {
                    mp.start()
                    isPlaying = true
                }
            }
        }) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                contentDescription = if (isPlaying) "Pause" else "Play",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun formatTime(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

// ... QuizFooter giữ nguyên

@Composable
private fun QuizFooter(isSubmitted: Boolean, score: Int, totalQuestions: Int, isAllAnswered: Boolean, onSubmitClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (isSubmitted) {
            Text(text = "Your Score: $score / $totalQuestions", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        } else {
            Button(onClick = onSubmitClick, enabled = isAllAnswered, modifier = Modifier.fillMaxWidth()) {
                val buttonText = if (isAllAnswered) "SUBMIT" else "Answer all questions"
                Text(buttonText, fontWeight = FontWeight.Bold)
            }
        }
    }
}
