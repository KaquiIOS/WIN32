package org.example.search.common

import org.example.search.const.IROSConst.Companion.CORP_SEARCH_TAB
import org.example.search.const.IROSConst.Companion.LAND_LOCATION_NUM_SEARCH
import org.example.search.const.IROSConst.Companion.LAND_ROAD_NAME_SEARCH
import org.example.search.const.IROSConst.Companion.LAND_SEARCH_TAB
import org.example.search.const.IROSConst.Companion.LAND_SIMPLE_SEARCH
import org.example.search.const.IROSConst.Companion.LAND_UNIQUE_NUM_SEARCH
import org.example.search.option.InitOption
import org.example.search.option.SearchOption

class CommonUtil {
    companion object {

        fun getPageUrl(searchTab: String, searchCategory: String): String? {

            /**
             * - 01 : 소재지번으로 찾기
             * - 02 : 고유번호로 찾기
             * - 03 : 도로명주소로 찾기
             * - 04 : 간편 검색
             */
            val pageUrl: String = when(searchTab) {
                LAND_SEARCH_TAB -> when(searchCategory) {
                    LAND_LOCATION_NUM_SEARCH -> "/frontservlet?cmd=RISUWelcomeViewC&initFlag=Y&MenuID=IR010001&gFlag=N&addrCls=1"
                    LAND_UNIQUE_NUM_SEARCH -> "/frontservlet?cmd=RISUWelcomeViewC"
                    LAND_ROAD_NAME_SEARCH -> "/frontservlet?cmd=RISUWelcomeViewC&initFlag=Y&MenuID=IR010001&gFlag=N&addrCls=2"
                    LAND_SIMPLE_SEARCH -> "/frontservlet?cmd=RISUWelcomeViewC&initFlag=Y&MenuID=IR010001&gFlag=N&addrCls=3"
                    else -> ""
                }
                CORP_SEARCH_TAB -> "/ifrontservlet?cmd=IISUGetCorpFrmCallC"
                else -> ""
            }

            if (pageUrl.isEmpty()) {
                println("PageUrl Set Error")
                return null
            }

            return pageUrl
        }

        fun getPageUrl(initOption: InitOption) = this.getPageUrl(initOption.searchTab, initOption.searchCategory)

        fun getPageUrl(searchOption: SearchOption) = this.getPageUrl(searchOption.searchTab, searchOption.searchCategory)

    }

}