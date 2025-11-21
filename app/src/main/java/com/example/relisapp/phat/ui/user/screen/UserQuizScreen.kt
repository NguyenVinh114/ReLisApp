package com.example.relisapp.phat.ui.user.screen

import android.media.MediaPlayer
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relisapp.phat.entity.model.AnswerResult
import com.example.relisapp.phat.entity.Choices
import com.example.relisapp.phat.entity.model.ChoiceState
import com.example.relisapp.phat.entity.model.QuestionWithChoices
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Replay5
import androidx.compose.material.icons.filled.Forward5
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserQuizScreen(
    lessonTitle: String?,
    questionsWithChoices: List<QuestionWithChoices>,
    isLoading: Boolean,
    audioPath: String?,
    score: Int,
    onBackClick: () -> Unit,
    quizResults: List<AnswerResult>,
    onSubmit: (selectedAnswers: Map<Int, String>) -> Unit
) {
    val selectedAnswers = remember { mutableStateMapOf<Int, String>() }
    var isSubmitted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = lessonTitle ?: "Quiz",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator()
                questionsWithChoices.isEmpty() -> EmptyState()
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        audioPath?.let {
                            item {
                                AudioPlayerFromAssets(
                                    fileName = it,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }
                        }
                        itemsIndexed(questionsWithChoices, key = { _, item -> item.question.questionId }) { index, item ->
                            val result = if (isSubmitted) {
                                quizResults.find { it.questionId == item.question.questionId }
                            } else {
                                null
                            }

                            QuestionItem(
                                questionIndex = index + 1,
                                questionWithChoices = item,
                                answer = selectedAnswers[item.question.questionId],
                                enabled = !isSubmitted,
                                result = result,
                                onAnswerChange = { qId, ans ->
                                    selectedAnswers[qId] = ans
                                }
                            )
                            if (index < questionsWithChoices.lastIndex) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Text(
        text = "Questions are being updated. Please check back later.",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 32.dp)
    )
}

@Composable
private fun QuestionItem(
    questionIndex: Int,
    questionWithChoices: QuestionWithChoices,
    answer: String?,
    enabled: Boolean,
    result: AnswerResult?,
    onAnswerChange: (questionId: Int, answer: String) -> Unit
) {
    val question = questionWithChoices.question
    val isFillBlank = question.questionType == "fill_in_the_blank"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp) // Thêm padding để nội dung không bị sát viền
    ) {
        Text(
            text = "Câu ${questionIndex}: ${question.questionText}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(16.dp))

        if (isFillBlank) {
            FillInTheBlank(
                answer = answer,
                enabled = enabled,
                result = result,
                questionId = question.questionId,
                onAnswerChange = onAnswerChange
            )
        } else {
            // ✅ Đã cập nhật lại logic của MultipleChoice như phiên bản gốc
            MultipleChoiceOriginalStyle(
                choices = questionWithChoices.choices,
                answer = answer,
                enabled = enabled,
                result = result,
                questionId = question.questionId,
                onAnswerChange = onAnswerChange
            )
        }
    }
}

