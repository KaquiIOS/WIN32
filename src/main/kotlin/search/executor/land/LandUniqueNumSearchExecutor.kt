package org.example.search.executor.land

import org.example.search.common.CommonUtil
import org.example.search.driver.IROSDriver
import org.example.search.option.LandSearchOption.LandUniqueNumSearchOption
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions

class LandUniqueNumSearchExecutor(driver: IROSDriver, searchOption: LandUniqueNumSearchOption):
    LandBaseSearchExecutor(driver, searchOption) {

    override fun setInitConditionForSearch(): Boolean {

        try {

            if(!driver.getCurrentUrl().contains("/frontservlet?cmd=RISUWelcomeViewC")) {
                driver.pageMove(searchOption)
                initPageHandle = driver.getWindowHandle()
            }

            if(driver.getFrameName().isEmpty()) {
                // page 구조가 inputFrame > inputFrame 으로 구성되어 있음
                driver.switchToWindow(initPageHandle)
                driver.switchToFrame("inputFrame")
                driver.switchToFrame("inputFrame")
            }

            driver.executeJavaScript("f_goPin_click();return false;")

            return true

        } catch (e: Exception) {
            println("LandUniqueNumSearchExecutor setInitConditionForSearch exception : $e")
            return false
        }
    }

    override fun search(): List<String> {

        if (!setInitConditionForSearch()) {
            return listOf()
        }

        driver.switchToWindow(initPageHandle)

        val castedSearchOption = (searchOption as LandUniqueNumSearchOption)

        val waitResult = driver.waitForAll(listOf(
            ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("inputFrame")),
            ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("resultFrame")),
            ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("frmOuterModal"))
        ))

        if (!waitResult)
            return listOf()

        driver.findElementBy(By.id("inpPinNo")).clear()
        driver.findElementBy(By.id("inpPinNo")).sendKeys(castedSearchOption.uniqueNum)

        applyDetailCondition(listOf("y202cmort_check", "y202trade_check"))

        return parseResult()
    }
}