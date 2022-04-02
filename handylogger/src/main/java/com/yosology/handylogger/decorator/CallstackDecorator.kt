package com.yosology.handylogger.decorator


/**
 * A prefix decorator for debugging
 * Line number:Class:Method
 *
 * This will work only for the debug build.
 */
open class CallstackDecorator(asTag: Boolean = false, decorator: LogDecorator? = null) :
    LogDecorator(decorator = decorator, type = if (asTag) Type.SUFFIX else Type.PREFIX)
{
    override fun manipulate(): String
    {
        var index = -1
        var found = false
        // find the function that called the logger.
        for ((i, e) in Thread.currentThread().stackTrace.withIndex())
        {
            // There can be several consecutive names containing 'HandyLogger'.
            if (e.className.contains("HandyLogger"))
            {
                found = true
            }
            else
            {
                if (found)
                {
                    index = i
                    break
                }
            }
        }

        if (index < 0)
            return ""

        val element = Thread.currentThread().stackTrace[index]

        return if (type == Type.SUFFIX)
        {
            "\t⚑[${
                element.className.substringAfterLast('.')
            }.${element.methodName}:${element.lineNumber}]"
        }
        else
        {
            "[${
                element.className.substringAfterLast('.')
            }.${element.methodName}:${element.lineNumber}]"
        }
    }
}

class CallstackTagDecorator(decorator: LogDecorator? = null) : CallstackDecorator(true, decorator)