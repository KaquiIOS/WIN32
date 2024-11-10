package org.example.search.executor.corp

import org.example.search.driver.IROSDriver
import org.example.search.option.CorpSearchOption.CorpRegNoSearchOption

class CorpRegNoSearchExecutor(driver: IROSDriver, searchOption: CorpRegNoSearchOption):
    CorpBaseSearchExecutor(driver, searchOption)