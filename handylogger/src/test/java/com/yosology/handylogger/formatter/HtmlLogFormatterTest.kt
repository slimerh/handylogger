package com.yosology.handylogger.formatter

import com.google.common.truth.Truth.assertThat
import com.yosology.handylogger.HandyLogger.LogLevel.DEBUG
import com.yosology.handylogger.HandyLogger.LogLevel.INFO
import org.junit.Test

class HtmlLogFormatterTest
{
    @Test
    fun format()
    {
        val formatter = HtmlLogFormatter()

        val message = StringBuilder("log message")

        formatter.format(DEBUG, "prefix", message, "suffix")
        assertThat(message.toString()).isEqualTo(
            """
            <div class='unit debug'><span class='prefix'>prefix</span><span class='msg'>log message</span><span class='suffix'>suffix</span></div>
            """.trimIndent()
        )

        message.clear()
        message.append("another log")

        formatter.format(INFO, "", message, "")
        assertThat(message.toString()).isEqualTo(
            """
            <div class='unit info'><span class='msg'>another log</span></div>
            """.trimIndent()
        )
    }
}