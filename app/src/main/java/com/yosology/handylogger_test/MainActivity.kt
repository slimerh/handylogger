package com.yosology.handylogger_test

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.handylogger_test.BuildConfig
import com.example.handylogger_test.R
import com.yosology.handylogger.HandyLogger
import com.yosology.handylogger.decorator.CallstackTagDecorator
import com.yosology.handylogger.decorator.TimeDateDecorator
import com.yosology.handylogger.formatter.BoxyLogFormatter
import com.yosology.handylogger.printer.FileLogPrinter
import com.yosology.handylogger.printer.LogPrinter
import com.yosology.handylogger.printer.LogcatPrinter

private const val LOG_TAG = "MainActivity"

class MainActivity : AppCompatActivity()
{
    private var needsFileRecording = false
    private var filePrinter: LogPrinter? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val logcat = getCommonBuilder()
            .setDefaultFormatter(BoxyLogFormatter())
            .addPrinter(LogcatPrinter(LOG_TAG))
            .build()

        logcat.i("Your first log with Handylogger.")
    }

    fun setUp(context: Context)
    {
        if (needsFileRecording)
        {
            // Note that the debug and verbose logs are not written to file.
            filePrinter = FileLogPrinter("${context.cacheDir.absolutePath}/log/")
        }
    }

    fun getCommonBuilder(): HandyLogger.Builder
    {
        val builder = HandyLogger.Builder()

        if (BuildConfig.DEBUG)
        {
            builder.addDecorator(CallstackTagDecorator())
        }

        if (needsFileRecording)
        {
            builder.addDecorator(TimeDateDecorator())
            filePrinter?.let { builder.addPrinter(it) }
        }

        return builder
    }
}