package com.github.artemkorsakov.tests.conf

import com.github.artemkorsakov.conf.Browser
import com.github.artemkorsakov.conf.Browser._
import com.github.artemkorsakov.spec.Tags
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BrowserSpec extends AnyFlatSpec with Matchers with Tags {

  "BrowserFromConf" should "be correct" taggedAs healthCheckTest in {
    Browser.withName("firefox") shouldBe Firefox
    Browser.withName("edge") shouldBe Edge
    Browser.withName("ie") shouldBe IE
    Browser.withName("safari") shouldBe Safari
    Browser.withName("chrome") shouldBe Chrome
  }

}
