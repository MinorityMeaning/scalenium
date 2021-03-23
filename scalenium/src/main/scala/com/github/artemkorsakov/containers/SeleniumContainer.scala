package com.github.artemkorsakov.containers

import com.dimafeng.testcontainers.SingleContainer
import com.dimafeng.testcontainers.lifecycle.TestLifecycleAware
import com.github.artemkorsakov.conf.Browser.{ Chrome, Firefox }
import com.github.artemkorsakov.conf.Config.serviceConf
import com.github.dockerjava.api.model.Bind
import org.openqa.selenium.remote.{ DesiredCapabilities, RemoteWebDriver }
import org.testcontainers.containers.BrowserWebDriverContainer
import org.testcontainers.containers.wait.strategy._
import org.testcontainers.lifecycle.TestDescription
import org.testcontainers.utility.DockerImageName

import java.io.File
import java.time.Duration
import java.time.temporal.ChronoUnit.SECONDS
import java.util.Optional

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

  override val container: BrowserWebDriverContainer[_] =
    dockerImageName
      .map(new BrowserWebDriverContainer(_))
      .getOrElse(new BrowserWebDriverContainer())

  container.setWaitStrategy(waitStrategy)
  container.withCapabilities(cap)
  recMode.foreach { case (recMode, recDir) => container.withRecordingMode(recMode, recDir) }

  def webDriver: RemoteWebDriver = container.getWebDriver

  override def afterTest(description: TestDescription, throwable: Option[Throwable]): Unit = {
    val javaThrowable: Optional[Throwable] = throwable match {
      case Some(error) => Optional.of(error)
      case None        => Optional.empty()
    }
    container.afterTest(description, javaThrowable)
    container.setBinds(new java.util.ArrayList[Bind]())
  }

}
