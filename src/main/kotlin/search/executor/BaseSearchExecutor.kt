package org.example.search.executor

import org.example.search.driver.IROSDriver
import org.example.search.option.SearchOption

abstract class BaseSearchExecutor(
    protected val driver: IROSDriver,
    protected val searchOption: SearchOption
) {

    protected var initPageHandle: String = driver.getWindowHandle()

    abstract fun search(): List<String>
    abstract fun setInitConditionForSearch(): Boolean
    abstract fun applyDetailCondition(detailSearchOption: List<String>)
    abstract fun parseResult(): List<String>
}