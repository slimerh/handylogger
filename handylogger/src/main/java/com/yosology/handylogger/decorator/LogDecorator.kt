package com.yosology.handylogger.decorator

import com.yosology.handylogger.formatter.LogFormatter

abstract class LogDecorator(
    protected val content: String = "",
    private val decorator: LogDecorator? = null,
    val type: Type = Type.PREFIX
)
{
    enum class Type
    { PREFIX, SUFFIX }

    /**
     * The decorator can have a formatter-specific decoration style.
     */
    var logFormatterAware: Class<LogFormatter>? = null

    fun decoratePrefix(msg: StringBuilder)
    {
        if (type == Type.PREFIX)
        {
            msg.append(manipulate())
        }

        decorator?.decoratePrefix(msg)
    }

    fun decorateSuffix(msg: StringBuilder)
    {
        decorator?.decorateSuffix(msg)

        if (type == Type.SUFFIX)
        {
            msg.append(manipulate())
        }
    }

    abstract fun manipulate(): String
}

