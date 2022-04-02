package com.yosology.handylogger.printer

import androidx.annotation.CallSuper
import com.yosology.handylogger.HandyLogger
import com.yosology.handylogger.HandyLogger.LogLevel.ERROR
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

open class FileLogPrinter(
    private val outputDirPath: String,
    private val extension: String = "txt"
) : LogPrinter
{
    private var fileWriter: Writer? = null
    private var fallbackLog = ConsoleLogPrinter()

    fun createLogfile(fileName: String): File?
    {
        val folder = File(outputDirPath)

        if (!folder.exists())
        {
            if (!folder.mkdirs())
                return null
        }

        var rolledCount = 0 // TODO To be rolled by size limit
        var logFile: File? = null

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

        while (true)
        {
            val filename = String.format(
                "${fileName}_${timeStamp}%s.${extension}",
                if (rolledCount > 0) "_$rolledCount" else ""
            )

            val newFile = File(folder, filename)

            if (newFile.exists())
            {
                rolledCount++
                continue
            }

            logFile = newFile
            break
        }

        return logFile
    }

    private var logFile: File? = null

    fun getLogFilePath() = logFile?.absolutePath ?: ""

    init
    {
        try
        {
            logFile = createLogfile("logs")
            fileWriter = BufferedWriter(FileWriter(logFile))
        }
        catch (e: Exception)
        {
            fallbackLog.print(ERROR, "failed to generate a log file: %$e")
        }
    }

    fun write(log: String)
    {
        try
        {
            fileWriter?.let {
                it.appendLine(log)
                it.flush()
            }
        }
        catch (e: IOException)
        {
            fallbackLog.print(ERROR, "failed to write log to file: %$e")
        }
    }

    override fun print(level: HandyLogger.LogLevel, log: String)
    {
        if (level == HandyLogger.LogLevel.DEBUG || level == HandyLogger.LogLevel.VERBOSE)
        {
            return
        }

        write(log)
    }

    /**
     * This will never be called for now.
     */
    @CallSuper
    override fun close()
    {
        try
        {
            fileWriter?.close()
            fileWriter = null
        }
        catch (e: Exception)
        {
            fallbackLog.print(ERROR, "failed to close a log file: %$e")
        }
    }
}
