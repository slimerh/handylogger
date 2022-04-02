package com.yosology.handylogger.formatter

import com.yosology.handylogger.HandyLogger

/**
 * Formats the message body inside a text box to make it more noticeable than other logs.
 */
class BoxyLogFormatter : LogFormatter
{
    private companion object
    {
        const val TOP_LEFT_CORNER = "╭"
        const val MESSAGE_START = "│"
        const val BOTTOM_LEFT_CORNER = "╰"
        const val HORIZONTAL_LINE = "────────────────────────────────────────────────────"
        const val TOP_BORDER = "$TOP_LEFT_CORNER$HORIZONTAL_LINE${HORIZONTAL_LINE}╼"
        const val BOTTOM_BORDER = "$BOTTOM_LEFT_CORNER$HORIZONTAL_LINE${HORIZONTAL_LINE}╼"
        const val NEWLINE = '\n'
        const val SPACE = ' '
    }

    override fun format(
        level: HandyLogger.LogLevel,
        prefix: String,
        msg: StringBuilder,
        suffix: String
    )
    {
        val px = if (prefix.isNotBlank()) "$prefix " else prefix

        msg.insert(0, "$TOP_BORDER$NEWLINE$MESSAGE_START$SPACE$px")

        if (suffix.isNotBlank())
        {
            msg.append(" ", suffix)
        }
        msg.appendLine().append(BOTTOM_BORDER)
    }
}