@Composable
private fun FillInTheBlank(
    answer: String?,
    enabled: Boolean,
    result: AnswerResult?,
    questionId: Int,
    onAnswerChange: (questionId: Int, answer: String) -> Unit
) {
    val (icon, color) = when {
        result == null -> null to MaterialTheme.colorScheme.outline
        result.isCorrect -> Icons.Default.CheckCircle to Color(0xFF28A745)
        else -> Icons.Default.Error to MaterialTheme.colorScheme.error
    }
    val animatedColor by animateColorAsState(targetValue = color, animationSpec = tween(300), label = "FillBlankColor")

    OutlinedTextField(
        value = answer ?: "",
        onValueChange = { if (enabled) onAnswerChange(questionId, it) },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Your answer...") },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = animatedColor,
            unfocusedBorderColor = animatedColor.copy(alpha = 0.7f),
        ),
        trailingIcon = {
            if (icon != null && !enabled) {
                Icon(icon, contentDescription = null, tint = animatedColor)
            }
        }
    )
    if (result != null && !result.isCorrect) {
        Text(
            text = "Correct answer: ${result.correctAnswer}",
            color = Color(0xFF28A745),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

// ✅ COMPOSABLE VỚI PHONG CÁCH GỐC


@Composable
private fun MultipleChoiceOriginalStyle(
    choices: List<Choices>, // ✅ Sửa lại kiểu dữ liệu từ Choices -> Choice
    answer: String?,
    enabled: Boolean,
    result: AnswerResult?,
    questionId: Int,
    onAnswerChange: (questionId: Int, answer: String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        choices.forEach { choice ->
            val isSelected = answer == choice.choiceId.toString()

            val choiceState = if (result == null) {
                ChoiceState.NEUTRAL
            } else {
                val isThisChoiceCorrect = result.correctAnswer == choice.choiceId.toString()
                when {
                    isSelected && isThisChoiceCorrect -> ChoiceState.CORRECT
                    isSelected && !isThisChoiceCorrect -> ChoiceState.INCORRECT
                    !isSelected && isThisChoiceCorrect -> ChoiceState.SHOW_CORRECT_ANSWER
                    else -> ChoiceState.NEUTRAL
                }
            }

            // Lấy các thuộc tính giao diện (màu nền, màu nội dung, màu viền, icon)
            val (backgroundColor, contentColor, borderColor, icon) = getChoiceAppearanceWithBorder(choiceState, isSelected)

            // Animate các thay đổi màu sắc
            val animatedBackgroundColor by animateColorAsState(backgroundColor, tween(300), "bg-color")
            val animatedContentColor by animateColorAsState(contentColor, tween(300), "content-color")
            val animatedBorderColor by animateColorAsState(borderColor, tween(300), "border-color")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .clip(RoundedCornerShape(12.dp))
                    // ✅ ÁP DỤNG VIỀN Ở ĐÂY
                    .border(
                        // Viền dày hơn khi được chọn hoặc đã có kết quả để làm nổi bật
                        width = if (isSelected || choiceState != ChoiceState.NEUTRAL) 2.dp else 1.dp,
                        color = animatedBorderColor,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(animatedBackgroundColor) // Nền có thể có một lớp màu rất nhạt
                    .clickable(enabled = enabled) {
                        onAnswerChange(
                            questionId,
                            choice.choiceId.toString()
                        )
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = { if (enabled) onAnswerChange(questionId, choice.choiceId.toString()) },
                    enabled = enabled,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = animatedContentColor,
                        unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(Modifier.width(16.dp))

                Text(
                    text = choice.choiceText,
                    color = animatedContentColor,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )

                if (icon != null && !enabled) {
                    Spacer(Modifier.width(16.dp))
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = animatedContentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/**
 * Hàm helper để quản lý giao diện, trả về bộ 4 giá trị.
 * Trả về: Quadruple(Màu nền, Màu nội dung, Màu viền, Icon hiển thị)
 */
@Composable
private fun getChoiceAppearanceWithBorder(
    state: ChoiceState,
    isSelected: Boolean
): Quadruple<Color, Color, Color, ImageVector?> {
    val correctColor = Color(0xFF28A745)
    val incorrectColor = MaterialTheme.colorScheme.error

    return when (state) {
        // Sau khi nộp bài
        ChoiceState.CORRECT -> Quadruple(
            correctColor.copy(alpha = 0.08f), // Nền xanh rất nhạt
            correctColor,                      // Chữ/Icon màu xanh
            correctColor,                      // Viền màu xanh
            Icons.Default.Check
        )
        ChoiceState.INCORRECT -> Quadruple(
            incorrectColor.copy(alpha = 0.08f), // Nền đỏ rất nhạt
            incorrectColor,                       // Chữ/Icon màu đỏ
            incorrectColor,                       // Viền màu đỏ
            Icons.Default.Close
        )
        ChoiceState.SHOW_CORRECT_ANSWER -> Quadruple(
            Color.Transparent,                    // Không nền
            MaterialTheme.colorScheme.onSurface,  // Chữ màu bình thường
            correctColor,                         // Viền màu xanh để chỉ ra đáp án đúng
            Icons.Default.Check                   // Vẫn có icon để xác nhận
        )
        // Khi chưa nộp bài
        ChoiceState.NEUTRAL -> {
            val content = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            val border = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            val background = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else Color.Transparent
            Quadruple(background, content, border, null)
        }
    }
}

// Bạn cần thêm class Quadruple này vào file, hoặc dùng List/Array nếu muốn.
// Dùng data class giúp code dễ đọc hơn.
data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)


@Composable
fun AudioPlayerFromAssets(fileName: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableIntStateOf(0) }
    var duration by remember { mutableIntStateOf(0) }

    // Hàm seek an toàn
    val seekTo: (Int) -> Unit = { position ->
        val newPosition = position.coerceIn(0, duration)
        currentPosition = newPosition
        mediaPlayer?.seekTo(newPosition)
    }

    DisposableEffect(fileName) {
        val player = MediaPlayer().apply {
            try {
                context.assets.openFd(fileName).use { afd ->
                    setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    prepare()
                    duration = this.duration
                }
                setOnCompletionListener {
                    isPlaying = false
                    currentPosition = 0 // Quay về đầu khi kết thúc
                }
            } catch (e: Exception) {
                e.printStackTrace()
                duration = 0
            }
        }
        mediaPlayer = player

        onDispose {
            player.release()
            mediaPlayer = null
        }
    }

    // Coroutine để cập nhật vị trí hiện tại của thanh trượt
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isActive) {
                currentPosition = mediaPlayer?.currentPosition ?: 0
                delay(200L) // Cập nhật 5 lần mỗi giây
            }
        }
    }

    // --- GIAO DIỆN ĐÃ THIẾT KẾ LẠI ---
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Thanh Slider
        Slider(
            value = currentPosition.toFloat(),
            onValueChange = { newPosition ->
                // Chỉ cập nhật UI, không seek mediaPlayer ở đây để tránh giật
                currentPosition = newPosition.toInt()
            },
            onValueChangeFinished = {
                // Seek mediaPlayer khi người dùng đã kéo xong
                mediaPlayer?.seekTo(currentPosition)
            },
            valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        // Thời gian
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp), // Thêm padding để khớp với lề của slider
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(currentPosition),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatTime(duration),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(8.dp))

        // Các nút điều khiển
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nút Tua Lại 10s
            IconButton(onClick = { seekTo(currentPosition - 10000) }) {
                Icon(
                    imageVector = Icons.Filled.Replay5,
                    contentDescription = "Rewind 10 seconds",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Nút Play/Pause chính
            IconButton(
                onClick = {
                    mediaPlayer?.let { mp ->
                        if (mp.isPlaying) mp.pause() else mp.start()
                        isPlaying = !isPlaying
                    }
                },
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                AnimatedContent(
                    targetState = isPlaying,
                    transitionSpec = {
                        scaleIn(animationSpec = tween(220, 90)) togetherWith
                                scaleOut(animationSpec = tween(220))
                    },
                    label = "PlayPauseIcon"
                ) { playing ->
                    Icon(
                        imageVector = if (playing) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                        contentDescription = if (playing) "Pause" else "Play",
                        modifier = Modifier.fillMaxSize(),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // Nút Tua Tới 10s
            IconButton(onClick = { seekTo(currentPosition + 10000) }) {
                Icon(
                    imageVector = Icons.Filled.Forward5,
                    contentDescription = "Forward 10 seconds",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatTime(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}


@Composable
private fun QuizFooter(
    isSubmitted: Boolean,
    score: Int,
    totalQuestions: Int,
    isAllAnswered: Boolean,
    onSubmitClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnimatedContent(
                targetState = isSubmitted,
                label = "Footer Animation"
            ) { submitted ->
                if (submitted) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Your Score",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$score / $totalQuestions",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Button(
                        onClick = onSubmitClick,
                        enabled = isAllAnswered,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        val buttonText = if (isAllAnswered) "SUBMIT" else "Answer all questions"
                        Text(buttonText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
