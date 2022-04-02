package com.yosology.handylogger.decorator

open class ThreadNameDecorator(decorator: LogDecorator? = null) :
    LogDecorator(decorator = decorator, type = Type.SUFFIX)
{
    override fun manipulate(): String
    {
        val threadName = Thread.currentThread().name
        return "\t\uD83E\uDDF5[$threadName]"
    }
}
