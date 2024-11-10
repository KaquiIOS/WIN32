package org.example.search.option

import org.example.search.const.IROSConst.Companion.CORP_IROS_REG_NUM_SEARCH
import org.example.search.const.IROSConst.Companion.CORP_NAME_SEARCH
import org.example.search.const.IROSConst.Companion.CORP_REG_NUM_SEARCH
import java.util.logging.Logger
import org.example.search.option.LandSearchOption.*
import org.example.search.option.CorpSearchOption.*
import org.example.search.const.IROSConst.Companion.CORP_SEARCH_TAB
import org.example.search.const.IROSConst.Companion.LAND_LOCATION_NUM_SEARCH
import org.example.search.const.IROSConst.Companion.LAND_ROAD_NAME_SEARCH
import org.example.search.const.IROSConst.Companion.LAND_SEARCH_TAB
import org.example.search.const.IROSConst.Companion.LAND_SIMPLE_SEARCH
import org.example.search.const.IROSConst.Companion.LAND_UNIQUE_NUM_SEARCH

class SearchOptionFactory {

    private val logger: Logger = Logger.getLogger("SearchOptionFactory")

    companion object {

        private val essProperties: List<String> = listOf("searchTab", "searchCategory")

        fun makeSearchOption(searchOption: Map<String, String>): SearchOption? {

            val hasEssProperties = essProperties.all { searchOption.contains(it) }

            if (!hasEssProperties) {
                println("Essential Properties not found")
                return null
            }

            return when(searchOption["searchTab"]) {
                LAND_SEARCH_TAB -> when(searchOption["searchCategory"]) {
                    LAND_LOCATION_NUM_SEARCH -> LandLocationNumSearchOption(searchOption) // 소재지번으로 찾기
                    LAND_UNIQUE_NUM_SEARCH -> LandUniqueNumSearchOption(searchOption) // 고유번호로 찾기
                    LAND_ROAD_NAME_SEARCH -> LandRoadNameSearchOption(searchOption) // 도로명주소로 찾기
                    LAND_SIMPLE_SEARCH -> LandSimpleSearchOption(searchOption) // 간편 검색
                    else -> null
                }
                CORP_SEARCH_TAB -> when(searchOption["searchCategory"]) {
                    CORP_NAME_SEARCH -> CorpNameSearchOption(searchOption) // 상호로 찾기
                    CORP_IROS_REG_NUM_SEARCH -> CorpIrosRegNoSearchOption(searchOption) // 등기번호로 찾기
                    CORP_REG_NUM_SEARCH -> CorpRegNoSearchOption(searchOption) // 법인등록번호로 찾기
                    else -> null
                }
                else -> null
            }
        }
    }
}