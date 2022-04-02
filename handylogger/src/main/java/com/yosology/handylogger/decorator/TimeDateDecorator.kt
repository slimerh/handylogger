package com.yosology.handylogger.decorator

import java.text.SimpleDateFormat
import java.util.*

class TimeDateDecorator(decorator: LogDecorator? = null) :
    LogDecorator("", decorator)
{
    private val format = SimpleDateFormat("dd_HHmmss", Locale.ENGLISH)

    override fun manipulate(): String
    {
        return "[${format.format(Date())}]"
    }
}
