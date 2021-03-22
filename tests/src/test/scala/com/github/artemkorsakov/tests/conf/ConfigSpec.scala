package com.github.artemkorsakov.tests.conf

import com.github.artemkorsakov.conf.Browser
import com.github.artemkorsakov.conf.Config.serviceConf
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ConfigSpec extends AnyFlatSpec with Matchers {

  "Config files" should "be readable" in {
    serviceConf.selenium.browser shouldBe Browser.Chrome
    serviceConf.selenium.videoLogs.should(not(be(null)))
    serviceConf.selenium.timeout should be >= 0L
  }

}
