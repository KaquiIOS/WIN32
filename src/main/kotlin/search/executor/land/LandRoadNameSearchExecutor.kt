package org.example.search.executor.land

import org.example.search.driver.IROSDriver
import org.example.search.option.LandSearchOption.LandRoadNameSearchOption
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.Select

class LandRoadNameSearchExecutor(driver: IROSDriver, searchOption: LandRoadNameSearchOption):
    LandBaseSearchExecutor(driver, searchOption) {

    override fun search(): List<String> {

        if(!setInitConditionForSearch()) {
            return listOf()
        }

        try {
            val castedSearchOption = searchOption as LandRoadNameSearchOption

            Select(driver.findElementBy(By.id("selkindcls"))).selectByIndex(castedSearchOption.gubunIdx!!)
            Select(driver.findElementBy(By.id("e001admin_regn1"))).selectByIndex(castedSearchOption.sidoIdx!!)
            Select(driver.findElementBy(By.id("admin_regn2"))).selectByIndex(castedSearchOption.sigunguIdx!!)
            driver.findElementBy(By.id("e001rd_name")).clear()
            driver.findElementBy(By.id("e001rd_name")).sendKeys(castedSearchOption.roadNm)
            driver.findElementBy(By.id("rd_buld_no")).clear()
            driver.findElementBy(By.id("rd_buld_no")).sendKeys(castedSearchOption.buildingNum)
        } catch (e: Exception) {
            println("LandRoadNameSearchExecutor search exception : $e")
            return listOf()
        }

        applyDetailCondition(listOf("y202cmort_flag", "y202trade_seq_flag", "cls_flag"))

        return parseResult()
    }
}