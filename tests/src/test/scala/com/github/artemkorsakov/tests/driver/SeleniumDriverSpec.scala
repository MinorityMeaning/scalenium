package com.github.artemkorsakov.tests.driver

import com.dimafeng.testcontainers.SeleniumTestContainerSuite
import org.openqa.selenium._
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote._
import org.openqa.selenium.support.ui._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.selenium.WebBrowser
import org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode

import java.io.File
import java.time.Duration

class SeleniumDriverSpec extends AnyFlatSpec with SeleniumTestContainerSuite with WebBrowser with Matchers {
  override def desiredCapabilities: DesiredCapabilities = new DesiredCapabilities(new ChromeOptions())
  override def recordingMode: (VncRecordingMode, File)  = (VncRecordingMode.RECORD_ALL, new File("./"))

  "Browser" should "show google" in {
    go to "https://www.google.com/"
    new WebDriverWait(webDriver, Duration.ofSeconds(10))
      .until(ExpectedConditions.visibilityOfElementLocated(By.name("q")))
  }

}
