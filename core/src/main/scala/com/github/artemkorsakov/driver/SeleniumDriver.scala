package com.github.artemkorsakov.driver

import com.github.artemkorsakov.conf.Config.serviceConf
import org.openqa.selenium.chrome.{ ChromeDriver, ChromeOptions }
import org.openqa.selenium.edge.{ EdgeDriver, EdgeOptions }
import org.openqa.selenium.firefox.{ FirefoxDriver, FirefoxOptions }
import org.openqa.selenium.ie.{ InternetExplorerDriver, InternetExplorerOptions }
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.safari.{ SafariDriver, SafariOptions }
import org.openqa.selenium.{ Capabilities, WebDriver }

import java.net.URL

object SeleniumDriver {
  private val hub: String       = serviceConf.selenium.hub
  private val hubURL: URL       = new URL(hub)
  private val isRemote: Boolean = serviceConf.selenium.isRemote
  private val browser: String   = serviceConf.selenium.browser

  def driver: WebDriver = if (isRemote) remoteDriver else localDriver

  def remoteDriver: WebDriver = new RemoteWebDriver(hubURL, capabilities)

  def localDriver: WebDriver =
    browser.toLowerCase match {
      case "firefox" => new FirefoxDriver(firefoxOptions)
      case "edge"    => new EdgeDriver(edgeOptions)
      case "ie"      => new InternetExplorerDriver(ieOptions)
      case "safari"  => new SafariDriver(safariOptions)
      case "chrome"  => new ChromeDriver(chromeOptions)
      case _         => throw new IllegalArgumentException(s"Invalid browser: $browser")
    }

  private def capabilities: Capabilities =
    browser.toLowerCase match {
      case "firefox" => firefoxOptions
      case "edge"    => edgeOptions
      case "ie"      => ieOptions
      case "safari"  => safariOptions
      case "chrome"  => chromeOptions
      case _         => throw new IllegalArgumentException(s"Invalid browser: $browser")
    }

  private def chromeOptions: ChromeOptions = {
    val options = new ChromeOptions
    // Docker has no monitor so we set a fixed size
    options.addArguments("--window-size=1920,1080")
    options.addArguments("--no-sandbox")
    options.addArguments("--incognito")
    options.addArguments("--disable-notifications")
    // To avoid browser crash. More details: https://github.com/elgalu/docker-selenium/issues/20
    options.addArguments("--disable-dev-shm-usage")
    options.addArguments("--disable-extensions")
    options.addArguments("--disable-gpu")
    options.addArguments("ignore-certificate-errors")
    options
  }

  private def firefoxOptions: FirefoxOptions = new FirefoxOptions()

  private def edgeOptions: EdgeOptions = new EdgeOptions()

  private def safariOptions: SafariOptions = new SafariOptions()

  private def ieOptions: InternetExplorerOptions = new InternetExplorerOptions()

}
