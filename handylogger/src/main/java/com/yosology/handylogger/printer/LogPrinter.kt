package com.yosology.handylogger.printer

import com.yosology.handylogger.HandyLogger

interface LogPrinter
{
    val newlineSeparation: Boolean get() = false

    fun print(level: HandyLogger.LogLevel, log: String)
    {
    }

    fun close()
    {
    }
}

class ConsoleLogPrinter : LogPrinter
{
    override fun print(level: HandyLogger.LogLevel, msg: String)
    {
        val priority = level.name

        println("${priority[0]}: $msg")
    }
}