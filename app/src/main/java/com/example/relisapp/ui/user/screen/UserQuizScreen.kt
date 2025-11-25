package com.example.relisapp.ui.user.screen

import android.media.MediaPlayer
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relisapp.data.local.entity.Choices
import com.example.relisapp.data.local.entity.model.AnswerResult
import com.example.relisapp.data.local.entity.model.ChoiceState
import com.example.relisapp.data.local.entity.model.QuestionWithChoices
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

// --- MODEL COMMENT (Đặt ở đây hoặc file riêng) ---
data class UserComment(
    val id: Int,
    val userName: String,
    val content: String,
    val timestamp: String
)

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
    onSubmit: (selectedAnswers: Map<Int, String>) -> Unit,

    // --- THAM SỐ COMMENT ---
    comments: List<UserComment> = emptyList(),
    onAddComment: (String) -> Unit = {}
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
                },
                onBackToLessonList = { onBackClick() }
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
                        // 1. Audio Player
                        audioPath?.let {
                            item {
                                AudioPlayerFromAssets(
                                    fileName = it,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }
                        }

                        // 2. Danh sách câu hỏi
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

                        // --- 3. PHẦN COMMENT ---
                        if (isSubmitted) {
                            item {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 24.dp),
                                    thickness = 4.dp,
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                )
                                Text(
                                    text = "Comments (${comments.size})",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }

                            items(comments) { comment ->
                                CommentItem(comment = comment)
                            }

                            item {
                                CommentInputSection(onSend = onAddComment)
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- CÁC COMPOSABLE CON ---

@Composable
fun CommentItem(comment: UserComment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = if (comment.userName.isNotEmpty()) comment.userName.first().toString().uppercase() else "?",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(comment.userName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(comment.timestamp, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(comment.content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun CommentInputSection(onSend: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Leave a comment...") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = {
                if (text.isNotBlank()) {
                    onSend(text)
                    text = ""
                }
            },
            enabled = text.isNotBlank(),
            modifier = Modifier
                .size(48.dp)
                .background(
                    if (text.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                    CircleShape
                )
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
        }
    }
}

@Composable
private fun QuizFooter(
    isSubmitted: Boolean,
    score: Int,
    totalQuestions: Int,
    isAllAnswered: Boolean,
    onSubmitClick: () -> Unit,
    onBackToLessonList: () -> Unit
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
                        Spacer(modifier = Modifier.height(12.dp))

                        // Nút Back to Lessons
                        Button(
                            onClick = onBackToLessonList,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Back to Lessons", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
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

// ... (Giữ nguyên các hàm con khác: EmptyState, QuestionItem, FillInTheBlank, MultipleChoiceOriginalStyle, AudioPlayerFromAssets, getChoiceAppearanceWithBorder, Quadruple) ...
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
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Câu ${questionIndex}: ${question.questionText}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(16.dp))

        if (isFillBlank) {
            FillInTheBlank(answer, enabled, result, question.questionId, onAnswerChange)
        } else {
            MultipleChoiceOriginalStyle(questionWithChoices.choices, answer, enabled, result, question.questionId, onAnswerChange)
        }
    }
}

// (Để tiết kiệm dòng, bạn giữ nguyên các hàm FillInTheBlank, MultipleChoiceOriginalStyle... từ code cũ của bạn, chúng đã đúng)
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

@Composable
private fun MultipleChoiceOriginalStyle(
    choices: List<Choices>,
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
            val (backgroundColor, contentColor, borderColor, icon) = getChoiceAppearanceWithBorder(choiceState, isSelected)
            val animatedBackgroundColor by animateColorAsState(backgroundColor, tween(300), "bg-color")
            val animatedContentColor by animateColorAsState(contentColor, tween(300), "content-color")
            val animatedBorderColor by animateColorAsState(borderColor, tween(300), "border-color")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = if (isSelected || choiceState != ChoiceState.NEUTRAL) 2.dp else 1.dp,
                        color = animatedBorderColor,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(animatedBackgroundColor)
                    .clickable(enabled = enabled) {
                        onAnswerChange(questionId, choice.choiceId.toString())
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
                Text(text = choice.choiceText, color = animatedContentColor, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                if (icon != null && !enabled) {
                    Spacer(Modifier.width(16.dp))
                    Icon(imageVector = icon, contentDescription = null, tint = animatedContentColor, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
private fun getChoiceAppearanceWithBorder(
    state: ChoiceState,
    isSelected: Boolean
): Quadruple<Color, Color, Color, ImageVector?> {
    val correctColor = Color(0xFF28A745)
    val incorrectColor = MaterialTheme.colorScheme.error
    return when (state) {
        ChoiceState.CORRECT -> Quadruple(correctColor.copy(alpha = 0.08f), correctColor, correctColor, Icons.Default.Check)
        ChoiceState.INCORRECT -> Quadruple(incorrectColor.copy(alpha = 0.08f), incorrectColor, incorrectColor, Icons.Default.Close)
        ChoiceState.SHOW_CORRECT_ANSWER -> Quadruple(Color.Transparent, MaterialTheme.colorScheme.onSurface, correctColor, Icons.Default.Check)
        ChoiceState.NEUTRAL -> {
            val content = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            val border = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            val background = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else Color.Transparent
            Quadruple(background, content, border, null)
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun AudioPlayerFromAssets(fileName: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableIntStateOf(0) }
    var duration by remember { mutableIntStateOf(0) }

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
                setOnCompletionListener { isPlaying = false; currentPosition = 0 }
            } catch (e: Exception) { e.printStackTrace(); duration = 0 }
        }
        mediaPlayer = player
        onDispose { player.release(); mediaPlayer = null }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isActive) {
                currentPosition = mediaPlayer?.currentPosition ?: 0
                delay(200L)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Slider(
            value = currentPosition.toFloat(),
            onValueChange = { currentPosition = it.toInt() },
            onValueChangeFinished = { mediaPlayer?.seekTo(currentPosition) },
            valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
            modifier = Modifier.fillMaxWidth()
        )
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = formatTime(currentPosition), style = MaterialTheme.typography.bodySmall)
            Text(text = formatTime(duration), style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { seekTo(currentPosition - 10000) }) { Icon(Icons.Filled.Replay5, contentDescription = null, modifier = Modifier.size(32.dp)) }
            IconButton(
                onClick = { mediaPlayer?.let { if (it.isPlaying) it.pause() else it.start(); isPlaying = !isPlaying } },
                modifier = Modifier.size(64.dp).background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(if (isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle, contentDescription = null, tint = Color.White, modifier = Modifier.fillMaxSize())
            }
            IconButton(onClick = { seekTo(currentPosition + 10000) }) { Icon(Icons.Filled.Forward5, contentDescription = null, modifier = Modifier.size(32.dp)) }
        }
    }
}
private fun formatTime(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}