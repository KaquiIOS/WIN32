package org.example.search.driver

import org.example.search.common.CommonUtil
import org.example.search.ext.join
import org.example.search.option.InitOption
import org.example.search.option.SearchOption
import org.openqa.selenium.*
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.chrome.ChromeOptions.LOGGING_PREFS
import org.openqa.selenium.logging.LogType
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.FluentWait
import java.io.File
import java.nio.file.Path
import java.time.Duration
import java.util.logging.Level

class IROSDriver(private val initOption: InitOption) {

    private val driver: WebDriver
    private val waitPolicy: FluentWait<ChromeDriver>

    init {

        // 크롬 드라이버 설정
        System.setProperty(
            "webdriver.chrome.driver",
            File.separator.join(Path.of("").toAbsolutePath().toString(), "resource", "driver", "chromedriver.130.0.6723.69.exe" )
        )

        /**
         * &#91; 빠른 조회를 위한 기본 옵션 설정 &#93;
         *
         * 1. --disable-images : 이미지 로딩 제외
         * 2. --headless=new : 사용자 조작 방지를 위해, Headless 설정
         * 3. --blink-settings=imagesEnabled=false: 이미지 설정 불허
         */
        val options = ChromeOptions().apply {
            addArguments("--disable-images", "--headless=new", "--blink-settings=imagesEnabled=false")
        }

        options.setCapability(LOGGING_PREFS, mapOf(LogType.BROWSER to Level.parse(initOption.logLevel)))

        // WebDriver 초기화
        driver = ChromeDriver(options)

        driver.get("http://www.iros.go.kr/PMainJ.jsp")

        // 대기전략
        waitPolicy = FluentWait(driver)
            .withTimeout(Duration.ofSeconds(5))
            .pollingEvery(Duration.ofMillis(1000L))
    }

    fun getWindowHandle(): String = driver.windowHandle

    fun findElementBy(by: By): WebElement = driver.findElement(by)

    fun findElementsBy(by: By): List<WebElement> = driver.findElements(by)

    fun getAlertMessage(): String = driver.switchTo().alert().text

    fun getCurrentUrl(): String = driver.currentUrl ?: ""

    fun getFrameName(): String {
        return try {
            when(val frameName = executeJavaScript("return window.frameElement").toString()) {
                "null" -> ""
                else -> frameName
            }
        } catch (e: Exception) {
            ""
        }
    }

    fun findElementSafely(selector: By): WebElement? = try {
        driver.findElement(selector)
    } catch (e: NoSuchElementException) {
        null
    } catch (e: Exception) {
        null
    }

    fun acceptAlert(): Boolean {
        try {
            driver.switchTo().alert().accept()
            return true
        } catch (ignored: UnhandledAlertException) {
            return false
        } catch(ignored: NoAlertPresentException) {
            return false
        } catch (ignored: Exception) {
            return false
        }
    }

    fun switchToWindow(handle: String): Boolean {

        try {
            driver.switchTo().window(handle)
        } catch (e: Exception) {
            println("Fail Switch to frame Error : $handle, $e")
            return false
        }

        return true
    }

    fun switchToFrame(frameName: String) : Boolean {

        try {
            driver.switchTo().frame(frameName)
        } catch (e: Exception) {
            println("Fail Switch to frame Error : $frameName, $e")
            return false
        }

        return true
    }

    fun executeJavaScript(query: String): Any? = (driver as JavascriptExecutor).executeScript(query)

    fun pageMove(searchOption: SearchOption) : Boolean {

        val url: String? = CommonUtil.getPageUrl(searchOption)

        if(!url.isNullOrEmpty()) {

            (driver as JavascriptExecutor).executeScript("window.location = 'http://www.iros.go.kr$url'")

            try {
                waitPolicy.until { (it as JavascriptExecutor).executeScript("return window.AnySign && AnySign.mAnySignEnable === true;") }
            } catch (e: Exception) {
                println("pageMove error : $e")
                return  false
            }

            if(!waitForPageLoad()) {
                println("PageLoading 중 문제가 발생하였습니다.")
                driver.quit()
                return false
            }

            waitPolicy.until { ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("inputFrame")) }
            val frameLoaded = waitPolicy.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("inputFrame")))

            if (frameLoaded != null) {
                return true
            }

            println("Frame load fail")
        }

        return false
    }

    fun waitForAll(conditions: List<ExpectedCondition<out SearchContext>>): Boolean =
        try {
            conditions.forEach { waitPolicy.until(it) }
            true
        } catch (e: TimeoutException) {
            false
        } catch (e: UnhandledAlertException) {
            switchToWindow(driver.windowHandle)
            false
        } catch (e: Exception) {
            false
        }

    fun wait(condition: ExpectedCondition<out SearchContext>): Boolean = waitForAll(listOf(condition))

    fun waitForPageLoad(): Boolean {
        return try {
            waitPolicy.until(ExpectedCondition<Boolean> {
                val anySignLoader = driver.findElement(By.id("AnySign4PCLoad"))
                anySignLoader.findElements(By.cssSelector("*")).isEmpty()
            })
        } catch (e: Exception) {
            false
        }
    }


    fun quit() = driver.quit()
}