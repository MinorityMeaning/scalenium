package com.github.artemkorsakov.tests.driver

import com.github.artemkorsakov.query.UpQuery._
import com.github.artemkorsakov.spec._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.selenium.WebBrowser._

class SeleniumDriverSpec extends AnyFlatSpec with SeleniumSpec with Matchers {

  "Browser" should "show google" in {
    go to "https://www.google.com/"
    cssSelector("input[name=q]").waitVisible()
  }

}
