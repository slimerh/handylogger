package com.yoso.handylogger.printer

import android.util.Log
import com.google.common.truth.Truth.assertThat
import com.yoso.handylogger.HandyLogger.LogLevel.DEBUG
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

private const val LOG_TAG = "HtmlFileLogTest"

class HtmlFileLogTest {
    @get:Rule
    val folder = TemporaryFolder()

    @Test
    fun testHtmlFileLog() {
        val tmpDir = folder.newFolder("subpath")

        val filePrinter = HtmlFileLogPrinter(
            tmpDir.absolutePath,
            ".msg { color: #000000; } .error { color: #FF0000; }"
        )

        val logger = com.yoso.handylogger.HandyLogger.Builder()
            .addDecorator(com.yoso.handylogger.decorator.CategoryDecorator("Html"))
            .addDecorator(com.yoso.handylogger.decorator.TagDecorator("Test"))
            .addPrinter(LogcatPrinter(LOG_TAG)) // #1 just for checking in the logcat view
            .addPrinter(
                filePrinter,
                com.yoso.handylogger.formatter.HtmlLogFormatter()
            ) // #2 the testing target
            .build()

        logger.log(DEBUG, "Message")

        val buffer = logger.getBuffer()

        assertThat(buffer).isEqualTo("<div class='unit debug'><span class='prefix'><span class='cat'>Html</span></span><span class='msg'>Message</span><span class='suffix'><span class='tag'>Test</span></span></div>")

        val logfilePath = filePrinter.getLogFilePath()

        assertThat(logfilePath).startsWith(tmpDir.absolutePath)
        assertThat(logfilePath).endsWith(".html")

        // Only the logs of INFO, WARNING and ERROR levels are written!
        logger.log(com.yoso.handylogger.HandyLogger.LogLevel.INFO, "Message2")

        // The content is flushed. Test reading without closing it.
        val file: File = File(logfilePath)
        val fileContent = file.readText().replace("""\s""".toRegex(), "")

        Log.d(LOG_TAG, "------------------ FILE CONTENT ----------------------")
        Log.d(LOG_TAG, fileContent)

        val expected = """
            #<!DOCTYPE html><html>
            #<style>
            #.msg { color: #000000; } .error { color: #FF0000; }
            #</style>
            #<body>
            #<div class='unit info'><span class='prefix'><span class='cat'>Html</span></span><span class='msg'>Message2</span><span class='suffix'><span class='tag'>Test</span></span></div>
                """.trimMargin("#")

        assertThat(fileContent).isEqualTo(expected.replace("""\s""".toRegex(), ""))
    }

}