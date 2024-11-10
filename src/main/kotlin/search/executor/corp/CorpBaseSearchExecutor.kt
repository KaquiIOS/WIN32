package org.example.search.executor.corp

import org.example.search.driver.IROSDriver
import org.example.search.executor.BaseSearchExecutor
import org.example.search.option.CorpSearchOption
import org.openqa.selenium.By
import org.openqa.selenium.UnhandledAlertException
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select

abstract class CorpBaseSearchExecutor(driver: IROSDriver, searchOption: CorpSearchOption):
    BaseSearchExecutor(driver, searchOption) {

    protected val initPageHandle: String = driver.getWindowHandle()

    override fun search(): List<String> {

        try {

            if(!setInitConditionForSearch()) {
                return listOf()
            }

            driver.findElementBy(By.id("SANGHO_NUM")).clear()
            driver.findElementBy(By.id("SANGHO_NUM")).sendKeys((searchOption as CorpSearchOption).text)

            return parseResult()

        } catch (e: Exception) {
            println("CorpBaseSearchExecutor setInitConditionForSearch exception : $e")
            return listOf()
        }
    }

    override fun setInitConditionForSearch(): Boolean {

        try {
            driver.findElementBy(By.id("${(searchOption).searchTab}Tab")).click()

            val registryOfficeDropList = driver.findElementBy(By.id("DropList"))
            if (registryOfficeDropList.isEnabled) {
                Select(registryOfficeDropList).selectByIndex(1)
                Select(driver.findElementBy(By.id("SGC_RTVBUBINGB"))).selectByIndex(1)
            }

            driver.findElementBy(By.id("SANGHO_NUM")).clear()
            driver.findElementBy(By.id("SANGHO_NUM")).sendKeys((searchOption as CorpSearchOption).text)

            return true
        } catch (e: Exception) {
            println("CorpBaseSearchExecutor setInitConditionForSearch exception : $e")
            return false
        }
    }

    override fun applyDetailCondition(detailSearchOption: List<String>) {}

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