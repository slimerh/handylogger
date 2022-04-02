package com.yosology.handylogger.formatter

import com.yosology.handylogger.HandyLogger

interface LogFormatter
{
    fun format(
        level: HandyLogger.LogLevel,
        prefix: String,
        msg: StringBuilder,
        suffix: String
    )
}