package com.yosology.handylogger.formatter

import com.yosology.handylogger.HandyLogger

/**
 * A simple formatter
 */
class PlainLogFormatter : LogFormatter
{
    override fun format(
        level: HandyLogger.LogLevel,
        prefix: String,
        msg: StringBuilder,
        suffix: String
    )
    {
        msg.insert(0, "$prefix ").append(" $suffix")
    }
}