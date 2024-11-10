package org.example.search.executor.corp

import org.example.search.driver.IROSDriver
import org.example.search.option.CorpSearchOption.CorpNameSearchOption

class CorpNameSearchExecutor(driver: IROSDriver, searchOption: CorpNameSearchOption):
    CorpBaseSearchExecutor(driver, searchOption)