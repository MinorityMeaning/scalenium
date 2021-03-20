package com.github.artemkorsakov.tests.conf

import com.github.artemkorsakov.conf.Browser
import com.github.artemkorsakov.conf.Config.serviceConf
import com.github.artemkorsakov.spec.Tags
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ConfigSpec extends AnyFlatSpec with Matchers with Tags {

  "Config files" should "be readable" taggedAs healthCheckTest in {
    serviceConf.selenium.browser shouldBe Browser.Chrome
    serviceConf.selenium.videoLogs.should(not(be(null)))
    serviceConf.selenium.timeout should be >= 0L
  }

}
