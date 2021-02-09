package com.github.artemkorsakov
package tests

import org.scalatest.matchers.should.Matchers
import org.scalatest.funsuite.AnyFunSuiteLike

class ScaleniumTestSuite extends AnyFunSuiteLike with Matchers {
  test("test Selenium on Scala") {
    1 shouldBe 1
  }
}
