package com.github.artemkorsakov.tests.conf

import com.github.artemkorsakov.conf.Browser
import com.github.artemkorsakov.conf.Config.serviceConf
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ConfigSpec extends AnyFunSuite with Matchers {
  test("Config files must be readable") {
    serviceConf.selenium.browser shouldBe Browser.Chrome
    serviceConf.selenium.videoLogs.should(not(be(null)))
    serviceConf.selenium.timeout should be >= 0L
  }

}
