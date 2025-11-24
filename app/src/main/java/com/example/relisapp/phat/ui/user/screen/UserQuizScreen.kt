package com.example.relisapp.phat.ui.user.screen

import android.media.MediaPlayer
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relisapp.phat.entity.Choices
import com.example.relisapp.phat.entity.model.AnswerResult
import com.example.relisapp.phat.entity.model.ChoiceState
import com.example.relisapp.phat.entity.model.QuestionWithChoices
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun UserQuizScreen(
    modifier: Modifier = Modifier,
    // lessonTitle không còn cần thiết nếu bạn đã dùng BaseUserScreen
    // lessonTitle: String?,
    questionsWithChoices: List<QuestionWithChoices>,
    isLoading: Boolean,
    audioPath: String?,
    lessonContent: String?,
    score: Int,
    quizResults: List<AnswerResult>,
    onSubmit: (selectedAnswers: Map<Int, String>) -> Unit
) {
    val selectedAnswers = remember { mutableStateMapOf<Int, String>() }
    var isSubmitted by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            // [SỬA LẠI LOGIC Ở ĐÂY]
            // 1. Ưu tiên hiển thị màn hình loading.
            // 2. Sau khi không loading, mới kiểm tra có câu hỏi hay không.
            if (isLoading) {
                CircularProgressIndicator()
            } else if (questionsWithChoices.isEmpty()) {
                EmptyState()
            } else {
                // Nội dung LazyColumn giữ nguyên, không thay đổi
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
                    if (!lessonContent.isNullOrBlank()) {
                        item {
                            ExpandableLessonContent(
                                content = lessonContent,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                    itemsIndexed(questionsWithChoices, key = { _, item -> item.question.questionId }) { index, item ->
                        val result = if (isSubmitted) {
                            quizResults.find { it.questionId == item.question.questionId }
                        } else null

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

@Composable
private fun ExpandableLessonContent(
    content: String,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Lesson Content",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
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
            FillInTheBlank(
                answer = answer,
                enabled = enabled,
                result = result,
                questionId = question.questionId,
                onAnswerChange = onAnswerChange
            )
        } else {
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
            val isSelected = answer.equals(choice.choiceText, ignoreCase = true)

            val choiceState = if (result == null) {
                ChoiceState.NEUTRAL
            } else {
                val isThisChoiceCorrect = result.correctAnswer.equals(choice.choiceText, ignoreCase = true)
                val wasThisChoiceSelectedByUser = result.userAnswer.equals(choice.choiceText, ignoreCase = true)

                when {
                    wasThisChoiceSelectedByUser && isThisChoiceCorrect -> ChoiceState.CORRECT
                    wasThisChoiceSelectedByUser && !isThisChoiceCorrect -> ChoiceState.INCORRECT
                    !wasThisChoiceSelectedByUser && isThisChoiceCorrect -> ChoiceState.SHOW_CORRECT_ANSWER
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
                        onAnswerChange(questionId, choice.choiceText)
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = { if (enabled) onAnswerChange(questionId, choice.choiceText) },
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

@Composable
private fun getChoiceAppearanceWithBorder(
    state: ChoiceState,
    isSelected: Boolean
): Quadruple<Color, Color, Color, ImageVector?> {
    val correctColor = Color(0xFF28A745)
    val incorrectColor = MaterialTheme.colorScheme.error

    return when (state) {
        ChoiceState.CORRECT -> Quadruple(
            correctColor.copy(alpha = 0.08f),
            correctColor,
            correctColor,
            Icons.Default.Check
        )
        ChoiceState.INCORRECT -> Quadruple(
            incorrectColor.copy(alpha = 0.08f),
            incorrectColor,
            incorrectColor,
            Icons.Default.Close
        )
        ChoiceState.SHOW_CORRECT_ANSWER -> Quadruple(
            Color.Transparent,
            MaterialTheme.colorScheme.onSurface,
            correctColor,
            Icons.Default.Check
        )
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
                setOnCompletionListener {
                    isPlaying = false
                    currentPosition = 0
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

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isActive) {
                currentPosition = mediaPlayer?.currentPosition ?: 0
                delay(200L)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Slider(
            value = currentPosition.toFloat(),
            onValueChange = { newPosition ->
                currentPosition = newPosition.toInt()
            },
            onValueChangeFinished = {
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { seekTo(currentPosition - 10000) }) {
                Icon(
                    imageVector = Icons.Filled.Replay5,
                    contentDescription = "Rewind 10 seconds",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

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
