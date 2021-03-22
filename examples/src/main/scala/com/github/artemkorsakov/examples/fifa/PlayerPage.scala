package com.github.artemkorsakov.examples.fifa

import com.github.artemkorsakov.query.UpQuery._
import com.github.artemkorsakov.query.Waiter
import org.openqa.selenium.WebDriver
import org.scalatestplus.selenium.Page
import org.scalatestplus.selenium.WebBrowser._

case class PlayerPage(url: String)(implicit val webDriver: WebDriver) extends Page with Waiter {
  val profileTab: Query     = xpath("//li[@id='profile']")
  val profileLink: Query    = xpath(s"${profileTab.queryString}/a")
  val citizenshipImg: Query = xpath("//th[.='Citizenship:']/following-sibling::td/img")

  def clickProfile(): Unit =
    if (!profileTab.doesClassContain("aktiv")) {
      clickOn(profileLink)
      val _ = profileTab.waitClassContain("aktiv")
    }

  def citizenship(): Seq[String] =
    findAll(citizenshipImg).flatMap(_.attribute("title")).toSeq

}
