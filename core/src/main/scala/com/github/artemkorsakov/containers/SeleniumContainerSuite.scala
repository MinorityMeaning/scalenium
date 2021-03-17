package com.github.artemkorsakov.containers

import com.dimafeng.testcontainers.ForEachTestContainer
import com.github.artemkorsakov.conf.Config.serviceConf
import com.github.artemkorsakov.conf.Logging
import com.github.artemkorsakov.driver.SeleniumDriver
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalatest.Suite
import org.testcontainers.containers.BrowserWebDriverContainer
import org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode

import java.io.File

trait SeleniumContainerSuite extends ForEachTestContainer with Logging { self: Suite =>

  private val desiredCapabilities: DesiredCapabilities =
    new DesiredCapabilities(SeleniumDriver.capabilities(serviceConf.selenium.browser))

  private val recordingMode: Option[(BrowserWebDriverContainer.VncRecordingMode, File)] = {
    val dir = serviceConf.selenium.videoLogs
    if (dir.isEmpty) None else Some((VncRecordingMode.RECORD_FAILING, new File(dir)))
  }

  val container: SeleniumContainer = SeleniumContainer(desiredCapabilities, recordingMode)

  implicit def webDriver: WebDriver = container.webDriver

}
