package org.example.search

import org.example.win32.ext.join
import org.openqa.selenium.*
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.chrome.ChromeOptions.LOGGING_PREFS
import org.openqa.selenium.logging.LogType
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.FluentWait
import java.io.File
import java.time.Duration
import java.util.*
import kotlin.NoSuchElementException


// 1. AnySign.mAnySignEnable 로 페이지 로딩이 되었는지 확인
// 2. 로딩이 될 때 까지 기다리기
// 3. ID/PW 입력
// 4.


fun test(corpNo: String) {

    System.setProperty("webdriver.chrome.driver", File.separator.join(System.getProperty("user.dir"), "resource", "driver", "chromedriver.130.0.6723.69.exe" ))

    val options = ChromeOptions()
    options.addArguments("--headless=new")
    options.addArguments("--blink-settings=imagesEnabled=false")
    options.setCapability(LOGGING_PREFS, mapOf(LogType.BROWSER to "ALL"))

    // WebDriver 초기화
    val driver: WebDriver = ChromeDriver(options)

    try {

        // 1. Driver Open
        driver.get("http://www.iros.go.kr/PMainJ.jsp")

        // 2. Wait Until AnySign module load
        val wait: FluentWait<WebDriver> = FluentWait(driver)
            .withTimeout(Duration.ofSeconds(30))
            .pollingEvery(Duration.ofMillis(150L))
            .ignoring(NoSuchElementException::class.java)

        val isAnySignEnabled = wait.until { (it as JavascriptExecutor).executeScript("return window.AnySign && AnySign.mAnySignEnable === true;") as Boolean }

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
        val divElement: WebElement = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.className("list_table")
        ))

        val resultTable = divElement.findElements(By.tagName("table"))

        if (resultTable.size > 1) {
            val searchResult = resultTable.get(1)

            val rows = searchResult.findElements(By.tagName("tr"))

            if (rows.size > 1) {
                for (idx in 1 until rows.size) {
                    val cells = rows[idx].findElements(By.tagName("td"))
                    println(cells.map { it.text })
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        driver.quit()
    }
}

fun main(args: Array<String>) {
    test("114271-0001636")
}