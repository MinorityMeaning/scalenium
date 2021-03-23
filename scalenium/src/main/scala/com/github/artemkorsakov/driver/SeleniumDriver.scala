package com.github.artemkorsakov.driver

import com.github.artemkorsakov.conf.Browser
import com.github.artemkorsakov.conf.Browser._
import org.openqa.selenium.Capabilities
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.edge.EdgeOptions
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.ie.InternetExplorerOptions
import org.openqa.selenium.safari.SafariOptions

object SeleniumDriver {
  def capabilities(browser: Browser): Capabilities =
    browser match {
      case Firefox => firefoxOptions
      case Edge    => edgeOptions
      case IE      => ieOptions
      case Safari  => safariOptions
      case Chrome  => chromeOptions
    }

  def chromeOptions: ChromeOptions = {
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

  def firefoxOptions: FirefoxOptions = new FirefoxOptions()

  def edgeOptions: EdgeOptions = new EdgeOptions()

  def safariOptions: SafariOptions = new SafariOptions()

  def ieOptions: InternetExplorerOptions = new InternetExplorerOptions()

}
