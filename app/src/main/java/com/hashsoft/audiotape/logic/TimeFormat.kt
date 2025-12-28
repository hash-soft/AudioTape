package com.hashsoft.audiotape.logic

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

class TimeFormat {
    companion object {
        fun formatMillis(milliseconds: Long): String {
            // ミリ秒からDurationオブジェクトを作成
            val duration = milliseconds.milliseconds

            // toComponentsを使って時、分、秒に分解し、フォーマットする
            return duration.toComponents { hours, minutes, seconds, _ ->
                val h = hours.toString().padStart(2, '0')
                val m = minutes.toString().padStart(2, '0')
                val s = seconds.toString().padStart(2, '0')
                if (hours > 0) "$h:$m:$s" else "$m:$s"
            }
        }

        fun formatDateTimeHm(milliseconds: Long, formatStyle: FormatStyle = FormatStyle.SHORT): String {
            val formatter = DateTimeFormatter.ofLocalizedDateTime(formatStyle)
                .withLocale(Locale.getDefault())
            val dateTime = Instant.ofEpochMilli(milliseconds)
                .atZone(ZoneId.systemDefault()) // タイムゾーンを指定
                .toLocalDateTime()
            return dateTime.format(formatter)
        }
    }
}