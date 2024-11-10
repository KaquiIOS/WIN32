package org.example.search.executor.land

import org.example.search.driver.IROSDriver
import org.example.search.option.LandSearchOption.LandUniqueNumSearchOption
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions

class LandUniqueNumSearchExecutor(driver: IROSDriver, searchOption: LandUniqueNumSearchOption):
    LandBaseSearchExecutor(driver, searchOption) {

    override fun setInitConditionForSearch(): Boolean {
        if (super.setInitConditionForSearch()) {
            try {
                driver.executeJavaScript("f_goPin_click();return true")
                driver.switchToWindow(initPageHandle)
                driver.waitForAll(
                    listOf(
                        ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("resultFrame")),
                        ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("frmOuterModal"))
                    )
                )
                return true

            } catch (e: Exception) {
                println("LandUniqueNumSearchExecutor setInitConditionForSearch exception : $e")
                return false
            }
        }
        return false
    }

    override fun search(): List<String> {

        if (!setInitConditionForSearch()) {
            return listOf()
        }

        val castedSearchOption = (searchOption as LandUniqueNumSearchOption)

        driver.findElementBy(By.id("inpPinNo")).clear()
        driver.findElementBy(By.id("inpPinNo")).sendKeys(castedSearchOption.uniqueNum)

        applyDetailCondition(listOf("y202cmort_check", "y202trade_check"))

        return parseResult()
    }
}