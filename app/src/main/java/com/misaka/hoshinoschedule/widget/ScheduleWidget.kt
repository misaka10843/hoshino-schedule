package com.misaka.hoshinoschedule.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.misaka.hoshinoschedule.AppContainer
import com.misaka.hoshinoschedule.MainActivity
import com.misaka.hoshinoschedule.R
import com.misaka.hoshinoschedule.data.work.SchedulePlanner
import com.misaka.hoshinoschedule.util.minutesToTimeText
import com.misaka.hoshinoschedule.util.termStartDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ScheduleWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(
            DpSize(110.dp, 110.dp),
            DpSize(180.dp, 110.dp),
            DpSize(250.dp, 150.dp),
            DpSize(250.dp, 250.dp)
        )
    )

    @SuppressLint("RestrictedApi")
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val noUpcomingText = safeGetString(context, R.string.widget_no_upcoming) ?: "No upcoming classes"
        val unavailableText = safeGetString(context, R.string.widget_unavailable) ?: "Widget unavailable"

        val widgetState: WidgetState? = runCatching {
            withContext(Dispatchers.IO) {
                val container = AppContainer(context)

                val zone = runCatching { ZoneId.systemDefault() }.getOrDefault(ZoneId.of("UTC"))
                val today = runCatching { LocalDate.now(zone) }.getOrDefault(LocalDate.now())
                val now = runCatching { ZonedDateTime.now(zone) }.getOrDefault(ZonedDateTime.now())

                val courses = runCatching { container.courseRepository.observeAllCourses().first() }
                    .getOrDefault(emptyList())

                val periods = runCatching { container.periodRepository.observePeriods().first() }
                    .getOrDefault(emptyList())

                val preferences = runCatching { container.settingsRepository.preferences.first() }
                    .getOrNull()

                val backgroundColor = preferences?.let { prefs ->
                    runCatching {
                        if (prefs.backgroundMode.name == "COLOR") {
                            parseHexColor(prefs.backgroundValue)
                        } else null
                    }.getOrNull()
                }

                val termStart = runCatching { preferences?.termStartDate() }.getOrNull() ?: today
                val totalWeeks = runCatching { preferences?.totalWeeks ?: 20 }.getOrDefault(20)
                    .coerceIn(1, 52)
                val timetableName = runCatching { preferences?.timetableName?.takeIf { it.isNotBlank() } }
                    .getOrNull() ?: "Timetable"

                val planner = SchedulePlanner()

                val raw = planner.buildUpcomingClasses(
                    courses = courses,
                    periods = periods,
                    termStartDate = termStart,
                    totalWeeks = totalWeeks,
                    zoneId = zone,
                    startDate = today,
                    daysAhead = 0
                )

                val entries = raw.asSequence()
                    .filter { it.startDateTime.toLocalDate() == today }
                    .filter { !it.startDateTime.isBefore(now) }
                    .sortedBy { it.startDateTime }
                    .take(2)
                    .map { scheduled ->
                        val label = runCatching { scheduled.startDateTime.format(AM_PM_FORMAT) }
                            .getOrDefault("")

                        val timeSpan = runCatching {
                            val startMin = scheduled.startDateTime.hour * 60 + scheduled.startDateTime.minute
                            val endMin = scheduled.endDateTime.hour * 60 + scheduled.endDateTime.minute
                            "${minutesToTimeText(startMin)}-${minutesToTimeText(endMin)}"
                        }.getOrDefault("")

                        val subtitle = buildString {
                            append(timeSpan)
                            scheduled.course.teacher?.takeIf { it.isNotBlank() }?.let {
                                append(" ").append(BULLET_SEPARATOR).append(" ").append(it)
                            }
                            scheduled.course.location?.takeIf { it.isNotBlank() }?.let {
                                append(" ").append(BULLET_SEPARATOR).append(" ").append(it)
                            }
                        }

                        WidgetEntry(
                            label = label,
                            name = scheduled.course.name.ifBlank { "Untitled" },
                            subtitle = subtitle
                        )
                    }
                    .toList()

                WidgetState(
                    title = timetableName,
                    date = today.format(DATE_FORMAT),
                    dayOfWeek = today.format(DAY_OF_WEEK_FORMAT),
                    entries = entries,
                    backgroundColor = backgroundColor
                )
            }
        }.getOrNull()

        provideContent {
            val size = LocalSize.current
            val isCompact = size.width < 180.dp || size.height < 140.dp
            val backgroundColor = widgetState?.backgroundColor?.let { color ->
                ColorProvider(day = color.copy(alpha = 0.95f), night = color.copy(alpha = 0.85f))
            } ?: ColorProvider(MaterialThemeColors.widgetBackground)

            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .clickable(actionStartActivity<MainActivity>())
            ) {
                widgetState?.let { state ->
                    if (isCompact) {
                        CompactWidgetContent(
                            state = state,
                            noUpcomingText = noUpcomingText
                        )
                    } else {
                        ExpandedWidgetContent(
                            state = state,
                            noUpcomingText = noUpcomingText,
                            showTwoCards = size.height >= 200.dp
                        )
                    }
                } ?: run {
                    ErrorWidgetContent(unavailableText)
                }
            }
        }
    }

    companion object {
        private val DATE_FORMAT: DateTimeFormatter =
            DateTimeFormatter.ofPattern("MM-dd", Locale.getDefault())
        private val DAY_OF_WEEK_FORMAT: DateTimeFormatter =
            DateTimeFormatter.ofPattern("EEE", Locale.getDefault())
        private val AM_PM_FORMAT: DateTimeFormatter =
            DateTimeFormatter.ofPattern("a", Locale.getDefault())
        private const val BULLET_SEPARATOR: Char = '\u00B7'
    }
}

class ScheduleWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ScheduleWidget()
}

private object MaterialThemeColors {
    val widgetBackground: Color = Color(0xFF1C1B1F)
    val onWidget: Color = Color(0xFFF5F5F5)
    val onWidgetSecondary: Color = Color(0xFFB0B0B0)
    val primary: Color = Color(0xFF80CBC4)
    val cardBackground: Color = Color(0xFF2C2B30)
    val statusPillBackground: Color = Color(0xFF4CAF50)
    val statusPillText: Color = Color(0xFFFFFFFF)
}

private data class WidgetState(
    val title: String,
    val date: String,
    val dayOfWeek: String,
    val entries: List<WidgetEntry>,
    val backgroundColor: Color?
)

private data class WidgetEntry(
    val label: String,
    val name: String,
    val subtitle: String
)

@SuppressLint("RestrictedApi")
@Composable
private fun CompactWidgetContent(
    state: WidgetState,
    noUpcomingText: String
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(12.dp),
        verticalAlignment = Alignment.Vertical.Top
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = state.dayOfWeek,
                style = TextStyle(
                    color = ColorProvider(MaterialThemeColors.onWidget),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                modifier = GlanceModifier.defaultWeight()
            )
            Text(
                text = state.date,
                style = TextStyle(
                    color = ColorProvider(MaterialThemeColors.onWidgetSecondary),
                    fontSize = 12.sp
                )
            )
        }
        
        Spacer(modifier = GlanceModifier.height(8.dp))
        
        if (state.entries.isEmpty()) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                Text(
                    text = "✓",
                    style = TextStyle(
                        color = ColorProvider(MaterialThemeColors.primary),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = noUpcomingText,
                    style = TextStyle(
                        color = ColorProvider(MaterialThemeColors.onWidgetSecondary),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
        } else {
            val entry = state.entries.first()
            Column(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Box(
                        modifier = GlanceModifier
                            .background(ColorProvider(MaterialThemeColors.statusPillBackground))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Next",
                            style = TextStyle(
                                color = ColorProvider(MaterialThemeColors.statusPillText),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    Text(
                        text = entry.label,
                        style = TextStyle(
                            color = ColorProvider(MaterialThemeColors.primary),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = entry.name,
                    style = TextStyle(
                        color = ColorProvider(MaterialThemeColors.onWidget),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                )
                if (entry.subtitle.isNotBlank()) {
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Text(
                        text = entry.subtitle,
                        style = TextStyle(
                            color = ColorProvider(MaterialThemeColors.onWidgetSecondary),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun ExpandedWidgetContent(
    state: WidgetState,
    noUpcomingText: String,
    showTwoCards: Boolean
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = state.dayOfWeek,
                    style = TextStyle(
                        color = ColorProvider(MaterialThemeColors.onWidget),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                Text(
                    text = state.title,
                    style = TextStyle(
                        color = ColorProvider(MaterialThemeColors.onWidgetSecondary),
                        fontSize = 12.sp
                    )
                )
            }
            Box(
                modifier = GlanceModifier
                    .background(ColorProvider(MaterialThemeColors.cardBackground))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.date,
                    style = TextStyle(
                        color = ColorProvider(MaterialThemeColors.onWidget),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
        
        Spacer(modifier = GlanceModifier.height(12.dp))
        
        if (state.entries.isEmpty()) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                Text(
                    text = "✓",
                    style = TextStyle(
                        color = ColorProvider(MaterialThemeColors.primary),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = GlanceModifier.height(8.dp))
                Text(
                    text = noUpcomingText,
                    style = TextStyle(
                        color = ColorProvider(MaterialThemeColors.onWidgetSecondary),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
        } else {
            val entriesToShow = if (showTwoCards) state.entries.take(2) else state.entries.take(1)
            entriesToShow.forEachIndexed { index, entry ->
                ClassCard(entry = entry, isFirst = index == 0)
                if (index < entriesToShow.size - 1) {
                    Spacer(modifier = GlanceModifier.height(8.dp))
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun ClassCard(entry: WidgetEntry, isFirst: Boolean) {
    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(ColorProvider(MaterialThemeColors.cardBackground))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            if (isFirst) {
                Box(
                    modifier = GlanceModifier
                        .background(ColorProvider(MaterialThemeColors.statusPillBackground))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Next",
                        style = TextStyle(
                            color = ColorProvider(MaterialThemeColors.statusPillText),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = GlanceModifier.width(8.dp))
            }
            Text(
                text = entry.label,
                style = TextStyle(
                    color = ColorProvider(MaterialThemeColors.primary),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            )
        }
        Spacer(modifier = GlanceModifier.height(6.dp))
        Text(
            text = entry.name,
            style = TextStyle(
                color = ColorProvider(MaterialThemeColors.onWidget),
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        )
        if (entry.subtitle.isNotBlank()) {
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = entry.subtitle,
                style = TextStyle(
                    color = ColorProvider(MaterialThemeColors.onWidgetSecondary),
                    fontSize = 12.sp
                )
            )
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun ErrorWidgetContent(errorText: String) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(16.dp),
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
    ) {
        Text(
            text = "⚠",
            style = TextStyle(
                color = ColorProvider(MaterialThemeColors.onWidgetSecondary),
                fontSize = 28.sp
            )
        )
        Spacer(modifier = GlanceModifier.height(8.dp))
        Text(
            text = errorText,
            style = TextStyle(
                color = ColorProvider(MaterialThemeColors.onWidgetSecondary),
                fontStyle = FontStyle.Italic,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        )
    }
}

private fun parseHexColor(hex: String): Color? {
    return try {
        val cleanHex = hex.removePrefix("#")
        when (cleanHex.length) {
            6 -> Color(android.graphics.Color.parseColor("#$cleanHex"))
            8 -> Color(android.graphics.Color.parseColor("#$cleanHex"))
            else -> null
        }
    } catch (_: Exception) {
        null
    }
}

private fun safeGetString(context: Context, resId: Int): String? = try {
    context.getString(resId)
} catch (_: Throwable) {
    null
}
