package com.yosology.handylogger.printer


/**
 * @param cssData   The full CSS part that will reside between <style> and </style>
 */
class HtmlFileLogPrinter(outputDirPath: String, private val cssData: String) :
    FileLogPrinter(outputDirPath, "html")
{
    init
    {
        writeHeader()
    }

    private fun writeHeader()
    {
        write("<!DOCTYPE html><html>")
        write("<style>")
        write(cssData)
        write("</style>")
        write("<body>")
    }

    private fun writeFooter()
    {
        write("</body>\n</html>")
    }

    override fun close()
    {
        writeFooter()

        super.close()
    }
}