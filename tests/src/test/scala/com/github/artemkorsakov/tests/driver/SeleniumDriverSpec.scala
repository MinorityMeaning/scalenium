package com.github.artemkorsakov.tests.driver

import com.github.artemkorsakov.driver.SeleniumDriver
import org.openqa.selenium.support.ui.{ ExpectedConditions, WebDriverWait }
import org.openqa.selenium.{ By, WebDriver }
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import java.time.Duration

class SeleniumDriverSpec extends AnyFunSuite with Matchers {

  test("Check local run") {
    val driver: WebDriver = SeleniumDriver.driver
    driver.get("https://www.google.com/")
    new WebDriverWait(driver, Duration.ofSeconds(10))
      .until(ExpectedConditions.visibilityOfElementLocated(By.name("q")))
    driver.close()
  }

}
