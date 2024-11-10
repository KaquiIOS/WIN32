package org.example.search.executor.corp

import org.example.search.driver.IROSDriver
import org.example.search.option.CorpSearchOption.CorpIrosRegNoSearchOption

class CorpIrosRegNoSearchExecutor(driver: IROSDriver, searchOption: CorpIrosRegNoSearchOption):
    CorpBaseSearchExecutor(driver, searchOption)