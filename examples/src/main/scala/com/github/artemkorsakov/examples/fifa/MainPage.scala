package com.github.artemkorsakov.examples.fifa

import com.github.artemkorsakov.query.UpQuery._
import com.github.artemkorsakov.query.Waiter
import org.openqa.selenium.WebDriver
import org.scalatestplus.selenium.Page
import org.scalatestplus.selenium.WebBrowser._

class MainPage(implicit val webDriver: WebDriver) extends Page with Waiter {
  val url                             = "https://www.transfermarkt.com/"
  val competitionsLink: Query         = xpath("//a[normalize-space(.)='Competitions']")
  def groupLink(group: String): Query = xpath(s"//a[normalize-space(.)='UEFA Nations League $group']")

  def goToGroup(group: String): NationsLeagueGroupPage = {
    competitionsLink.waitVisible().click()
    groupLink(group).waitVisible().click()
    val leagueGroupPage = new NationsLeagueGroupPage
    leagueGroupPage.waitLoad(group)
    leagueGroupPage
  }
}
