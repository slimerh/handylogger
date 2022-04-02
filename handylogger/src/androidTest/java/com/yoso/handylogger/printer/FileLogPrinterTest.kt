package com.yoso.handylogger.printer

import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class FileLogPrinterTest {

    @get:Rule
    val folder = TemporaryFolder()

    @Test
    fun getLogFile() {
        val tmpDir = folder.newFolder("subfolder")
        val printer = FileLogPrinter(tmpDir.absolutePath)
        val logFile = printer.createLogfile("logs")

        assertThat(logFile).isNotNull()
    }

    @Test
    fun print() {
        val tmpDir = folder.newFolder("subfolder")
        val printer = FileLogPrinter(tmpDir.absolutePath)

        val testContent = "longest journey"

        printer.write(testContent)

        val logfilePath = printer.getLogFilePath()

        assertThat(logfilePath).startsWith(tmpDir.absolutePath)
        assertThat(logfilePath).endsWith(".txt")

        // The content is flushed. Test reading without closing it.
        val file = File(logfilePath)
        val content = file.readText()

        assertThat(content).isEqualTo(testContent + "\n")
    }

    private fun getFileFromPath(obj: Any, fileName: String): File {
        val classLoader = obj.javaClass.classLoader
        val resource = classLoader.getResource(fileName)
        return File(resource.path)
    }

}