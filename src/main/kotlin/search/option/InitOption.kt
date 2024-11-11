package org.example.search.option

/**
 * &#91; InitOption &#93;
 *
 * SearchTab: Int
 * - 01 : 부동산 등기
 * - 02 : 법인 등기
 *
 * SearchCategory: Int
 * - 부동산등기
 * - 01 : 소재지번으로 찾기
 * - 02 : 고유번호로 찾기
 * - 03 : 도로명주소로 찾기
 * - 04 : 간편 검색
 * - 법인등기
 * - 11 : 상호로 찾기
 * - 12 : 등기번호로 찾기
 * - 13 : 등록번호로 찾기
 *
 * refreshInterval: Int
 * - Selenium 유지를 위한 리프레시 타임
 *
 * logLevel: String
 * - 로그 레벨 (아래로 갈 수록 상세로그 기록)
 * - &#34;OFF&#34;
 * - &#34;SEVERE&#34;
 * - &#34;WARNING&#34;
 * - &#34;INFO&#34;
 * - &#34;CONFIG&#34;
 * - &#34;FINE&#34;
 * - &#34;FINER&#34;
 * - &#34;FINEST&#34;
 * - &#34;ALL&#34;
 */
data class InitOption(
    val searchTab: String,
    val searchCategory: String,
    val refreshInterval: Int = 10,
    val logLevel: String = "ALL"
)