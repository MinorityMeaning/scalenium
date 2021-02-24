package com.github.artemkorsakov.tests.conf

import com.github.artemkorsakov.conf.Config.serviceConf
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ConfigSpec extends AnyFunSuite with Matchers {

  test("Config files must be readable") {
    serviceConf.selenium.isRemote.shouldBe(false)
    serviceConf.selenium.hub.shouldNot(be(empty))
    serviceConf.selenium.browser.toLowerCase.shouldEqual("chrome")
    serviceConf.selenium.timeout.shouldBe(30)
  }

}
