package org.example.search.executor.land

import org.example.search.driver.IROSDriver
import org.example.search.executor.BaseSearchExecutor
import org.example.search.option.LandSearchOption
import org.openqa.selenium.By
import org.openqa.selenium.UnhandledAlertException
import org.openqa.selenium.support.ui.ExpectedConditions

abstract class LandBaseSearchExecutor(driver: IROSDriver, searchOption: LandSearchOption):
    BaseSearchExecutor(driver, searchOption) {

    protected val initPageHandle: String = driver.getWindowHandle()

    abstract override fun search(): List<String>

    override fun setInitConditionForSearch(): Boolean {

        driver.wait(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("inputFrame")))

        val currentTab = driver.findElementBy(By.className("tab_on")).getAttribute("id")

        if (searchOption.searchCategory.last() != currentTab?.last()) {
            driver.switchToWindow(initPageHandle)
            if(!driver.pageMove(searchOption))
                return false
        }

        return true
    }

    override fun applyDetailCondition(detailSearchOption: List<String>) {

        detailSearchOption.forEach { tagId ->
            driver.findElementSafely(By.id(tagId))?.let { elem ->
                if (elem.tagName == "input") {

                    val castedSearchOption = searchOption as LandSearchOption

                    val clickYn = when {
                        tagId.startsWith("y202cmort") -> castedSearchOption.gongdamFlag == "Y"
                        tagId.startsWith("y202trade") -> castedSearchOption.salesListFlag == "Y"
                        tagId == "cls_flag" -> castedSearchOption.closeFlag == "Y"
                        else -> false
                    }

                    if (clickYn && !elem.isSelected)
                        elem.click()
                }
            }
        }
    }

    override fun parseResult(): List<String> {

        try {

            driver.executeJavaScript("return f_search(this.form, 1, 0, 0);")
            driver.switchToWindow(initPageHandle)

            val waitResult = driver.waitForAll(listOf(
                ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("resultFrame")),
                ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("frmOuterModal")),
                ExpectedConditions.visibilityOfElementLocated(By.className("list_table")),
                ExpectedConditions.visibilityOfElementLocated(By.tagName("tr"))
            ))

            if (!waitResult)
                return listOf()

            driver.findElementBy(By.cssSelector(".list_table table:nth-of-type(2)")).let {
                return it.findElements(By.tagName("tr")).drop(1).map { tag -> tag.text }
            }

        } catch (e: UnhandledAlertException) {
            driver.acceptAlert()
            return listOf()
        } catch (e: Exception) {
            return listOf()
        } finally {
            driver.switchToWindow(initPageHandle)
        }
    }
}