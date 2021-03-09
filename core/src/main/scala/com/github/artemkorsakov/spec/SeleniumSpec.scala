package com.github.artemkorsakov.spec

import com.dimafeng.testcontainers.SeleniumTestContainerSuite
import com.github.artemkorsakov.conf.Config.serviceConf
import com.github.artemkorsakov.conf.Logging
import com.github.artemkorsakov.driver.SeleniumDriver.capabilities
import org.openqa.selenium.remote._
import org.scalatest.Suite
import org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode

import java.io.File

trait SeleniumSpec extends SeleniumTestContainerSuite with Logging { self: Suite =>

  override def desiredCapabilities: DesiredCapabilities = {
    val cap = capabilities(serviceConf.selenium.browser)
    new DesiredCapabilities(cap)
  }

  override def recordingMode: (VncRecordingMode, File) = {
    val dir = serviceConf.selenium.videoLogs
    if (dir.isEmpty) null else (VncRecordingMode.RECORD_FAILING, new File(dir))
  }

}
