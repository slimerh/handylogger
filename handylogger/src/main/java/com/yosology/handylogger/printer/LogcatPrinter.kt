package com.yosology.handylogger.printer

import android.util.Log
import com.yosology.handylogger.HandyLogger

/**
 * Log printer for the Android devices.
 * Note that this won't work for the unit test code. {@see ConsoleLogPrinter}
 */
class LogcatPrinter(private val tag: String) : LogPrinter
{
    private companion object
    {
        // This is to filter and show only the messages from HandyLogger.
        const val GREP_CHAR = "◖"
    }

    override val newlineSeparation: Boolean get() = true

    override fun print(level: HandyLogger.LogLevel, msg: String)
    {
        val priority = when (level)
        {
            HandyLogger.LogLevel.ERROR -> Log.ERROR
            HandyLogger.LogLevel.WARNING -> Log.WARN
            HandyLogger.LogLevel.INFO -> Log.INFO
            HandyLogger.LogLevel.DEBUG -> Log.DEBUG
            HandyLogger.LogLevel.VERBOSE -> Log.VERBOSE
        }

        // Printing '◖' doesn't mean to look prettier.
        // This is to filter and show only the messages from HandyLogger.
        Log.println(priority, tag, "$GREP_CHAR$msg")
    }
}
