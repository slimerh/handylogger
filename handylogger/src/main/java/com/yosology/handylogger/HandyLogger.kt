package com.yosology.handylogger

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.yosology.handylogger.decorator.LogDecorator
import com.yosology.handylogger.formatter.LogFormatter
import com.yosology.handylogger.formatter.PlainLogFormatter
import com.yosology.handylogger.printer.LogPrinter
import org.jetbrains.annotations.TestOnly
import java.io.PrintWriter
import java.io.StringWriter
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private const val EXTREMELY_VERBOSE_LOG_DEFAULT_VALUE = true

class HandyLogger private constructor()
{
    companion object
    {
        // All the instances share the buffer to reduce the memory storage.

        private const val MAX_BUFFER_SIZE = 200
        private const val MAX_AFFIX_BUFFER_SIZE = 40

        /**
         * These all StringBuilder objects are used only in the locked 'log' function
         * (so don't bother with StringBuffer.)
         */
        private val buffer = StringBuilder(MAX_BUFFER_SIZE)
        private val prefixBuffer = StringBuilder(MAX_AFFIX_BUFFER_SIZE)
        private val suffixBuffer = StringBuilder(MAX_AFFIX_BUFFER_SIZE)

        private fun trimBuffers()
        {
            buffer.apply {
                if (capacity() > MAX_BUFFER_SIZE)
                {
                    setLength(MAX_BUFFER_SIZE)
                    trimToSize()
                }
            }

            arrayOf(prefixBuffer, suffixBuffer).forEach {
                it.apply {
                    if (capacity() > MAX_AFFIX_BUFFER_SIZE)
                    {
                        setLength(MAX_AFFIX_BUFFER_SIZE)
                        trimToSize()
                    }
                }
            }
        }

        private val lock = ReentrantLock()
    }

    enum class LogLevel
    {
        ERROR,
        WARNING,
        INFO,
        DEBUG,
        VERBOSE
    }

    private val printers = ArrayList<LogPrinter>()
    private var defaultFormatter: LogFormatter? = null
    private val printerAwareFormatters = HashMap<String, LogFormatter>()
    private val logDecorators = ArrayList<LogDecorator>()

    private var stripDebuggingLog: Boolean = false
    private var hasPrefixDecorator = false
    private var hasSuffixDecorator = false

    /**
     * super verbose mode
     */
    var extremelyVerbose = EXTREMELY_VERBOSE_LOG_DEFAULT_VALUE

    /**
     * Changeable log level
     */
    private var defaultLogLevel = LogLevel.DEBUG

    init
    {
        defaultFormatter = PlainLogFormatter()
    }

    private fun prettifyJson(json: String): String
    {
        return GsonBuilder()
            .setPrettyPrinting()
            .create()
            .toJson(JsonParser.parseString(json).asJsonObject)
    }

    internal fun removeQuotesAroundObject(json: String): String
    {
        return json
            .replace("""\\""".toRegex(), "")
            .replace(""""\{""".toRegex(), "{")
            .replace("""\}"""".toRegex(), "}")
    }

    /**
     * @param limit     if not 0, the message that is longer than this length will be ignored.
     */
    fun log(
        level: LogLevel,
        msg: String,
        suppressDecorator: Boolean = false,
        limit: Int = 0
    ): HandyLogger = lock.withLock {

        if (limit > 0 && msg.length > limit)
        {
            val msg = "The message longer than $limit is ignored."
            printers.forEach {
                it.print(level, msg)
            }
            return this
        }

        val beautified = tryBeautifyingJSON(msg)

        printers.forEach { printer ->
            // Is there a printer-specific formatter?
            val formatter = printerAwareFormatters[printer.javaClass.simpleName] ?: defaultFormatter

            buffer.clear()
            prefixBuffer.clear()
            suffixBuffer.clear()

            if (beautified != null)
            {
                buffer.appendLine()
                buffer.append(beautified)
            }
            else
            {
                buffer.append(msg)
            }

            if (!suppressDecorator)
            {
                logDecorators.forEach { decorator ->
                    decorator.logFormatterAware = formatter?.javaClass
                    decorator.decoratePrefix(prefixBuffer)
                }

                logDecorators.forEach { decorator ->
                    decorator.logFormatterAware = formatter?.javaClass
                    decorator.decorateSuffix(suffixBuffer)
                }
            }

            formatter?.format(
                level,
                prefixBuffer.toString(),
                buffer,
                suffixBuffer.toString()
            )

            if (printer.newlineSeparation)
            {
                buffer.split("\n".toRegex()).forEach {
                    printer.print(level, it)
                }
            }
            else
            {
                printer.print(level, buffer.toString())
            }
        }

        trimBuffers()

        return this
    }

