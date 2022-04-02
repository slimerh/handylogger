package com.yosology.handylogger.decorator

/**
 * Prepends indentation before the message body.
 *
 * @param indentCount   The number of indentation to prepend.
 */
class IndentDecorator(private val indentCount: Int = 1, decorator: LogDecorator? = null) :
    LogDecorator("\t", decorator)
{
    override fun manipulate(): String
    {
        return content.repeat(indentCount)
    }
}
