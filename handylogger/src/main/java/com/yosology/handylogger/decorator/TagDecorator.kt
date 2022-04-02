package com.yosology.handylogger.decorator

import com.yosology.handylogger.formatter.HtmlLogFormatter


/**
 * The output will be like 'Message #Tag'
 */
class TagDecorator(tagName: String, decorator: LogDecorator? = null) :
    LogDecorator(tagName, decorator, Type.SUFFIX)
{
    override fun manipulate(): String
    {
        return if (logFormatterAware == HtmlLogFormatter::class.java)
        {
            "<span class='tag'>$content</span>"
        }
        else
        {
            "#$content"
        }
    }
}
