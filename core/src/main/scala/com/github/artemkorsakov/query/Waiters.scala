package com.github.artemkorsakov.query

import com.github.artemkorsakov.conf.Config.serviceConf
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait

import java.time.Duration

trait Waiters {
  protected def webDriverWait(implicit driver: WebDriver) =
    new WebDriverWait(driver, Duration.ofSeconds(serviceConf.selenium.timeout))
}
