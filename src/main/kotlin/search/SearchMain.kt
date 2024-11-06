package org.example.search

import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.FluentWait
import org.openqa.selenium.support.ui.Wait
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration


// 1. AnySign.mAnySignEnable 로 페이지 로딩이 되었는지 확인
// 2. 로딩이 될 때 까지 기다리기
// 3. ID/PW 입력
// 4.


fun test() {

    System.setProperty("webdriver.chrome.driver", "C:\\Users\\JongseongWon\\InteliJProjects\\WIN32\\src\\main\\driver\\chromedriver.130.0.6723.69.exe")

    // WebDriver 초기화
    val driver: WebDriver = ChromeDriver()

    try {

        driver.get("http://www.iros.go.kr/PMainJ.jsp")

        //val wait : Wait<WebDriver> = WebDriverWait(driver, Duration.ofSeconds(3))
        val wait: FluentWait<WebDriver> = FluentWait(driver)
            .withTimeout(Duration.ofSeconds(30))
            .pollingEvery(Duration.ofMillis(300L))

        val isAnySignEnabled = wait.until { (it as JavascriptExecutor).executeScript("return window.AnySign && AnySign.mAnySignEnable === true;") as Boolean }

        if (!isAnySignEnabled) {
            println("AnySign 모듈이 활성화 되지 않았습니다.")
            driver.quit()
        }

        val idInputTag = driver.findElement(By.id("id_user_id"))
        idInputTag.sendKeys("kokoxg2")

        val passwordInputTag = driver.findElement(By.id("password"))
        passwordInputTag.sendKeys("Whdtjd1!q")

        // class가 "mt05"인 <li> 태그를 찾기
        val liElement = driver.findElement(By.className("mt05"))

        // <a> 태그 내부의 onclick 속성 실행
        val anchorElement: WebElement = liElement.findElement(By.tagName("a"))

        // JavascriptExecutor를 사용하여 onclick 이벤트 호출
        (driver as JavascriptExecutor).executeScript("arguments[0].click();", anchorElement)

        val login = wait.until { (it as JavascriptExecutor).executeScript("return window.AnySign && AnySign.mAnySignEnable === true;") as Boolean }

        if (!login) {
            println("AnySign 모듈이 활성화 되지 않았습니다.")
            driver.quit()
        }


        driver.quit()

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun main(args: Array<String>) {
    test()
}