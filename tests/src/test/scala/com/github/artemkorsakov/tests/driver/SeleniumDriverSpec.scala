package com.github.artemkorsakov.tests.driver

import com.github.artemkorsakov.containers.SeleniumContainerSuite
import com.github.artemkorsakov.query.UpQuery._
import com.github.artemkorsakov.spec.Tags
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.selenium.WebBrowser._

class SeleniumDriverSpec extends AnyFlatSpec with SeleniumContainerSuite with Matchers with Tags {

  "Browser" should "show google" taggedAs healthCheckTest in {
    go to "https://www.google.com/"
    name("q").waitVisible()
  }

  it should "reuse webDriver" taggedAs healthCheckTest in {
    go to "https://www.google.com/"
    name("q").waitVisible()
  }

}
