package org.example.search.option

import org.example.search.ext.toJsonString

sealed class SearchOption(private val searchOption: Map<String, String>) {

    val searchTab: String = searchOption["searchTab"] ?: ""
    val searchCategory: String = searchOption["searchCategory"] ?: ""

    abstract fun checkValidity(): Boolean

    fun toJson(): String = searchOption.toJsonString()
}

sealed class CorpSearchOption(searchOption: Map<String, String>) : SearchOption(searchOption) {

    val text: String? = searchOption["text"]
    val tabNo: Char? = searchOption["searchCategory"]?.last()

    override fun checkValidity(): Boolean = searchTab.isNotEmpty() && searchCategory.isNotEmpty()

    class CorpNameSearchOption(searchOption: Map<String, String>): CorpSearchOption(searchOption) {
        override fun checkValidity(): Boolean = super.checkValidity()
                && text?.let { it.length < 50 } == true
    }

    class CorpIrosRegNoSearchOption(searchOption: Map<String, String>): CorpSearchOption(searchOption) {
        override fun checkValidity(): Boolean = super.checkValidity()
                && text?.matches(Regex("\\d{6}")) == true
    }

    class CorpRegNoSearchOption(searchOption: Map<String, String>): CorpSearchOption(searchOption) {
        override fun checkValidity(): Boolean = super.checkValidity()
                && text?.matches(Regex("\\d{6}-\\d{7}")) == true
    }
}

sealed class LandSearchOption(searchOption: Map<String, String>): SearchOption(searchOption) {

    val gongdamFlag: String = searchOption["gongdamYn"] ?: "N"
    val salesListFlag: String = searchOption["salesListYn"] ?: "N"
    val closeFlag: String = searchOption["closeYn"] ?: "N"

    override fun checkValidity(): Boolean = searchTab.isNotEmpty() && searchCategory.isNotEmpty()

    class LandSimpleSearchOption(searchOption: Map<String, String>): LandSearchOption(searchOption) {

        val gubunIdx: Int? = searchOption["gubunIdx"]?.toIntOrNull()
        val sidoIdx: Int? = searchOption["sidoIdx"]?.toIntOrNull()
        val juso: String? = searchOption["juso"]

        override fun checkValidity(): Boolean = super.checkValidity()
                && gubunIdx != null
                && sidoIdx != null
                && juso?.let { it.length < 50 } == true
    }

    class LandLocationNumSearchOption(searchOption: Map<String, String>): LandSearchOption(searchOption) {

        val gubunIdx: Int? = searchOption["gubunIdx"]?.toIntOrNull()
        val sidoIdx: Int? = searchOption["sidoIdx"]?.toIntOrNull()
        val lidong: String? = searchOption["lidong"]
        val jibun: String? = searchOption["jibun"]

        override fun checkValidity(): Boolean = super.checkValidity()
                && gubunIdx?.let { it > 0 } == true
                && sidoIdx?.let { it > 0 } == true
                && lidong?.let { it.length < 10 } == true
                && jibun?.let { it.length < 30 } == true
    }

    class LandRoadNameSearchOption(searchOption: Map<String, String>): LandSearchOption(searchOption) {

        val gubunIdx: Int? = searchOption["gubunIdx"]?.toIntOrNull()
        val sidoIdx: Int? = searchOption["sidoIdx"]?.toIntOrNull()
        val sigunguIdx: Int? = searchOption["sigunguIdx"]?.toIntOrNull()
        val roadNm: String? = searchOption["roadNm"]
        val buildingNum: String? = searchOption["buildingNum"]

        override fun checkValidity(): Boolean = super.checkValidity()
                && gubunIdx?.let { it > 0 } == true
                && sidoIdx?.let { it > 0 } == true
                && sigunguIdx?.let { it > 0 } == true
                && roadNm?.let { it.length < 10 } == true
                && buildingNum?.let { it.length < 30 } == true
    }

    class LandUniqueNumSearchOption(searchOption: Map<String, String>): LandSearchOption(searchOption) {

        val uniqueNum: String? = searchOption["uniqueNum"]

        override fun checkValidity(): Boolean = super.checkValidity()
                && uniqueNum?.matches(Regex("\\d{4}-\\d{4}-\\d{6}")) == true
    }
}