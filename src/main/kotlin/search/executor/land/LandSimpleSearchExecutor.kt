package org.example.search.executor.land

import org.example.search.driver.IROSDriver
import org.example.search.option.LandSearchOption.LandSimpleSearchOption
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.Select

class LandSimpleSearchExecutor(driver: IROSDriver, searchOption: LandSimpleSearchOption):
    LandBaseSearchExecutor(driver, searchOption) {

    override fun search(): List<String> {

        if(!setInitConditionForSearch()) {
            return listOf()
        }

        val castedSearchOption = searchOption as LandSimpleSearchOption

        try {
            Select(driver.findElementBy(By.id("selkindcls"))).selectByIndex(castedSearchOption.gubunIdx!!)
            Select(driver.findElementBy(By.id("e001admin_regn1"))).selectByIndex(castedSearchOption.sidoIdx!!)
            driver.findElementBy(By.id("txt_simple_address")).clear()
            driver.findElementBy(By.id("txt_simple_address")).sendKeys(castedSearchOption.juso)
        } catch (e: Exception) {
            println("LandSimpleSearchExecutor search exception : $e")
            return listOf()
        }

        applyDetailCondition(listOf("y202cmort_flag", "y202trade_seq_flag", "cls_flag"))

        return parseResult()
    }
}