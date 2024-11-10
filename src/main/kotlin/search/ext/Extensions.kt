package org.example.search.ext

import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

fun String.join(vararg strs: String): String = strs.joinToString(separator = this)

fun Map<String, String>.toJsonString() =
    this.entries.joinToString(separator = ", ", prefix = "{", postfix = "}") { "\"${it.key}\": \"${it.value}\""}

fun WebDriver.safeFindElement(selector: By): WebElement? = try {
    this.findElement(selector)
} catch (e: NoSuchElementException) {
    null
} catch (e: Exception) {
    null
}
