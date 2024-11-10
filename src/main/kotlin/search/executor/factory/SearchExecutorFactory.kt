package org.example.search.executor.factory

import org.example.search.driver.IROSDriver
import org.example.search.executor.BaseSearchExecutor
import org.example.search.executor.corp.CorpIrosRegNoSearchExecutor
import org.example.search.executor.corp.CorpNameSearchExecutor
import org.example.search.executor.corp.CorpRegNoSearchExecutor
import org.example.search.executor.land.LandLocationNumSearchExecutor
import org.example.search.executor.land.LandRoadNameSearchExecutor
import org.example.search.executor.land.LandSimpleSearchExecutor
import org.example.search.executor.land.LandUniqueNumSearchExecutor
import org.example.search.option.CorpSearchOption
import org.example.search.option.CorpSearchOption.*
import org.example.search.option.LandSearchOption
import org.example.search.option.LandSearchOption.*
import org.example.search.option.SearchOption

class SearchExecutorFactory{

    companion object {

        fun getExecutor(driver: IROSDriver, searchOption: SearchOption) : BaseSearchExecutor =
            when(searchOption) {
                is CorpSearchOption -> {
                    when (searchOption) {
                        is CorpIrosRegNoSearchOption -> CorpIrosRegNoSearchExecutor(driver, searchOption)
                        is CorpNameSearchOption -> CorpNameSearchExecutor(driver, searchOption)
                        is CorpRegNoSearchOption -> CorpRegNoSearchExecutor(driver, searchOption)
                    }
                }

                is LandSearchOption -> {
                    when (searchOption) {
                        is LandLocationNumSearchOption -> LandLocationNumSearchExecutor(driver, searchOption)
                        is LandRoadNameSearchOption -> LandRoadNameSearchExecutor(driver, searchOption)
                        is LandSimpleSearchOption -> LandSimpleSearchExecutor(driver, searchOption)
                        is LandUniqueNumSearchOption -> LandUniqueNumSearchExecutor(driver, searchOption)
                    }
                }
            }
    }
}