    private fun tryBeautifyingJSON(msg: String): String?
    {
        var beautified: String? = null
        // Try beautifying the JSON string if the {@code msg} is only composed of JSON data.
        if (findContentType(msg) == ContentType.JSON)
        {
            try
            {
                beautified = prettifyJson(removeQuotesAroundObject(msg))
            }
            catch (e: Exception)
            {
                // not a Json format. ignore
            }
        }

        return beautified
    }

    @TestOnly
    fun getBuffer() = buffer.toString()

    fun log(msg: String)
    {
        log(defaultLogLevel, msg)
    }

    fun e(msg: String)
    {
        log(LogLevel.ERROR, msg)
    }

    fun e(throwable: Throwable)
    {
        e("", throwable)
    }

    fun e(msg: String, throwable: Throwable)
    {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        throwable.printStackTrace(pw)
        pw.flush()

        lock.withLock {
            log(LogLevel.ERROR, msg)

            log(LogLevel.ERROR, sw.toString(), suppressDecorator = true)
        }
    }

    /**
     * @param msg   if empty, the callstack info will be printed.
     */
    fun d(msg: String)
    {
        log(LogLevel.DEBUG, msg)
    }

    fun w(msg: String)
    {
        log(LogLevel.WARNING, msg)
    }

    fun w(throwable: Throwable)
    {
        log(LogLevel.WARNING, throwable.toString())
    }

    fun i(msg: String)
    {
        log(LogLevel.INFO, msg)
    }

    fun v(msg: String, limit: Int = 0)
    {
        log(LogLevel.VERBOSE, msg, limit = limit)
    }

    fun x(msg: String)
    {
        if (extremelyVerbose)
        {
            log(LogLevel.VERBOSE, msg)
        }
    }

    internal enum class ContentType
    { JSON, UNKNOWN }

    internal fun findContentType(msg: String): ContentType
    {
        return if ((msg.startsWith('{') && msg.endsWith('}')) ||
            (msg.startsWith('[') && msg.endsWith(']')) ||
            (msg.startsWith(""""{""") && msg.endsWith("""}""""))
        )
        {
            ContentType.JSON
        }
        else
        {
            ContentType.UNKNOWN
        }
    }

    class Builder
    {
        private val logger = HandyLogger()

        fun setDefaultFormatter(formatter: LogFormatter): Builder
        {
            logger.defaultFormatter = formatter
            return this
        }

        /**
         * You can use two kinds of ways of adding multiple decorators.
         *
         * e.g.)
         *  1. addDecorator(CategoryDecorator("Network", SubCategoryDecorator("WebSocket")))
         *  2. addDecorator(CategoryDecorator("Network").addDecorator(SubCategoryDecorator("WebSocket"))
         */
        fun addDecorator(decorator: LogDecorator): Builder
        {

            if (decorator.type == LogDecorator.Type.PREFIX)
            {
                logger.logDecorators.add(decorator)
                logger.hasPrefixDecorator = true
            }
            else
            {
                logger.logDecorators.add(0, decorator)
                logger.hasSuffixDecorator = true
            }

            return this
        }

        fun addPrinter(
            printer: LogPrinter,
            printerSpecificFormatter: LogFormatter? = null
        ): Builder
        {
            logger.printers.add(printer)

            if (printerSpecificFormatter != null)
            {
                logger.printerAwareFormatters[printer.javaClass.simpleName] =
                    printerSpecificFormatter
            }

            return this
        }

        fun setStripDebuggingLog(strip: Boolean): Builder
        {
            logger.stripDebuggingLog = strip
            return this
        }

        fun build(): HandyLogger
        {
            return logger
        }
    }
}