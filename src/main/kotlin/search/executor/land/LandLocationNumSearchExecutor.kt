package org.example.search.executor.land

import org.example.search.driver.IROSDriver
import org.example.search.option.LandSearchOption.LandLocationNumSearchOption
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.Select

class LandLocationNumSearchExecutor(driver: IROSDriver, searchOption: LandLocationNumSearchOption):
    LandBaseSearchExecutor(driver, searchOption) {

    override fun search(): List<String> {

        if(!setInitConditionForSearch()) {
            return listOf()
        }

        try {

            val castedSearchOption = searchOption as LandLocationNumSearchOption

            Select(driver.findElementBy(By.id("selkindcls"))).selectByIndex((castedSearchOption.gubunIdx!!))
            Select(driver.findElementBy(By.id("e001admin_regn1"))).selectByIndex(castedSearchOption.sidoIdx!!)
            driver.findElementBy(By.id("e001admin_regn3")).sendKeys(castedSearchOption.lidong)
            driver.findElementBy(By.id("a312lot_no")).sendKeys(castedSearchOption.jibun)

        } catch (e: Exception) {
            println("LandLocationNumSearchExecutor search exception : $e")
        }

        applyDetailCondition(listOf("y202cmort_flag", "y202trade_seq_flag", "cls_flag"))

        return parseResult()
    }
}