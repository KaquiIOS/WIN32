package org.example.search

import org.example.win32.ext.join
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.chrome.ChromeOptions.LOGGING_PREFS
import org.openqa.selenium.logging.LogType
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.FluentWait
import java.io.File
import java.time.Duration
import java.util.logging.Level


fun test(corpNoLst: List<String>) {

    var startTime = System.currentTimeMillis()

    System.setProperty("webdriver.chrome.driver", File.separator.join(System.getProperty("user.dir"), "resource", "driver", "chromedriver.130.0.6723.69.exe" ))

    val options = ChromeOptions().apply {
        setExperimentalOption("prefs", "profile.managed_default_content_settings.images" to 2)
        addArguments("--disable-images", "--headless=new", "--blink-settings=imagesEnabled=false")
    }

    options.setCapability(LOGGING_PREFS, mapOf(LogType.BROWSER to Level.ALL))

    // WebDriver 초기화
    val driver: WebDriver = ChromeDriver(options)

    try {

        // 1. Driver Open
        driver.get("http://www.iros.go.kr/PMainJ.jsp")

         // 2. Wait Until AnySign module load
        val wait: FluentWait<WebDriver> = FluentWait(driver)
            .withTimeout(Duration.ofSeconds(5))
            .pollingEvery(Duration.ofMillis(1000L))
            .ignoring(NoSuchElementException::class.java)

        var isAnySignEnabled = wait.until { (it as JavascriptExecutor).executeScript("return window.AnySign && AnySign.mAnySignEnable === true;") as Boolean }

        if (!isAnySignEnabled) {
            println("AnySign 모듈이 활성화 되지 않았습니다.")
            driver.quit()
        }

        // 3. switch to select pagehttp://www.iros.go.kr/PMainJ.jsp
        (driver as JavascriptExecutor).executeScript("window.location = '/ifrontservlet?cmd=IISUGetCorpFrmCallC';")

        // 로그에 `AnySign_onmessage_01004` 메시지가 나타날 때까지 대기
        val foundMessage = wait.until {
            // BROWSER 로그 가져오기
            val logs = driver.manage().logs().get(LogType.BROWSER)

            // 로그 중 `AnySign_onmessage_01004`가 포함된 메시지 찾기
            logs.all.any { logEntry ->
                logEntry.message.contains("AnySign_executeDecCallback")
            }
        }

        if (!foundMessage) {
            println("PageLoading 중 문제가 발생하였습니다.")
            driver.quit()
        }

        // 4. search by corp no
        val topHandle = driver.windowHandle

        // 최초 1회 초기화가 되어있으면 그 이후부턴 빠를 것 아닌가?
        for(corpNo in corpNoLst) {

            val startTime2 = System.currentTimeMillis()

            // move to inputFrame
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("inputFrame")))
            driver.findElement(By.id("3Tab")).click()
            driver.findElement(By.id("SANGHO_NUM")).sendKeys(corpNo)
            driver.findElement(By.className("sbtn_bg02_action")).click()

            // move to top
            driver.switchTo().window(topHandle)

            // move to resultFrame
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("resultFrame")))
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("frmOuterModal")))

            // 6. parse data
            // CSS 선택자로 table 요소가 로드될 때까지 대기
            val resultTable: WebElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".list_table table:nth-of-type(2)")
            ))

            val result: List<String> = resultTable.findElements(By.tagName("tr")).drop(1).map { it.text }

            println("$corpNo : ${(System.currentTimeMillis() - startTime2) / 1000}s")
            println(result)

            // move to top
            driver.switchTo().window(topHandle)
        }

    } catch (e: Exception) {
        e.printStackTrace()
    } finally {

        println("total : ${(System.currentTimeMillis() - startTime) / 1000}s")

        driver.quit()
    }
}

fun main(args: Array<String>) {
    test(listOf("114271-0001636", "200111-0018882"))
}
