package com.github.artemkorsakov.examples.fifa

import com.github.artemkorsakov.query.Waiter
import org.openqa.selenium.WebDriver
import org.scalatestplus.selenium.Page
import org.scalatestplus.selenium.WebBrowser._
import com.github.artemkorsakov.query.UpQuery._

case class PlayerPage(url: String)(implicit val webDriver: WebDriver) extends Page with Waiter {
  val profileQuery: Query     = xpath("//li[@id='profile']")
  val profileLinkQuery: Query = xpath(s"${profileQuery.queryString}/a")
  val citizenshipQuery: Query = xpath("//th[.='Citizenship:']/following-sibling::td/img")

  def clickProfile(): Unit =
    if (!profileQuery.doesClassContain("aktiv")) {
      clickOn(profileLinkQuery)
      val _ = profileQuery.waitClassContain("aktiv")
    }

  def citizenship(): Seq[String] =
    findAll(citizenshipQuery).flatMap(_.attribute("title")).toSeq

  def waitLoad(): Unit = {
    val _ = profileQuery.waitVisible()
  }

}
