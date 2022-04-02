package com.yosology.handylogger.decorator

import com.yosology.handylogger.formatter.HtmlLogFormatter


/**
 * The output will be like '[Category] Message'
 */
@Suppress("KDocUnresolvedReference")
class CategoryDecorator(categoryName: String, decorator: LogDecorator? = null) :
    LogDecorator(categoryName, decorator)
{
    override fun manipulate(): String
    {
        return if (logFormatterAware == HtmlLogFormatter::class.java)
        {
            "<span class='cat'>$content</span>"
        }
        else
        {
            return "[$content]"
        }
    }
}


typealias SubCategoryDecorator = CategoryDecorator