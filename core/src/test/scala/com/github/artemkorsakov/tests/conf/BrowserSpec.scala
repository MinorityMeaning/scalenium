package com.github.artemkorsakov.tests.conf

import com.github.artemkorsakov.conf.Browser
import com.github.artemkorsakov.conf.Browser._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BrowserSpec extends AnyFlatSpec with Matchers {

  "BrowserFromConf" should "be correct" in {
    Browser.withName("firefox") shouldBe Firefox
    Browser.withName("edge") shouldBe Edge
    Browser.withName("ie") shouldBe IE
    Browser.withName("safari") shouldBe Safari
    Browser.withName("chrome") shouldBe Chrome
  }

}
