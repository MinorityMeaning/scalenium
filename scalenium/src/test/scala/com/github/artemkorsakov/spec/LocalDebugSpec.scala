package com.github.artemkorsakov.spec

import com.github.artemkorsakov.conf.Browser._
import com.github.artemkorsakov.conf.Config.serviceConf
import com.github.artemkorsakov.conf.{ Browser, Logging }
import com.github.artemkorsakov.driver.SeleniumDriver._
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.safari.SafariDriver
import org.scalatest.{ BeforeAndAfterAll, Suite }

trait LocalDebugSpec extends BeforeAndAfterAll with Logging { self: Suite =>
  def localDriver(browser: Browser): WebDriver =
    browser match {
      case Firefox => new FirefoxDriver(firefoxOptions)
      case Edge    => new EdgeDriver(edgeOptions)
      case IE      => new InternetExplorerDriver(ieOptions)
      case Safari  => new SafariDriver(safariOptions)
      case Chrome  => new ChromeDriver(chromeOptions)
    }

  implicit val webDriver: WebDriver = localDriver(serviceConf.selenium.browser)

  override def afterAll(): Unit = {
    webDriver.close()
    super.afterAll()
  }

}
