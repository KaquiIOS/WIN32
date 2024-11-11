package org.example.search

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.example.search.driver.IROSDriver
import org.example.search.executor.factory.SearchExecutorFactory
import org.example.search.option.InitOption
import org.example.search.option.SearchOptionFactory

fun search(initOption: InitOption, searchOptions: List<Map<String, String>>) {

    val irosDriver: IROSDriver = IROSDriver(initOption)

    for (searchOptionMap in searchOptions) {
        val searchOption = SearchOptionFactory.makeSearchOption(searchOptionMap)
        requireNotNull(searchOption)
        if(searchOption.checkValidity()) {
            val searchExecutor = SearchExecutorFactory.getExecutor(irosDriver, searchOption)
            val start = System.currentTimeMillis()
            println(searchExecutor.search())
            println("take ${(System.currentTimeMillis() - start) / 1000}s")
        } else {
            println("SearchOption 오류")
        }
    }
}

fun main() {

    var irosDriver: IROSDriver = IROSDriver(InitOption("01", "01", 10, "ALL"))

    /**
    * - 01 : 소재지번으로 찾기
    * - 02 : 고유번호로 찾기
    * - 03 : 도로명주소로 찾기
    * - 04 : 간편 검색
    */
    var searchOptions: List<Map<String, String>> = listOf(
        mapOf("searchTab" to "01", "searchCategory" to "04", "gubunIdx" to "0", "sidoIdx" to "3", "juso" to "첨단로 7", "gongdamYn" to "Y"),
        mapOf("searchTab" to "01", "searchCategory" to "04", "gubunIdx" to "0", "sidoIdx" to "3", "juso" to "첨단로 7", "gongdamYn" to "Y"),
        mapOf("searchTab" to "01", "searchCategory" to "01", "gubunIdx" to "1", "sidoIdx" to "3", "lidong" to "신서동", "jibun" to "1157", "salesListYn" to "Y"),
        mapOf("searchTab" to "01", "searchCategory" to "03", "gubunIdx" to "1", "sidoIdx" to "3", "sigunguIdx" to "6", "roadNm" to "첨단로", "buildingNum" to "7", "closeYn" to "Y"),
        mapOf("searchTab" to "01", "searchCategory" to "02", "gubunIdx" to "1", "uniqueNum" to "1701-2014-015462", "gongdamYn" to "Y", "salesListYn" to "Y", "closeYn" to "Y"),
        mapOf("searchTab" to "01", "searchCategory" to "02", "gubunIdx" to "1", "uniqueNum" to "1701-2014-015462"),
        mapOf("searchTab" to "01", "searchCategory" to "02", "gubunIdx" to "1", "uniqueNum" to "1701-2014-015462"),
        mapOf("searchTab" to "01", "searchCategory" to "02", "gubunIdx" to "1", "uniqueNum" to "1701-2014-015462"),
        mapOf("searchTab" to "01", "searchCategory" to "02", "gubunIdx" to "1", "uniqueNum" to "1701-2014-015462"),
        mapOf("searchTab" to "01", "searchCategory" to "02", "gubunIdx" to "1", "uniqueNum" to "1701-2014-015462")
    )

    for (searchOptionMap in searchOptions) {
        val searchOption = SearchOptionFactory.makeSearchOption(searchOptionMap)
        requireNotNull(searchOption)
        if(searchOption.checkValidity()) {
            val searchExecutor = SearchExecutorFactory.getExecutor(irosDriver, searchOption)
            val start = System.currentTimeMillis()
            println(searchExecutor.search())
            println("take ${(System.currentTimeMillis() - start) / 1000}s")
        } else {
            println("SearchOption 오류")
        }
    }

    irosDriver.quit()

    // 법인등기 목록 조회 테스트
    irosDriver = IROSDriver(InitOption(
        searchTab = "02", searchCategory = "11", refreshInterval = 10, logLevel = "ALL"
    ))

    searchOptions = listOf(
        // 사업장 조회 옵션 테스트
        mapOf("searchTab" to "02", "searchCategory" to "11", "text" to "신용보증기금"),
        //mapOf("searchTab" to "02", "searchCategory" to "12", "text" to "002172"), // 등기번호는 제외하고 구성하면 좋을듯. 직접 등기소를 선택해서 보는 경우는 드물 것 같음
        mapOf("searchTab" to "02", "searchCategory" to "13", "text" to "114271-0001636")
    )

    for (searchOptionMap in searchOptions) {
        val searchOption = SearchOptionFactory.makeSearchOption(searchOptionMap)
        requireNotNull(searchOption)
        if(searchOption.checkValidity()) {
            val searchExecutor = SearchExecutorFactory.getExecutor(irosDriver, searchOption)
            val start = System.currentTimeMillis()
            println(searchExecutor.search())
            println("take ${(System.currentTimeMillis() - start) / 1000}s")
        } else {
            println("SearchOption 오류")
        }
    }

    irosDriver.quit()
}
