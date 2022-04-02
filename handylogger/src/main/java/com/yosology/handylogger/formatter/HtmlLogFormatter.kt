package com.yosology.handylogger.formatter

import com.yosology.handylogger.HandyLogger

class HtmlLogFormatter : LogFormatter
{
    override fun format(
        level: HandyLogger.LogLevel,
        prefix: String,
        msg: StringBuilder,
        suffix: String
    )
    {
        val levelClass = when (level)
        {
            HandyLogger.LogLevel.ERROR -> "err"
            HandyLogger.LogLevel.WARNING -> "warn"
            HandyLogger.LogLevel.INFO -> "info"
            HandyLogger.LogLevel.DEBUG -> "debug"
            HandyLogger.LogLevel.VERBOSE -> "verbose"
        }

        if (prefix.isNotBlank())
        {
            msg.insert(
                0,
                "<div class='unit ${levelClass}'><span class='prefix'>$prefix</span><span class='msg'>"
            )
        }
        else
        {
            msg.insert(
                0,
                "<div class='unit ${levelClass}'><span class='msg'>"
            )
        }

        msg.append("</span>") // close the span for the message body.

        if (suffix.isNotBlank())
        {
            msg.append("<span class='suffix'>$suffix</span>")
        }

        msg.append("</div>")
    }
}