package com.github.artemkorsakov.tests.driver

import com.github.artemkorsakov.containers.BaseContainer
import com.github.artemkorsakov.query.UpQuery._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.selenium.WebBrowser._

class SeleniumDriverSpec extends AnyFlatSpec with BaseContainer with Matchers {

  "Browser" should "show google" in {
    go to "https://www.google.com/"
    cssSelector("input[name=q]").waitVisible()
  }

}
