package com.hashsoft.audiotape.ui.resource

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.hashsoft.audiotape.R
import java.io.File


@Composable
fun displaySortOrderValue(index: Int): String {
    val labels = stringArrayResource(R.array.audio_list_sort_labels).toList()
    return labels.getOrElse(index) { stringResource(R.string.unknown_sort_order_label) }
}

@Composable
fun buildAnnotatedSettings(
    separator: String = " \u00b7 ",
    vararg elements: String?
): AnnotatedString {
    val nonNullElements = elements.filterNot { it.isNullOrEmpty() }

    return buildAnnotatedString {
        nonNullElements.forEachIndexed { index, element ->
            if (element == null) return@forEachIndexed
            append(element)

            if (index < nonNullElements.lastIndex) {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    append(separator)
                }
            }
        }
    }
}

@Composable
fun treeListToAnnotatedString(
    list: List<String>?,
    separator: String = " > ",
    default: String = ""
): AnnotatedString {
    val (srcList, srcSeparator) = if (list == null) {
        default.removePrefix(File.separator).split(File.separator) to File.separator
    } else {
        list to separator
    }

    return buildAnnotatedString {
        srcList.forEachIndexed { index, string ->
            if (index < srcList.lastIndex) {
                append(string + srcSeparator)
            } else {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(string)
                }
            }
        }
    }
}
