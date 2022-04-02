package com.yosology.handylogger

import com.google.common.truth.Truth.assertThat
import com.yosology.handylogger.decorator.CategoryDecorator
import com.yosology.handylogger.decorator.SubCategoryDecorator
import com.yosology.handylogger.decorator.TagDecorator
import com.yosology.handylogger.formatter.PlainLogFormatter
import com.yosology.handylogger.printer.ConsoleLogPrinter
import org.junit.Test

class HandyLoggerTest
{
    @Test
    fun testLog()
    {
        val logger = HandyLogger.Builder()
            .setDefaultFormatter(PlainLogFormatter())
            .addDecorator(
                CategoryDecorator(
                    "Network",
                    SubCategoryDecorator("WebSocket")
                )
            )
            .addDecorator(
                TagDecorator(
                    "Session",
                    TagDecorator("Init")
                )
            )
            .addPrinter(ConsoleLogPrinter())
            .build()

        logger.log(HandyLogger.LogLevel.DEBUG, "Message")

        val buffer = logger.getBuffer()

        assertThat(buffer).isEqualTo("[Network][WebSocket] Message #Init#Session")
    }

    @Test
    fun testRemoveQuotesAroundObject()
    {
        val logger = HandyLogger.Builder().build()

        "{\"m\":-1,\"o\":\"{\\\"versionId\\\":\\\"68e8cf053dc069bc8c20ba6cb32946fad550d5ca\\\"}\"}".let { json ->
            val ret = logger.removeQuotesAroundObject(json)
            assertThat(ret).isEqualTo(
                """{"m":-1,"o":{"versionId":"68e8cf053dc069bc8c20ba6cb32946fad550d5ca"}}"""
            )
        }

        """{\"m\":-1,\"o\":\"{\\\"versionId\\\":\\\"68e8cf053dc069bc8c20ba6cb32946fad550d5ca\\\"}\"}""".let { json ->
            val ret = logger.removeQuotesAroundObject(json)
            assertThat(ret).isEqualTo(
                """{"m":-1,"o":{"versionId":"68e8cf053dc069bc8c20ba6cb32946fad550d5ca"}}"""
            )
        }

        """"{\"m\":-1,\"o\":\"{\\\"versionId\\\":\\\"68e8cf053dc069bc8c20ba6cb32946fad550d5ca\\\"}\"}"""".let { json ->
            val ret = logger.removeQuotesAroundObject(json)
            assertThat(ret).isEqualTo(
                """{"m":-1,"o":{"versionId":"68e8cf053dc069bc8c20ba6cb32946fad550d5ca"}}"""
            )
        }
    }

    @Test
    fun testFindContentType()
    {
        val logger = HandyLogger.Builder().build()
        logger.findContentType(""""{\"m\":3,\"i\":-1,\"n\":\"Event\"}"""").let {
            assertThat(it).isEqualTo(HandyLogger.ContentType.JSON)
        }
        logger.findContentType("""{"m":3,"i":-1,"n":"Event"}""").let {
            assertThat(it).isEqualTo(HandyLogger.ContentType.JSON)
        }
        logger.findContentType(
            """{
            "name":"John",
            "age":30,
            "cars":[ "Ford", "BMW", "Fiat" ]
            }"""
        ).let {
            assertThat(it).isEqualTo(HandyLogger.ContentType.JSON)
        }
        logger.findContentType("""[ "Ford", "BMW", "Fiat" ]""").let {
            assertThat(it).isEqualTo(HandyLogger.ContentType.JSON)
        }
    }
}