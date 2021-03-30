package com.github.artemkorsakov.examples.tests

import com.github.artemkorsakov.examples.fifa._
import com.github.artemkorsakov.spec.LocalDebugSpec
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.selenium.WebBrowser._

class NationsLeagueSpec extends AnyFlatSpec with LocalDebugSpec with Matchers {

  "NationsLeagueSpec" should "make a url list of national teams" in {
    val mainPage = new MainPage
    go to mainPage

    Seq("A", "B", "C", "D").foreach(group => {
      val leagueGroupPage = mainPage.goToGroup(group)
      val groupResult     = leagueGroupPage.results
      log.info(s"Group $group")
      groupResult.foreach { case (i, str) => log.info(s"$i - $str") }
    })
  }

}
