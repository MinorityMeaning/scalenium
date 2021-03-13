package com.github.artemkorsakov.containers

import com.dimafeng.testcontainers.lifecycle.TestLifecycleAware
import com.dimafeng.testcontainers.{ ForEachTestContainer, SingleContainer }
import com.github.artemkorsakov.conf.Browser._
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.{ DesiredCapabilities, RemoteWebDriver }
import org.scalatest.Suite
import org.testcontainers.containers.BrowserWebDriverContainer
import org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode
import org.testcontainers.containers.wait.strategy._
import org.testcontainers.lifecycle.TestDescription
import org.testcontainers.utility.DockerImageName
import com.github.artemkorsakov.conf.Config.serviceConf
import com.github.artemkorsakov.conf.Logging
import com.github.artemkorsakov.driver.SeleniumDriver

import java.io.File
import java.time.Duration
import java.time.temporal.ChronoUnit.SECONDS
import java.util.Optional

trait BaseContainer extends ForEachTestContainer with Logging { self: Suite =>

  def desiredCapabilities: DesiredCapabilities =
    new DesiredCapabilities(SeleniumDriver.capabilities(serviceConf.selenium.browser))

  def recordingMode: Option[(BrowserWebDriverContainer.VncRecordingMode, File)] = {
    val dir = serviceConf.selenium.videoLogs
    if (dir.isEmpty) None else Some((VncRecordingMode.RECORD_FAILING, new File(dir)))
  }

  val container: SeleniumContainer = SeleniumContainer(desiredCapabilities, recordingMode)

  implicit lazy val webDriver: WebDriver = container.webDriver

}

/** <a href='https://github.com/testcontainers/testcontainers-java/issues/3607'>issue</a> */
case class SeleniumContainer(
    cap: DesiredCapabilities,
    recMode: Option[(BrowserWebDriverContainer.VncRecordingMode, File)] = None
) extends SingleContainer[BrowserWebDriverContainer[_]]
    with TestLifecycleAware {
  private val tag = "4.0.0-alpha-7-20201119"

  private val dockerImageName: Option[DockerImageName] =
    serviceConf.selenium.browser match {
      case Chrome  => Some(DockerImageName.parse("selenium/standalone-chrome").withTag(tag))
      case Firefox => Some(DockerImageName.parse("selenium/standalone-firefox").withTag(tag))
      case _       => None
    }

  override val container: BrowserWebDriverContainer[_] =
    dockerImageName
      .map(new BrowserWebDriverContainer(_))
      .getOrElse(new BrowserWebDriverContainer())
  container.withCapabilities(cap)
  recMode.foreach { case (recMode, recDir) => container.withRecordingMode(recMode, recDir) }

  private val logWaitStrategy: WaitStrategy =
    new LogMessageWaitStrategy()
      .withRegEx(
        ".*(RemoteWebDriver instances should connect to|Selenium Server is up and running|.*Started Selenium standalone.*).*\n"
      )
      .withStartupTimeout(Duration.of(15, SECONDS))

  private val waitStrategy = new WaitAllStrategy()
    .withStrategy(logWaitStrategy)
    .withStrategy(new HostPortWaitStrategy)
    .withStartupTimeout(Duration.of(15, SECONDS))

  container.setWaitStrategy(waitStrategy)

  lazy val webDriver: RemoteWebDriver = container.getWebDriver

  override def afterTest(description: TestDescription, throwable: Option[Throwable]): Unit = {
    val javaThrowable: Optional[Throwable] = throwable match {
      case Some(error) => Optional.of(error)
      case None        => Optional.empty()
    }
    container.afterTest(description, javaThrowable)
  }

}
